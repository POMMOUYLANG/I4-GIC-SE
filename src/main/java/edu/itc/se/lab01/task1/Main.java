package edu.itc.se.lab01.task1;

import java.util.List;

/**
 * ============================================================================
 * Task 1: Creating and Running Threads
 * ============================================================================
 * 
 * GOAL:
 * - Create threads in four different ways.
 * - Understand the difference between start() and run().
 * - Coordinate thread completion using join().
 * 
 * ----------------------------------------------------------------------------
 * FOUR WAYS TO CREATE THREADS IN JAVA:
 * ----------------------------------------------------------------------------
 * 1. Subclassing Thread:
 *    Define a class that extends java.lang.Thread and override run().
 *    Example: Countdown apollo = new Countdown("apollo");
 * 
 * 2. Implementing Runnable:
 *    Define a class that implements java.lang.Runnable and pass an instance
 *    to new Thread(runnable, name).
 *    Example: Thread gemini = new Thread(new CountdownTask(), "gemini");
 * 
 * 3. Lambda Expression for Runnable:
 *    Since Runnable is a functional interface (@FunctionalInterface), pass a
 *    lambda expression () -> { ... } directly into Thread constructor.
 *    Example: Thread mercury = new Thread(() -> { ... }, "mercury");
 * 
 * 4. Virtual Threads (Java 21+):
 *    Use Thread.ofVirtual() builder to create lightweight virtual threads
 *    managed by the JVM rather than 1:1 OS platform threads.
 *    Example: Thread artemis = Thread.ofVirtual().name("artemis").start(...);
 * 
 * ----------------------------------------------------------------------------
 * DIFFERENCE BETWEEN start() AND run() (OBSERVATIONS & EXPLANATION):
 * ----------------------------------------------------------------------------
 * 1. Asynchronous Concurrency vs. Synchronous Method Call:
 *    - start(): Prompts the JVM to allocate a new underlying execution context
 *      (platform OS thread or virtual thread mount) and runs run() asynchronously
 *      on that separate thread. All countdowns interleave concurrently.
 *    - run(): Does NOT create or start a new thread. It simply executes run()
 *      as a standard, synchronous method call on the CALLING thread ('main').
 *      Each countdown must finish completely before the next one starts.
 * 
 * 2. Thread Identity and Name (Thread.currentThread().getName()):
 *    - start(): Tasks execute on their respective threads. CountdownTask
 *      and the lambda print their assigned names: "gemini", "mercury", "artemis".
 *    - run(): Because execution occurs on the caller thread,
 *      Thread.currentThread().getName() resolves to "main" for gemini and mercury!
 *      (Only apollo still prints "apollo" because Countdown extends Thread
 *      and calls its own instance method getName()).
 * 
 * 3. Timing of "Mission control: all launched":
 *    - start(): Prints immediately after thread creation/start, while countdowns
 *      are still actively ticking down in the background.
 *    - run(): Prints only AFTER all sequential countdowns have already finished,
 *      because the caller thread was blocked executing each run() sequentially.
 * 
 * 4. Thread Lifecycle State (getState()) & join() Behavior:
 *    - start(): Threads transition NEW -> RUNNABLE -> ... -> TERMINATED.
 *      join() correctly blocks the main thread until each thread finishes.
 *      After joins, all thread states are TERMINATED.
 *    - run(): The threads were never started via the JVM scheduler, so their
 *      state remains NEW! Calling join() on an unstarted thread returns immediately
 *      without waiting because the thread was never alive (isAlive() == false).
 * 
 * 5. Virtual Thread Behavior on direct run():
 *    - In the JDK (JEP 444), java.lang.VirtualThread overrides public void run()
 *      as an empty no-op (return;). Virtual threads require continuation mounting
 *      via the scheduler, initiated by start(). Therefore, invoking run() directly
 *      on an unstarted virtual thread produces no output at all.
 * ============================================================================
 */

// Step 1: Subclass of Thread whose run() prints <name>: 3, 2, 1, liftoff!
class Countdown extends Thread {
    public Countdown(String name) {
        super(name);
    }

    @Override
    public void run() {
        for (int i = 3; i >= 1; i--) {
            System.out.println(getName() + ": " + i);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println(getName() + ": liftoff!");
    }
}

// Step 2: Implementation of Runnable that does the same thing,
// using Thread.currentThread().getName()
class CountdownTask implements Runnable {
    @Override
    public void run() {
        for (int i = 3; i >= 1; i--) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println(Thread.currentThread().getName() + ": liftoff!");
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Step 5: Check for --sequential flag
        boolean sequential = args.length > 0 && args[0].equals("--sequential");

        // Step 3: Create threads in different ways
        // 1) Countdown extends Thread named "apollo"
        Thread apollo = new Countdown("apollo");

        // 2) Thread(Runnable, name) with CountdownTask named "gemini"
        Thread gemini = new Thread(new CountdownTask(), "gemini");

        // 3) Thread from a lambda named "mercury"
        Thread mercury = new Thread(() -> {
            for (int i = 3; i >= 1; i--) {
                System.out.println(Thread.currentThread().getName() + ": " + i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println(Thread.currentThread().getName() + ": liftoff!");
        }, "mercury");

        // 4) Virtual thread named "artemis"
        Thread artemis;

        // Step 5 & Step 3: Call run() if --sequential is present, otherwise start()
        if (sequential) {
            // Unstarted virtual thread so run() can be called instead of start()
            artemis = Thread.ofVirtual().name("artemis").unstarted(new CountdownTask());
            List<Thread> threads = List.of(apollo, gemini, mercury, artemis);
            for (Thread t : threads) {
                t.run();
            }
        } else {
            // Normal concurrent execution:
            apollo.start();
            gemini.start();
            mercury.start();
            // Built and started with Thread.ofVirtual().name("artemis").start(...)
            artemis = Thread.ofVirtual().name("artemis").start(new CountdownTask());
        }

        List<Thread> threads = List.of(apollo, gemini, mercury, artemis);

        // Step 4: Print "Mission control: all launched" immediately after starting them
        System.out.println("Mission control: all launched");

        // Step 4: join() all four threads
        for (Thread t : threads) {
            t.join();
        }

        // Step 4: Print "Mission control: all landed"
        System.out.println("Mission control: all landed");

        // Step 6: Print each thread's isVirtual() and getState() after the joins
        for (Thread t : threads) {
            System.out.printf("%-8s virtual=%-5b state=%s%n",
                    t.getName(),
                    t.isVirtual(),
                    t.getState());
        }
    }
}