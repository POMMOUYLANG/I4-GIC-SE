package edu.itc.se.lab01.challenge;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.Stream;

/**
 * ============================================================================
 * Challenge: Parallel Word-Frequency Analyser
 * ============================================================================
 *
 * SCENARIO & ARCHITECTURE:
 * High-throughput parallel text scanner and word-frequency counter designed
 * for large multi-file datasets (e.g., server logs, e-books).
 *
 * 1. PRODUCER-CONSUMER PIPELINE (BACK-PRESSURE):
 *    - A dedicated scanner platform thread walks the target directory.
 *    - Pushes discovered file paths into a bounded BlockingQueue<Path> (capacity 16).
 *    - Bounded queue prevents unbounded heap consumption and throttles the producer
 *      when consumers are busy (back-pressure).
 *    - Signals completion to workers using a poison pill sentinel (POISON = Path.of("__POISON__")).
 *
 * 2. WORKER POOL & CONCURRENT ACCUMULATION:
 *    - N worker threads (default = available cores, or virtual threads with --virtual).
 *    - Each worker consumes paths, reads files line-by-line, splits on non-letters,
 *      converts to lowercase, and updates a shared ConcurrentHashMap<String, LongAdder>.
 *    - LongAdder eliminates CAS-loop contention across CPU cores via internal cell striping
 *      (Striped64), drastically outperforming synchronized blocks and AtomicLong.
 *
 * 3. NON-BLOCKING LIVE PROGRESS REPORTER:
 *    - A scheduled background thread fires every 500 ms.
 *    - Prints: [time] files done/total, words counted, throughput (words/s), top 3 words.
 *    - Never blocks worker threads: reads atomic counters (filesDone, wordsDone) and
 *      samples ConcurrentHashMap lock-free via a size-3 min-heap.
 *
 * 4. COOPERATIVE CANCELLATION & GRACEFUL SHUTDOWN:
 *    - Shutdown is triggered via JVM shutdown hook (Ctrl+C) or deadline (--timeout SECONDS).
 *    - The shutdown() method is guarded by an AtomicBoolean stopping flag to ensure idempotency.
 *    - Interrupts the scanner thread and worker thread pool (shutdownNow()).
 *    - Waits at most 2 seconds for worker threads to drain/exit cleanly.
 *    - Prints a partial result clearly marked [PARTIAL] and writes result.csv.
 *    - Workers check the interrupt flag between files and restore interrupt status if take() throws.
 *
 * 5. VERIFICATION & CSV REPORTING:
 *    - Outputs formatted top N word frequencies, counts, percentage shares, and execution time.
 *    - Writes output to result.csv.
 *    - Includes a single-threaded reference implementation for verification and JUnit testing.
 */
public class Challenge {

    // Bounded queue with capacity 16 to provide back-pressure on the directory scanner
    private final BlockingQueue<Path> queue = new ArrayBlockingQueue<>(16);

    // Shared thread-safe frequency map using LongAdder for lock-free striped increments
    private final ConcurrentHashMap<String, LongAdder> counts = new ConcurrentHashMap<>();

    // Atomic progress counters
    private final AtomicInteger filesDone = new AtomicInteger();
    private final LongAdder wordsDone = new LongAdder();
    private final AtomicBoolean stopping = new AtomicBoolean(false);
    private final AtomicInteger totalFiles = new AtomicInteger(0);

    // Sentinel poison pill object used to signal end of work to worker threads
    private static final Path POISON = Path.of("__POISON__");

    // Pipeline configuration parameters
    private final Path dir;
    private final int workers;
    private final boolean virtual;
    private final int top;
    private final long timeoutSec;

    // Execution state and threading references
    private volatile long startTimeNanos;
    private volatile long endTimeNanos;
    private volatile boolean isPartialResult = false;
    private Thread scannerThread;
    private ExecutorService pool;
    private ScheduledExecutorService reporter;
    private Thread shutdownHook;

    /**
     * Constructs a Parallel Word-Frequency Analyser.
     *
     * @param dir        Target directory containing text files to process
     * @param workers    Number of worker threads (default = available processor cores)
     * @param virtual    true to use virtual threads; false for platform threads
     * @param top        Number of top frequent words to report (default 10)
     * @param timeoutSec Maximum execution time in seconds (0 or negative for no timeout)
     */
    public Challenge(Path dir, int workers, boolean virtual, int top, long timeoutSec) {
        this.dir = Objects.requireNonNull(dir, "dir must not be null");
        this.workers = workers > 0 ? workers : Runtime.getRuntime().availableProcessors();
        this.virtual = virtual;
        this.top = top > 0 ? top : 10;
        this.timeoutSec = timeoutSec;
    }

    /**
     * Executes the parallel word-frequency analysis pipeline.
     *
     * @throws InterruptedException if interrupted while awaiting pipeline completion
     */
    public void run() throws InterruptedException {
        startTimeNanos = System.nanoTime();

        // 1. Pre-calculate total file count so the live reporter displays total files (e.g. 37/200)
        int discoveredFiles = 0;
        try (Stream<Path> stream = Files.walk(dir)) {
            discoveredFiles = (int) stream.filter(Files::isRegularFile).count();
        } catch (IOException e) {
            System.err.println("Error reading directory: " + e.getMessage());
            return;
        }
        totalFiles.set(discoveredFiles);

        // 2. Instantiate thread pool (Virtual thread per task OR Fixed platform thread pool)
        pool = virtual ? Executors.newVirtualThreadPerTaskExecutor()
                       : Executors.newFixedThreadPool(workers);

        // 3. Launch the directory scanner on a dedicated platform thread
        scannerThread = Thread.ofPlatform().name("scanner").start(this::scan);

        // 4. Launch the live progress reporter (every 500 ms)
        reporter = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "reporter");
            t.setDaemon(true);
            return t;
        });
        reporter.scheduleAtFixedRate(this::report, 500, 500, TimeUnit.MILLISECONDS);

        // 5. Register JVM shutdown hook for Ctrl+C interception
        shutdownHook = new Thread(this::shutdown, "shutdown-hook");
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        // 6. Submit worker tasks and track completion via CountDownLatch
        CountDownLatch latch = new CountDownLatch(workers);
        for (int i = 0; i < workers; i++) {
            pool.submit(() -> {
                try {
                    work();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 7. Enforce deadline timeout or await normal completion
        boolean completed = false;
        if (timeoutSec > 0) {
            completed = latch.await(timeoutSec, TimeUnit.SECONDS);
            if (!completed) {
                // Timeout deadline reached
                long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeNanos);
                double elapsedSec = elapsedMillis / 1000.0;
                System.out.printf(Locale.US, "[%.1fs] deadline reached - stopping%n", elapsedSec);
                shutdown();
                return;
            }
        } else {
            latch.await();
            completed = true;
        }

        // 8. Normal termination cleanup
        if (completed && !stopping.get()) {
            endTimeNanos = System.nanoTime();

            // Stop reporter
            reporter.shutdown();
            reporter.awaitTermination(1, TimeUnit.SECONDS);

            // Safely deregister shutdown hook as pipeline finished normally
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (IllegalStateException ignored) {
                // JVM may already be shutting down
            }

            // Shutdown worker pool
            pool.shutdown();
            pool.awaitTermination(2, TimeUnit.SECONDS);

            // Print final DONE results and generate result.csv
            printResult(false);
        }
    }

    /**
     * Scanner thread: walks the directory tree and puts regular file paths into the bounded queue.
     * Back-pressure is applied naturally when the queue capacity (16) is reached.
     * Finally, puts 'workers' poison pill sentinels into the queue to signal worker termination.
     */
    private void scan() {
        try (Stream<Path> stream = Files.walk(dir)) {
            Iterator<Path> it = stream.filter(Files::isRegularFile).iterator();
            while (it.hasNext() && !Thread.currentThread().isInterrupted() && !stopping.get()) {
                queue.put(it.next());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println("Scanner I/O error: " + e.getMessage());
        } finally {
            // Signal every worker to terminate by placing one POISON pill per worker
            for (int i = 0; i < workers; i++) {
                try {
                    queue.put(POISON);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * Worker thread execution loop: takes file paths from the bounded queue, checks for the
     * POISON sentinel, processes words in the file, and updates progress counters.
     * Cooperatively checks the interruption flag between files and restores it if take() throws.
     */
    private void work() {
        try {
            while (!Thread.currentThread().isInterrupted() && !stopping.get()) {
                Path p = queue.take();
                if (p == POISON) {
                    return; // Normal completion sentinel
                }
                countFile(p);
                filesDone.incrementAndGet();
            }
        } catch (InterruptedException e) {
            // Restore interrupted status upon InterruptedException as required
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Reads a single file line by line, splits each line on non-letter delimiters,
     * normalizes to lowercase, and increments the shared ConcurrentHashMap count.
     *
     * @param p Path to the text file
     */
    private void countFile(Path p) {
        try (BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check cooperative cancellation between lines
                if (Thread.currentThread().isInterrupted() || stopping.get()) {
                    return;
                }

                int lineWords = 0;
                int len = line.length();
                int start = -1;

                // High-performance character-based tokenization (avoids regex overhead)
                for (int i = 0; i < len; i++) {
                    char c = line.charAt(i);
                    if (Character.isLetter(c)) {
                        if (start < 0) {
                            start = i;
                        }
                    } else {
                        if (start >= 0) {
                            String word = line.substring(start, i).toLowerCase(Locale.ROOT);
                            counts.computeIfAbsent(word, k -> new LongAdder()).increment();
                            lineWords++;
                            start = -1;
                        }
                    }
                }
                // Handle word extending to the end of the line
                if (start >= 0) {
                    String word = line.substring(start, len).toLowerCase(Locale.ROOT);
                    counts.computeIfAbsent(word, k -> new LongAdder()).increment();
                    lineWords++;
                }

                if (lineWords > 0) {
                    wordsDone.add(lineWords);
                }
            }
        } catch (IOException e) {
            // Silently skip unreadable or deleted files in robust pipeline
        }
    }

    /**
     * Scheduled reporter: prints progress every 500 ms without blocking worker threads.
     * Reads atomic counters directly and extracts current top 3 words lock-free.
     */
    private void report() {
        if (stopping.get()) return;

        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeNanos);
        double elapsedSec = elapsedMillis / 1000.0;
        int done = filesDone.get();
        int total = totalFiles.get();
        long words = wordsDone.sum();
        long throughput = elapsedSec > 0 ? Math.round(words / elapsedSec) : 0;

        List<Map.Entry<String, Long>> top3 = getTopWords(3);
        StringBuilder topSb = new StringBuilder("top: ");
        for (int i = 0; i < top3.size(); i++) {
            Map.Entry<String, Long> entry = top3.get(i);
            if (i > 0) topSb.append(" ");
            topSb.append(entry.getKey()).append(String.format(Locale.US, "(%,d)", entry.getValue()));
        }

        System.out.printf(Locale.US, "[%.1fs] files %d/%d words %,d %,d w/s %s%n",
                elapsedSec, done, total, words, throughput, topSb.toString());
    }

    /**
     * Idempotent shutdown procedure triggered either by timeout deadline or Ctrl+C shutdown hook.
     * Interrupts scanner and worker threads, waits at most 2 seconds, and prints partial results.
     */
    public void shutdown() {
        if (!stopping.compareAndSet(false, true)) {
            return; // Already shut down or in progress
        }
        isPartialResult = true;
        endTimeNanos = System.nanoTime();

        // 1. Interrupt scanner thread
        if (scannerThread != null && scannerThread.isAlive()) {
            scannerThread.interrupt();
        }

        // 2. Stop and interrupt all workers
        if (pool != null) {
            pool.shutdownNow();
        }

        // 3. Stop progress reporter
        if (reporter != null) {
            reporter.shutdownNow();
        }

        // 4. Wait at most 2 seconds for workers to terminate cleanly
        try {
            if (pool != null) {
                pool.awaitTermination(2, TimeUnit.SECONDS);
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        // 5. Output partial results
        printResult(true);
    }

    /**
     * Retrieves the top K most frequent words lock-free using a bounded min-heap.
     * Iterating ConcurrentHashMap.entrySet() provides weakly-consistent, non-blocking traversal.
     *
     * @param k Number of top words to retrieve
     * @return List of top K entries sorted descending by frequency
     */
    private List<Map.Entry<String, Long>> getTopWords(int k) {
        if (counts.isEmpty() || k <= 0) {
            return Collections.emptyList();
        }

        // Min-heap ordered by count ascending: stores the top K largest entries
        PriorityQueue<Map.Entry<String, Long>> minHeap = new PriorityQueue<>(
                Comparator.comparingLong(Map.Entry::getValue)
        );

        for (Map.Entry<String, LongAdder> entry : counts.entrySet()) {
            long val = entry.getValue().sum();
            if (val <= 0) continue;

            Map.Entry<String, Long> item = Map.entry(entry.getKey(), val);
            if (minHeap.size() < k) {
                minHeap.offer(item);
            } else if (val > minHeap.peek().getValue()) {
                minHeap.poll();
                minHeap.offer(item);
            }
        }

        List<Map.Entry<String, Long>> result = new ArrayList<>(minHeap);
        result.sort(Comparator.comparingLong((Map.Entry<String, Long> e) -> e.getValue()).reversed());
        return result;
    }

    /**
     * Prints the final (or partial) frequency table and writes result.csv.
     *
     * @param isPartial true if execution was interrupted/timed out before completing all files
     */
    private void printResult(boolean isPartial) {
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(
                (endTimeNanos > 0 ? endTimeNanos : System.nanoTime()) - startTimeNanos);
        double elapsedSec = elapsedMillis / 1000.0;
        int files = filesDone.get();
        long totalWords = wordsDone.sum();
        String modeStr = virtual ? "virtual" : ("platform(" + workers + ")");

        if (isPartial) {
            System.out.printf(Locale.US, "[PARTIAL] files=%d words=%,d elapsed=%.2fs mode=%s%n",
                    files, totalWords, elapsedSec, modeStr);
        } else {
            System.out.printf(Locale.US, "DONE files=%d words=%,d elapsed=%.2fs mode=%s%n",
                    files, totalWords, elapsedSec, modeStr);
        }

        System.out.println(" rank word count share");
        List<Map.Entry<String, Long>> topEntries = getTopWords(top);
        int rank = 1;
        for (Map.Entry<String, Long> entry : topEntries) {
            double share = totalWords > 0 ? (entry.getValue() * 100.0 / totalWords) : 0.0;
            System.out.printf(Locale.US, "%5d %-10s %10s %5.2f %%%n",
                    rank++, entry.getKey(), String.format(Locale.US, "%,d", entry.getValue()), share);
        }

        writeCsv(topEntries, totalWords);
        System.out.println("result.csv written");
    }

    /**
     * Exports the top words and frequency statistics to result.csv.
     */
    private void writeCsv(List<Map.Entry<String, Long>> topEntries, long totalWords) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of("result.csv"), StandardCharsets.UTF_8)) {
            writer.write("rank,word,count,share\n");
            int rank = 1;
            for (Map.Entry<String, Long> entry : topEntries) {
                double share = totalWords > 0 ? (entry.getValue() * 100.0 / totalWords) : 0.0;
                writer.write(String.format(Locale.US, "%d,%s,%d,%.2f%%\n",
                        rank++, entry.getKey(), entry.getValue(), share));
            }
        } catch (IOException e) {
            System.err.println("Error writing result.csv: " + e.getMessage());
        }
    }

    /**
     * Converts the internal ConcurrentHashMap<String, LongAdder> to Map<String, Long>
     * for verification and test assertions.
     *
     * @return Map containing all counted words and their total frequencies
     */
    public Map<String, Long> getCountsMap() {
        Map<String, Long> result = new HashMap<>();
        counts.forEach((k, v) -> {
            long sum = v.sum();
            if (sum > 0) result.put(k, sum);
        });
        return result;
    }

    /**
     * @return true if the pipeline produced a partial result due to timeout or interruption
     */
    public boolean isPartial() {
        return isPartialResult;
    }

    /**
     * @return true if cancellation / shutdown has been requested
     */
    public boolean isStopping() {
        return stopping.get();
    }

    /**
     * Single-threaded reference implementation for verification and JUnit test assertions.
     * Iterates all files sequentially in a single thread, counts word occurrences,
     * and returns the exact frequency map.
     *
     * @param dir Directory containing files to count
     * @return Map of word to total occurrence count
     * @throws IOException if directory or file read fails
     */
    public static Map<String, Long> countSingleThreaded(Path dir) throws IOException {
        Map<String, Long> map = new HashMap<>();
        try (Stream<Path> stream = Files.walk(dir)) {
            List<Path> files = stream.filter(Files::isRegularFile).sorted().toList();
            for (Path file : files) {
                try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int len = line.length();
                        int start = -1;
                        for (int i = 0; i < len; i++) {
                            char c = line.charAt(i);
                            if (Character.isLetter(c)) {
                                if (start < 0) start = i;
                            } else {
                                if (start >= 0) {
                                    String word = line.substring(start, i).toLowerCase(Locale.ROOT);
                                    map.merge(word, 1L, Long::sum);
                                    start = -1;
                                }
                            }
                        }
                        if (start >= 0) {
                            String word = line.substring(start, len).toLowerCase(Locale.ROOT);
                            map.merge(word, 1L, Long::sum);
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * Test data generator helper: writes 'numFiles' text files with approximately 'wordsPerFile'
     * random words sampled from a fixed vocabulary of 2,000 words.
     *
     * Uses a Zipf-like distribution so common words (e.g. "the", "and", "of") appear with realistic
     * natural language frequencies matching real-world corpora.
     *
     * @param targetDir    Output directory where files are written
     * @param numFiles     Number of text files to create (e.g. 200)
     * @param wordsPerFile Target number of words per file (e.g. ~50,000)
     * @throws IOException if file creation fails
     */
    public static void generateTestData(Path targetDir, int numFiles, int wordsPerFile) throws IOException {
        Files.createDirectories(targetDir);

        // Fixed vocabulary of 2,000 words
        String[] vocab = buildVocabulary(2000);

        // Precompute cumulative Zipf distribution weights (s = 0.85)
        double s = 0.85;
        double[] cdf = new double[vocab.length];
        double sum = 0.0;
        for (int i = 0; i < vocab.length; i++) {
            sum += 1.0 / Math.pow(i + 1, s);
            cdf[i] = sum;
        }
        for (int i = 0; i < cdf.length; i++) {
            cdf[i] /= sum;
        }

        // Generate files in parallel for maximum generation speed
        int parallelism = Runtime.getRuntime().availableProcessors();
        ExecutorService genPool = Executors.newFixedThreadPool(parallelism);
        try {
            CountDownLatch genLatch = new CountDownLatch(numFiles);
            for (int f = 0; f < numFiles; f++) {
                final int fileIndex = f;
                genPool.submit(() -> {
                    try {
                        Path file = targetDir.resolve(String.format("file_%04d.txt", fileIndex));
                        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                            ThreadLocalRandom rng = ThreadLocalRandom.current();
                            int wordsWritten = 0;
                            while (wordsWritten < wordsPerFile) {
                                int lineLen = rng.nextInt(10, 20);
                                StringBuilder line = new StringBuilder();
                                for (int w = 0; w < lineLen && wordsWritten < wordsPerFile; w++) {
                                    // Binary search in Zipf CDF for random word sampling
                                    double r = rng.nextDouble();
                                    int idx = Arrays.binarySearch(cdf, r);
                                    if (idx < 0) idx = -idx - 1;
                                    if (idx >= vocab.length) idx = vocab.length - 1;

                                    line.append(vocab[idx]).append(" ");
                                    wordsWritten++;
                                }
                                bw.write(line.toString().trim());
                                bw.newLine();
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error generating file " + fileIndex + ": " + e.getMessage());
                    } finally {
                        genLatch.countDown();
                    }
                });
            }
            genLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            genPool.shutdown();
        }
    }

    /**
     * Builds a deterministic fixed vocabulary of size N containing high-frequency English words
     * followed by synthetic lexical tokens.
     */
    private static String[] buildVocabulary(int size) {
        String[] common = {
            "the", "and", "of", "to", "a", "in", "is", "that", "it", "for",
            "as", "was", "with", "be", "by", "on", "not", "he", "at", "this",
            "are", "from", "or", "have", "an", "they", "which", "one", "you", "were",
            "her", "all", "she", "there", "would", "their", "we", "him", "been", "has",
            "when", "who", "will", "more", "no", "if", "out", "so", "said", "what",
            "up", "its", "about", "into", "than", "them", "can", "only", "other", "new",
            "some", "could", "time", "these", "two", "may", "then", "do", "first", "any",
            "my", "now", "such", "like", "our", "over", "man", "me", "even", "most",
            "made", "after", "also", "did", "many", "before", "must", "through", "back", "years",
            "where", "much", "your", "way", "well", "down", "should", "because", "each", "just"
        };
        String[] vocab = new String[size];
        int commonCount = Math.min(size, common.length);
        System.arraycopy(common, 0, vocab, 0, commonCount);
        for (int i = commonCount; i < size; i++) {
            int temp = i;
            StringBuilder sb = new StringBuilder();
            while (temp > 0 || sb.length() < 3) {
                sb.append((char) ('a' + (temp % 26)));
                temp /= 26;
            }
            vocab[i] = sb.reverse().toString();
        }
        return vocab;
    }

    /**
     * Command-line entry point.
     * Usage:
     *   java ... Challenge <dir> [--top N] [--timeout SECONDS] [--virtual] [--workers N]
     *   java ... Challenge --generate <dir> [numFiles] [wordsPerFile]
     */
    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("--help") || args[0].equals("-h")) {
            printUsage();
            return;
        }

        // Test data generation CLI mode
        if (args[0].equals("--generate")) {
            if (args.length < 2) {
                System.err.println("Usage: --generate <dir> [numFiles=200] [wordsPerFile=50000]");
                return;
            }
            Path genDir = Path.of(args[1]);
            int numFiles = args.length >= 3 ? Integer.parseInt(args[2]) : 200;
            int wordsPerFile = args.length >= 4 ? Integer.parseInt(args[3]) : 50_000;
            System.out.printf("Generating %d files with ~%d words each into '%s'...%n", numFiles, wordsPerFile, genDir);
            long start = System.currentTimeMillis();
            try {
                generateTestData(genDir, numFiles, wordsPerFile);
                long elapsed = System.currentTimeMillis() - start;
                System.out.printf("Generation complete in %.2fs!%n", elapsed / 1000.0);
            } catch (IOException e) {
                System.err.println("Generation failed: " + e.getMessage());
            }
            return;
        }

        // Parse CLI arguments
        Path targetDir = null;
        int topN = 10;
        long timeout = 0;
        boolean useVirtual = false;
        int workerCount = Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--top") && i + 1 < args.length) {
                topN = Integer.parseInt(args[++i]);
            } else if (arg.equals("--timeout") && i + 1 < args.length) {
                timeout = Long.parseLong(args[++i]);
            } else if (arg.equals("--virtual")) {
                useVirtual = true;
            } else if (arg.equals("--workers") && i + 1 < args.length) {
                workerCount = Integer.parseInt(args[++i]);
            } else if (!arg.startsWith("--") && targetDir == null) {
                targetDir = Path.of(arg);
            }
        }

        if (targetDir == null) {
            System.err.println("Error: Target directory not specified.");
            printUsage();
            return;
        }

        if (!Files.exists(targetDir) || !Files.isDirectory(targetDir)) {
            System.err.println("Error: Directory does not exist or is not a directory: " + targetDir);
            return;
        }

        Challenge analyzer = new Challenge(targetDir, workerCount, useVirtual, topN, timeout);
        try {
            analyzer.run();
        } catch (InterruptedException e) {
            System.err.println("Analysis interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java edu.itc.se.lab01.challenge.Challenge <dir> [--top N] [--timeout SECONDS] [--virtual] [--workers N]");
        System.out.println("  java edu.itc.se.lab01.challenge.Challenge --generate <dir> [numFiles] [wordsPerFile]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  <dir>               Directory containing text files to process");
        System.out.println("  --top N             Number of top words to report (default: 10)");
        System.out.println("  --timeout SECONDS   Cancel and print [PARTIAL] results after deadline");
        System.out.println("  --virtual           Use virtual threads instead of fixed platform thread pool");
        System.out.println("  --workers N         Number of platform worker threads (default: CPU cores)");
        System.out.println("  --generate <dir>    Generate synthetic test data (200 files of 50,000 words)");
    }
}