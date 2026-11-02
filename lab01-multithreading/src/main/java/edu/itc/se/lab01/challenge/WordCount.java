package edu.itc.se.lab01.challenge;

import java.nio.file.Path;

/**
 * WordCount CLI entry point matching the class name in the assignment skeleton:
 * java ... edu.itc.se.lab01.challenge.WordCount <dir> [--top N] [--timeout SECONDS] [--virtual]
 *
 * Inherits all pipeline execution, bounded queue back-pressure, worker pool orchestration,
 * live reporting, and graceful shutdown capabilities from Challenge.
 */
public class WordCount extends Challenge {

    /**
     * Constructs a WordCount analyzer instance.
     *
     * @param dir        Target directory containing text files
     * @param workers    Worker count (default: cores)
     * @param virtual    true for virtual threads, false for platform threads
     * @param top        Top N words to display
     * @param timeoutSec Timeout in seconds
     */
    public WordCount(Path dir, int workers, boolean virtual, int top, long timeoutSec) {
        super(dir, workers, virtual, top, timeoutSec);
    }

    /**
     * CLI entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        Challenge.main(args);
    }
}

