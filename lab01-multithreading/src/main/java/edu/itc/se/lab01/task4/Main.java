package edu.itc.se.lab01.task4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * ============================================================================
 * Task 4: Thread Pools, Future, and Virtual Threads
 * ============================================================================
 * 
 * GOAL:
 * - Replace hand-made threads with ExecutorService thread pools.
 * - Collect asynchronous task results through Future objects.
 * - Handle task failures (ExecutionException) and timeouts (TimeoutException).
 * - Compare fixed platform thread pools against virtual threads for I/O workloads.
 * - Use CompletableFuture with allOf and handle() for non-blocking asynchronous pipelines.
 * 
 * ----------------------------------------------------------------------------
 * 1. CPU-BOUND WORKLOAD (PRIME COUNTING):
 * ----------------------------------------------------------------------------
 * - Characteristics: Saturated CPU instruction pipelines; minimal blocking.
 * - Sizing: Number of threads matches Runtime.getRuntime().availableProcessors().
 *   Creating more threads than physical/logical cores adds context-switching
 *   overhead without increasing throughput.
 * - Partitioning: 4,000,000 numbers split into 16 chunks submitted to a fixed pool.
 * - Performance: Near-linear speedup (~1,260 ms single-threaded down to ~230 ms
 *   on 16 cores).
 * 
 * ----------------------------------------------------------------------------
 * 2. I/O-BOUND WORKLOAD: PLATFORM THREADS VS. VIRTUAL THREADS:
 * ----------------------------------------------------------------------------
 * - Characteristics: Tasks spend >90% of their time waiting for network/disk I/O
 *   (simulated via Thread.sleep(200)).
 * 
 * - Fixed Platform Pool (20 threads):
 *   * Submitting 500 downloads to 20 threads requires 25 serial batches (500 / 20 = 25).
 *   * Minimum theoretical runtime: 25 batches * 200 ms = 5,000 ms.
 *   * Each platform thread maps 1:1 to an OS thread with ~1 MB dedicated stack.
 *   * Total runtime: ~5,010 ms.
 * 
 * - Virtual Threads (Executors.newVirtualThreadPerTaskExecutor()):
 *   * Creates a dedicated virtual thread per task (500 virtual threads).
 *   * Virtual threads are lightweight user-mode threads (~few hundred bytes on heap).
 *   * When a virtual thread blocks (e.g. Thread.sleep), the JVM unmounts it from the
 *     underlying OS carrier thread, allowing other virtual threads to run.
 *   * All 499 fast tasks execute concurrently in ~200 ms.
 *   * The single slow task is cancelled at the 1,000 ms timeout mark.
 *   * Total runtime: ~1,010 ms (~5x faster, bounded only by the 1s timeout!).
 * 
 * ----------------------------------------------------------------------------
 * 3. ERROR & TIMEOUT HANDLING WITH FUTURE:
 * ----------------------------------------------------------------------------
 * - ExecutionException: Thrown by future.get() when the underlying Callable throws
 *   an exception (e.g. IOException("boom")). The original cause is accessible via getCause().
 * - TimeoutException: Thrown when future.get(timeout, unit) expires before task completion.
 * - Cancellation: Calling future.cancel(true) sends an interrupt to the running thread
 *   to abort execution and prevent resource leaks.
 * 
 * ----------------------------------------------------------------------------
 * 4. CLEAN EXECUTOR SHUTDOWN (JDK 19+ try-with-resources):
 * ----------------------------------------------------------------------------
 * - In modern Java (JDK 19+), ExecutorService extends AutoCloseable.
 * - Exiting the try-with-resources block automatically invokes close(), which calls
 *   shutdown() and waits for pending tasks to terminate, exiting cleanly without System.exit.
 * ============================================================================
 */

// Step 1: PrimeCounter Callable for CPU-bound chunking
class PrimeCounter implements Callable<Long> {
    private final long from;
    private final long to;

    public PrimeCounter(long from, long to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Long call() {
        return Main.countPrimes(from, to);
    }
}

// Step 2, 3 & 4: FakeDownload Callable for I/O-bound simulation
class FakeDownload implements Callable<String> {
    private final String url;
    private final int index;

    // Standard constructor
    public FakeDownload(String url) {
        this(url, -1);
    }

    // Constructor with index for failure and timeout testing
    public FakeDownload(String url, int index) {
        this.url = url;
        this.index = index;
    }

    @Override
    public String call() throws Exception {
        // Step 4: One download (index 0) sleeps 5 seconds to test timeout handling
        if (index == 0) {
            Thread.sleep(5000);
            return url + " -> " + url.length();
        }

        // Step 3: Every 50th download throws IOException("boom") to test failure handling
        if (index > 0 && index % 50 == 0) {
            throw new IOException("boom");
        }

        // Simulated network latency: 200 ms
        Thread.sleep(200);
        return url + " -> " + url.length();
    }
}

public class Main {
    /**
     * Naive trial-division prime checking for range [from, to).
     */
    public static long countPrimes(long from, long to) {
        long count = 0;
        for (long n = Math.max(from, 2); n < to; n++) {
            boolean prime = true;
            for (long d = 2; d * d <= n; d++) {
                if (n % d == 0) {
                    prime = false;
                    break;
                }
            }
            if (prime)
                count++;
        }
        return count;
    }

    /**
     * Step 2, 3 & 4: Submit 500 downloads, handle failures and timeouts.
     */
    private static void runDownloads(String label, ExecutorService executor) {
        long t0 = System.nanoTime();
        List<Future<String>> futures = new ArrayList<>();

        // Submit 500 downloads
        for (int i = 0; i < 500; i++) {
            futures.add(executor.submit(new FakeDownload("https://example.com/file/" + i, i)));
        }

        int ok = 0;
        int failed = 0;
        int timedOut = 0;

        // Collect results with future.get()
        for (Future<String> future : futures) {
            try {
                // Step 4: 1-second timeout per download
                future.get(1, TimeUnit.SECONDS);
                ok++;
            } catch (TimeoutException e) {
                // Step 4: Report timed out and cancel running task
                timedOut++;
                future.cancel(true);
            } catch (ExecutionException e) {
                // Step 3: Collect task exceptions without crashing
                failed++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("%-35s: ok=%-3d failed=%-2d timedOut=%-2d in %d ms%n",
                label, ok, failed, timedOut, elapsedMs);
    }

    /**
     * Step 6 (Bonus): Asynchronous pipeline using CompletableFuture, allOf, and handle().
     */
    private static void runDownloadsCompletableFuture(ExecutorService executor) {
        long t0 = System.nanoTime();
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            final int index = i;
            String url = "https://example.com/file/" + i;

            CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
                try {
                    return new FakeDownload(url, index).call();
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            }, executor)
                    .orTimeout(1, TimeUnit.SECONDS)
                    .handle((result, ex) -> {
                        // Transform failures and timeouts into a fallback string
                        if (ex != null) {
                            return "fallback-for-" + index;
                        }
                        return result;
                    });

            futures.add(cf);
        }

        // Wait for all CompletableFutures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("CompletableFuture (virtual)        : completed %d downloads in %d ms%n",
                futures.size(), elapsedMs);
    }

    public static void main(String[] args) throws Exception {
        final long LIMIT = 4_000_000;
        final int CHUNKS = 16;

        // --- Single-threaded baseline ---
        long t0 = System.nanoTime();
        long single = countPrimes(0, LIMIT);
        System.out.printf("single-threaded: %d primes in %d ms%n", single, (System.nanoTime() - t0) / 1_000_000);

        // --- Step 1 & 5: CPU-bound Prime Counting with FixedThreadPool ---
        t0 = System.nanoTime();
        long total = 0;
        int processors = Runtime.getRuntime().availableProcessors();
        try (ExecutorService pool = Executors.newFixedThreadPool(processors)) {
            List<Future<Long>> futures = new ArrayList<>();
            long chunkSize = (LIMIT + CHUNKS - 1) / CHUNKS;

            for (int i = 0; i < CHUNKS; i++) {
                long from = i * chunkSize;
                long to = Math.min((i + 1) * chunkSize, LIMIT);
                futures.add(pool.submit(new PrimeCounter(from, to)));
            }

            for (Future<Long> future : futures) {
                total += future.get();
            }
        }
        System.out.printf("pool (%d threads): %d primes in %d ms%n",
                processors, total, (System.nanoTime() - t0) / 1_000_000);

        // --- Step 2 & 5: I/O-bound: 20 Platform Threads vs. Virtual Threads ---
        try (ExecutorService fixedPool = Executors.newFixedThreadPool(20)) {
            runDownloads("downloads on fixed(20) platform threads", fixedPool);
        }

        try (ExecutorService virtualPool = Executors.newVirtualThreadPerTaskExecutor()) {
            runDownloads("downloads on virtual threads", virtualPool);
        }

        // --- Step 6 (Bonus): CompletableFuture version ---
        try (ExecutorService virtualPool = Executors.newVirtualThreadPerTaskExecutor()) {
            runDownloadsCompletableFuture(virtualPool);
        }
    }
}