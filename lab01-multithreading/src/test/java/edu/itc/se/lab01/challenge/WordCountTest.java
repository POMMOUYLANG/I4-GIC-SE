package edu.itc.se.lab01.challenge;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ============================================================================
 * Verification Test Suite for Challenge & WordCount
 * ============================================================================
 *
 * Requirements:
 * 1. Single-threaded reference implementation and parallel implementation
 *    (both platform and virtual threads) produce identical frequency maps for a 20-file fixture.
 * 2. Tool stops cleanly within 3 seconds when given --timeout 1 on a directory of 5,000 files.
 */
public class WordCountTest {

    @Test
    @DisplayName("Verification: Parallel matches single-threaded reference on 20-file fixture")
    void testParallelMatchesSingleThreaded(@TempDir Path tempDir) throws Exception {
        runParallelMatchesSingleThreaded(tempDir);
    }

    private void runParallelMatchesSingleThreaded(Path tempDir) throws Exception {
        // Create 20 test fixture files
        int numFiles = 20;
        int wordsPerFile = 2_000;
        Challenge.generateTestData(tempDir, numFiles, wordsPerFile);

        // 1. Compute baseline counts using single-threaded reference implementation
        Map<String, Long> expected = Challenge.countSingleThreaded(tempDir);
        assertNotNull(expected);
        assertFalse(expected.isEmpty(), "Reference count should not be empty");

        // 2. Compute counts using parallel platform threads (4 workers)
        WordCount wcPlatform = new WordCount(tempDir, 4, false, 10, 0);
        wcPlatform.run();
        Map<String, Long> actualPlatform = wcPlatform.getCountsMap();

        assertEquals(expected.size(), actualPlatform.size(), "Map sizes must match");
        assertEquals(expected, actualPlatform, "Platform parallel counts must match single-threaded reference");
        assertFalse(wcPlatform.isPartial(), "Normal execution should not be marked partial");

        // 3. Compute counts using virtual threads
        WordCount wcVirtual = new WordCount(tempDir, 4, true, 10, 0);
        wcVirtual.run();
        Map<String, Long> actualVirtual = wcVirtual.getCountsMap();

        assertEquals(expected.size(), actualVirtual.size(), "Map sizes must match for virtual threads");
        assertEquals(expected, actualVirtual, "Virtual threads counts must match single-threaded reference");
        assertFalse(wcVirtual.isPartial(), "Virtual threads normal execution should not be marked partial");
    }

    @Test
    @DisplayName("Cancellation: Tool stops within 3 s when given --timeout 1 on 5,000 files")
    void testTimeoutStopsWithin3Seconds(@TempDir Path tempDir) throws Exception {
        runTimeoutStopsWithin3Seconds(tempDir);
    }

    private void runTimeoutStopsWithin3Seconds(Path tempDir) throws Exception {
        int numFiles = 5_000;
        String lineChunk = "alpha beta gamma delta epsilon zeta eta theta iota kappa lambda mu nu xi omicron pi rho sigma tau\n";
        // 1000 repetitions * 20 words = 20,000 words per file (total 100,000,000 words)
        StringBuilder contentSb = new StringBuilder();
        for (int c = 0; c < 1000; c++) {
            contentSb.append(lineChunk);
        }
        String fileContent = contentSb.toString();

        // Fast generation of 5,000 files
        for (int i = 0; i < numFiles; i++) {
            Path file = tempDir.resolve(String.format("file_%05d.txt", i));
            Files.writeString(file, fileContent, StandardCharsets.UTF_8);
        }

        // Run with --timeout 1
        long startTime = System.currentTimeMillis();
        WordCount wc = new WordCount(tempDir, 8, false, 5, 1);
        wc.run();
        long elapsed = System.currentTimeMillis() - startTime;

        // Verify stops within 3 seconds
        assertTrue(elapsed < 3000, "Execution must stop within 3 seconds, elapsed: " + elapsed + " ms");
        assertTrue(wc.isPartial(), "Result must be marked partial on timeout cancellation");
    }

    private static void deleteRecursively(Path root) {
        if (!Files.exists(root)) return;
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.deleteIfExists(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {}
    }

    /**
     * Standalone CLI runner to execute both verification tests directly without external test runners.
     */
    public static void main(String[] args) {
        System.out.println("Running WordCount verification test suite...");
        WordCountTest test = new WordCountTest();

        try {
            System.out.println("--> [Test 1/2] Verifying Parallel & Virtual matches Single-Threaded reference (20 files)...");
            Path tempDir1 = Files.createTempDirectory("wc_fixture_20_");
            try {
                test.testParallelMatchesSingleThreaded(tempDir1);
            } finally {
                deleteRecursively(tempDir1);
            }
            System.out.println("    [PASSED] Single-threaded and parallel maps are 100% identical!");

            System.out.println("--> [Test 2/2] Verifying --timeout 1 stops within 3 seconds (5,000 files)...");
            Path tempDir2 = Files.createTempDirectory("wc_fixture_5000_");
            try {
                test.testTimeoutStopsWithin3Seconds(tempDir2);
            } finally {
                deleteRecursively(tempDir2);
            }
            System.out.println("    [PASSED] Execution cleanly aborted and returned [PARTIAL] within 3 seconds!");

            System.out.println("=================================================");
            System.out.println(" ALL TESTS PASSED SUCCESSFULLY! (2/2)");
            System.out.println("=================================================");
        } catch (Throwable t) {
            System.err.println("Test failed: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }
}
