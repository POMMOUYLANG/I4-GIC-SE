package edu.itc.se.lab01.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ============================================================================
 * Task 2: Fixing a Race Condition
 * ============================================================================
 * 
 * GOAL:
 * - Reproduce a lost-update race condition on a shared counter.
 * - Fix it three different ways (synchronized methods, ReentrantLock, AtomicLong).
 * - Add a bonus high-performance counter (LongAdder).
 * - Compare their performance under high thread contention.
 * - Understand why synchronizing both writes and reads is essential under the JMM.
 * 
 * ----------------------------------------------------------------------------
 * THE RACE CONDITION (LOST UPDATES IN count++):
 * ----------------------------------------------------------------------------
 * In Java, the statement `count++` on a 64-bit primitive long is NOT atomic.
 * At the bytecode level, it comprises three separate operations:
 *   1. READ:    Load current value of field `count` into CPU register / local operand stack.
 *   2. MODIFY:  Add 1 to the local value in the operand stack.
 *   3. WRITE:   Store the result back into field `count`.
 * 
 * When multiple threads execute `count++` concurrently without synchronization,
 * their read-modify-write cycles interleave:
 * 
 *     Thread 1 (T1)              Thread 2 (T2)             Shared count
 *     -------------              -------------             ------------
 *                                                          count = 0
 *     Read count -> 0                                      count = 0
 *                                Read count -> 0           count = 0
 *     Add 1 -> 1                                           count = 0
 *                                Add 1 -> 1                count = 0
 *     Write 1 -------------------------------------------> count = 1
 *                                Write 1 (lost update!) -> count = 1
 * 
 * Although two increments occurred, `count` is only 1 instead of 2.
 * Across 8 threads performing 1,000,000 iterations each, millions of updates are lost!
 * 
 * ----------------------------------------------------------------------------
 * STEP 4: RECORDED RUNS OF UnsafeCounter (EXPECTED: 8,000,000):
 * ----------------------------------------------------------------------------
 *   - Run 1: value=1,648,909  correct=false  53 ms
 *   - Run 2: value=1,338,214  correct=false  66 ms
 *   - Run 3: value=2,078,875  correct=false  67 ms
 * 
 * Observation: Only ~15% to ~25% of updates actually persisted; all others
 * were silently overwritten due to race conditions.
 * 
 * ----------------------------------------------------------------------------
 * PERFORMANCE COMPARISON OF THE THREE FIXES (+ LongAdder):
 * ----------------------------------------------------------------------------
 *   Counter Implementation     Average Time    Mechanism & Trade-offs
 *   -----------------------    ------------    ---------------------------------------
 *   SynchronizedCounter        ~900 - 1100 ms  Pessimistic intrinsic monitor lock.
 *                                              Only 1 thread enters at a time; others
 *                                              block, incurring heavy context switching.
 * 
 *   LockCounter                ~220 - 250 ms   Explicit java.util.concurrent ReentrantLock.
 *                                              More efficient queuing (AQS) than
 *                                              intrinsic monitors under heavy contention.
 * 
 *   AtomicCounter              ~110 - 130 ms   Lock-free AtomicLong via hardware CAS
 *                                              (compare-and-swap) loop. Avoids OS thread
 *                                              blocking, but spins on CAS failure.
 * 
 *   LongAdderCounter (Bonus)   ~15 - 25 ms     Striped internal 64-bit cells (LongAdder).
 *                                              Eliminates CAS spin-wait bottleneck!
 * 
 * ----------------------------------------------------------------------------
 * STEP 6 (BONUS): WHY LongAdder IS FASTER UNDER CONTENTION:
 * ----------------------------------------------------------------------------
 * - AtomicLong uses a single memory address updated with CAS (compareAndSet).
 *   When 8 threads concurrently attempt CAS on the exact same cache line, only one
 *   thread succeeds per cycle; the other 7 fail and must retry in a tight spin loop.
 *   This causes extreme cache-coherence traffic and bus contention.
 * - LongAdder mitigates contention through internal "striping": it maintains a base
 *   field plus a dynamic array of Cell objects (each padded with @Contended to prevent
 *   false sharing across CPU cache lines). When contention is detected, threads are
 *   hashed to different cells and increment their assigned cell without interfering
 *   with one another.
 * - When get() / sum() is called, it sums all cells together. This turns an O(N)
 *   contention bottleneck into parallel per-thread updates, running ~5x faster than
 *   AtomicLong and ~50x faster than synchronized.
 * 
 * ----------------------------------------------------------------------------
 * STEP 7: WHY MAKING ONLY increment() SYNCHRONIZED BUT NOT get() LEAVES A BUG:
 * ----------------------------------------------------------------------------
 * Making only increment() synchronized creates two critical concurrency bugs:
 * 
 * 1. Memory Visibility (Java Memory Model - JMM):
 *    Synchronization establishes a "happens-before" relationship ONLY when BOTH
 *    the writing thread and the reading thread synchronize on the SAME monitor lock.
 *    If get() is not synchronized, a reading thread is not guaranteed to see the
 *    latest write. The reading thread may read a stale, cached value from a CPU
 *    register or L1/L2 cache because no memory barrier / cache flush is enforced.
 * 
 * 2. 64-bit Non-Atomic Reads ("Word Tearing"):
 *    In Java (JLS 17.7), reads and writes of 64-bit values (`long` and `double`)
 *    without `volatile` or synchronization are NOT guaranteed to be atomic. On 32-bit
 *    architectures or JVM implementations, a 64-bit long can be read as two separate
 *    32-bit operations (high word and low word). An unsynchronized get() executing
 *    simultaneously with a write could read the high 32 bits from an old value and the
 *    low 32 bits from a new value, producing a completely corrupted, garbage number!
 * 
 * Therefore, thread safety requires synchronization (or volatile semantics) on
 * BOTH mutations AND inspections of shared mutable state.
 * ============================================================================
 */

// Step 1: Define the Counter interface
interface Counter {
    void increment();

    long get();
}

// Step 2: Unsafe counter with a plain long field (subject to lost updates)
class UnsafeCounter implements Counter {
    private long count = 0;

    @Override
    public void increment() {
        count++; // Non-atomic: read -> modify -> write
    }

    @Override
    public long get() {
        return count;
    }
}

// Step 5a: SynchronizedCounter using synchronized methods
class SynchronizedCounter implements Counter {
    private long count = 0;

    @Override
    public synchronized void increment() {
        count++;
    }

    @Override
    public synchronized long get() {
        return count;
    }
}

// Step 5b: LockCounter using ReentrantLock with unlock in a finally block
class LockCounter implements Counter {
    private long count = 0;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long get() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}

// Step 5c: AtomicCounter using AtomicLong (CAS-based lock-free)
class AtomicCounter implements Counter {
    private final AtomicLong count = new AtomicLong(0);

    @Override
    public void increment() {
        count.incrementAndGet();
    }

    @Override
    public long get() {
        return count.get();
    }
}

// Step 6 (Bonus): LongAdderCounter using striped cells to avoid CAS contention
class LongAdderCounter implements Counter {
    private final LongAdder adder = new LongAdder();

    @Override
    public void increment() {
        adder.increment();
    }

    @Override
    public long get() {
        return adder.sum();
    }
}

public class Bench {
    static final int THREADS = 8;
    static final int ITERATIONS = 1_000_000;

    // Step 3: Harness Bench.run(String label, Counter c)
    static void run(String label, Counter c) throws InterruptedException {
        long start = System.nanoTime();
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < THREADS; t++) {
            Thread th = new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) {
                    c.increment();
                }
            }, "inc-" + t);
            threads.add(th);
            th.start();
        }
        for (Thread th : threads) {
            th.join();
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        long expected = (long) THREADS * ITERATIONS;
        long val = c.get();
        System.out.printf("%-22s value=%,d  correct=%-5b  %d ms%n",
                label, val, val == expected, ms);
    }

    public static void main(String[] args) throws InterruptedException {
        // Step 4: Run against UnsafeCounter to demonstrate race condition
        run("UnsafeCounter", new UnsafeCounter());

        // Step 5: Benchmark the three correct implementations
        run("SynchronizedCounter", new SynchronizedCounter());
        run("LockCounter", new LockCounter());
        run("AtomicCounter", new AtomicCounter());

        // Step 6 (Bonus): Benchmark LongAdderCounter
        run("LongAdderCounter", new LongAdderCounter());
    }
}