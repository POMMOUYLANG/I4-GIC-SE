package edu.itc.se.lab01.task5;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ============================================================================
 * Task 5: Diagnosing a Deadlock and Shutting Down Gracefully
 * ============================================================================
 * 
 * GOAL:
 * - Reproduce a real deadlock caused by inconsistent lock acquisition order.
 * - Detect and diagnose deadlocks using JDK tools (jstack / ThreadMXBean).
 * - Fix the deadlock using lock ordering (OrderedBank).
 * - Fix the deadlock using timed tryLock with back-off (TryLockBank).
 * - Implement cooperative cancellation via thread interruption and clean shutdown.
 * 
 * ----------------------------------------------------------------------------
 * 1. THE DEADLOCK PROBLEM:
 * ----------------------------------------------------------------------------
 * In Bank.transfer(from, to, amount):
 *   synchronized (from) {
 *       synchronized (to) { ... }
 *   }
 * Lock acquisition order depends on the argument order. When two threads
 * transfer funds in opposite directions simultaneously:
 *   - "transfer-3": holds monitor Account 1, waits for monitor Account 2
 *   - "transfer-6": holds monitor Account 2, waits for monitor Account 1
 * This creates Coffman's Circular Wait condition, resulting in permanent deadlock.
 * 
 * ----------------------------------------------------------------------------
 * 2. DIAGNOSIS (JDK TOOLS & WATCHDOG):
 * ----------------------------------------------------------------------------
 * - JDK Command Line: `jcmd <pid> Thread.print` or `jstack <pid>` prints thread dumps
 *   and identifies Java-level deadlocks (saved in deadlock.txt).
 * - Programmatic Detection: `ManagementFactory.getThreadMXBean().findDeadlockedThreads()`
 *   scans the JVM for deadlocked monitor cycles at runtime.
 * 
 * ----------------------------------------------------------------------------
 * 3. FIX A (ORDERED LOCK ACQUISITION):
 * ----------------------------------------------------------------------------
 * - In OrderedBank, locks are ALWAYS acquired in ascending order of Account ID:
 *       Account first  = from.id() < to.id() ? from : to;
 *       Account second = from.id() < to.id() ? to : from;
 * - Because all threads acquire locks in the exact same global order, circular wait
 *   is mathematically eliminated. Deadlock is impossible.
 * 
 * ----------------------------------------------------------------------------
 * 4. FIX B (TIMED tryLock WITH RANDOM BACK-OFF):
 * ----------------------------------------------------------------------------
 * - In TryLockBank, each Account owns a ReentrantLock.
 * - Threads attempt `tryLock(10, TimeUnit.MILLISECONDS)` on both locks. If the second
 *   lock cannot be acquired within 10 ms, the thread releases the first lock and sleeps
 *   for a random back-off period before retrying.
 * - This breaks the "Hold and Wait" condition and prevents livelock through jitter.
 * 
 * ----------------------------------------------------------------------------
 * 5. COOPERATIVE CANCELLATION & GRACEFUL SHUTDOWN:
 * ----------------------------------------------------------------------------
 * - Workers check `!Thread.currentThread().isInterrupted()` in their work loop.
 * - When blocking calls (Thread.sleep, tryLock) throw InterruptedException,
 *   the thread restores its interrupt flag via `Thread.currentThread().interrupt()`
 *   and terminates gracefully.
 * - The main thread interrupts workers and awaits termination with a timeout
 *   using `worker.join(1000)`.
 * ============================================================================
 */

// Step 1: Account class
class Account {
    final int id;
    private long balance;

    public Account(int id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public int id() {
        return id;
    }

    public long balance() {
        return balance;
    }

    public void withdraw(long amount) {
        balance -= amount;
    }

    public void deposit(long amount) {
        balance += amount;
    }
}

// Step 1: The original deadlocking bank (lock order depends on argument order)
class Bank {
    // BUG: lock order depends on the argument order -> circular wait
    public static void transfer(Account from, Account to, long amount) {
        synchronized (from) {
            synchronized (to) {
                if (from.balance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                }
            }
        }
    }
}

// Step 4: Fix A — OrderedBank acquires monitors in ascending id order
class OrderedBank {
    public static void transfer(Account from, Account to, long amount) {
        if (from == to) return; // Prevent self-transfer

        // Acquire locks in ascending order of account ID to eliminate circular wait
        Account first = from.id() < to.id() ? from : to;
        Account second = from.id() < to.id() ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.balance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                }
            }
        }
    }
}

// Step 5: Account with ReentrantLock for Fix B
class TryLockAccount {
    final int id;
    private long balance;
    final ReentrantLock lock = new ReentrantLock();

    public TryLockAccount(int id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public int id() {
        return id;
    }

    public long balance() {
        return balance;
    }

    public void withdraw(long amount) {
        balance -= amount;
    }

    public void deposit(long amount) {
        balance += amount;
    }
}

// Step 5: Fix B — TryLockBank using tryLock(10, MILLISECONDS) and random back-off
class TryLockBank {
    public static boolean transfer(TryLockAccount from, TryLockAccount to, long amount, AtomicLong retries)
            throws InterruptedException {
        if (from == to) return true;

        while (!Thread.currentThread().isInterrupted()) {
            if (from.lock.tryLock(10, TimeUnit.MILLISECONDS)) {
                try {
                    if (to.lock.tryLock(10, TimeUnit.MILLISECONDS)) {
                        try {
                            if (from.balance() >= amount) {
                                from.withdraw(amount);
                                to.deposit(amount);
                            }
                            return true; // Successfully transferred
                        } finally {
                            to.lock.unlock();
                        }
                    }
                } finally {
                    from.lock.unlock();
                }
            }
            // Failed to acquire both locks: count retry and back-off randomly
            retries.incrementAndGet();
            Thread.sleep(ThreadLocalRandom.current().nextInt(1, 3));
        }
        return false;
    }
}

public class Main {
    /**
     * Step 3: Starts a watchdog daemon thread that checks for deadlocks every second.
     */
    public static Thread startWatchdog() {
        Thread watchdog = new Thread(() -> {
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    long[] deadlockedIds = bean.findDeadlockedThreads();
                    if (deadlockedIds != null && deadlockedIds.length > 0) {
                        ThreadInfo[] infos = bean.getThreadInfo(deadlockedIds);
                        List<String> names = Arrays.stream(infos)
                                .filter(Objects::nonNull)
                                .map(ThreadInfo::getThreadName)
                                .sorted()
                                .toList();
                        if (!names.isEmpty()) {
                            System.out.println("[watchdog] DEADLOCK detected between: " + String.join(", ", names));
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
        return watchdog;
    }

    /**
     * Runs Step 4: Fix A (OrderedBank).
     */
    private static void testOrderedBank() throws InterruptedException {
        System.out.println("# OrderedBank");
        Account[] accounts = new Account[4];
        for (int i = 0; i < accounts.length; i++) accounts[i] = new Account(i, 1_000);

        long t0 = System.currentTimeMillis();
        Thread[] workers = new Thread[8];
        for (int w = 0; w < workers.length; w++) {
            workers[w] = new Thread(() -> {
                var rnd = ThreadLocalRandom.current();
                for (int i = 0; i < 100_000; i++) {
                    Account a = accounts[rnd.nextInt(accounts.length)];
                    Account b = accounts[rnd.nextInt(accounts.length)];
                    if (a != b) OrderedBank.transfer(a, b, rnd.nextInt(1, 50));
                }
            }, "ordered-" + w);
            workers[w].start();
        }

        for (Thread t : workers) t.join();
        long elapsed = System.currentTimeMillis() - t0;
        long total = 0; for (Account a : accounts) total += a.balance();
        System.out.printf("transfers=800000 total=%d (expected 4000) in %d ms%n%n", total, elapsed);
    }

    /**
     * Runs Step 5: Fix B (TryLockBank).
     */
    private static void testTryLockBank() throws InterruptedException {
        System.out.println("# TryLockBank");
        TryLockAccount[] accounts = new TryLockAccount[4];
        for (int i = 0; i < accounts.length; i++) accounts[i] = new TryLockAccount(i, 1_000);

        AtomicLong retries = new AtomicLong(0);
        long t0 = System.currentTimeMillis();
        Thread[] workers = new Thread[8];
        for (int w = 0; w < workers.length; w++) {
            workers[w] = new Thread(() -> {
                var rnd = ThreadLocalRandom.current();
                for (int i = 0; i < 100_000; i++) {
                    TryLockAccount a = accounts[rnd.nextInt(accounts.length)];
                    TryLockAccount b = accounts[rnd.nextInt(accounts.length)];
                    if (a != b) {
                        try {
                            TryLockBank.transfer(a, b, rnd.nextInt(1, 50), retries);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }, "trylock-" + w);
            workers[w].start();
        }

        for (Thread t : workers) t.join();
        long elapsed = System.currentTimeMillis() - t0;
        long total = 0; for (TryLockAccount a : accounts) total += a.balance();
        System.out.printf("transfers=800000 retries=%d total=%d (expected 4000) in %d ms%n%n",
                retries.get(), total, elapsed);
    }

    /**
     * Runs Step 6: Graceful Shutdown.
     */
    private static void testGracefulShutdown() throws InterruptedException {
        System.out.println("# graceful shutdown");
        Account[] accounts = new Account[4];
        for (int i = 0; i < accounts.length; i++) accounts[i] = new Account(i, 1_000);

        Thread[] workers = new Thread[8];
        AtomicLong[] counts = new AtomicLong[8];
        for (int i = 0; i < 8; i++) counts[i] = new AtomicLong(0);

        for (int w = 0; w < workers.length; w++) {
            final int workerId = w;
            workers[w] = new Thread(() -> {
                var rnd = ThreadLocalRandom.current();
                long count = 0;
                while (!Thread.currentThread().isInterrupted()) {
                    Account a = accounts[rnd.nextInt(accounts.length)];
                    Account b = accounts[rnd.nextInt(accounts.length)];
                    if (a != b) {
                        OrderedBank.transfer(a, b, rnd.nextInt(1, 50));
                        count++;
                    }
                }
                counts[workerId].set(count);
                System.out.printf("transfer-%d stopped after %d transfers%n", workerId, count);
            }, "transfer-" + w);
            workers[w].start();
        }

        // Run workers for 2000 ms
        Thread.sleep(2000);
        System.out.println("[main] interrupting 8 workers after 2000 ms");
        for (Thread w : workers) {
            w.interrupt();
        }

        // Join workers with 1-second timeout
        boolean allStopped = true;
        for (Thread w : workers) {
            w.join(1000);
            if (w.isAlive()) {
                allStopped = false;
            }
        }

        long total = 0;
        for (Account a : accounts) total += a.balance();
        System.out.printf("all workers stopped=%b total=%d (expected 4000)%n", allStopped, total);
    }

    /**
     * Steps 1 & 2: Broken version with watchdog that hangs in deadlock.
     */
    private static void runBrokenVersion() throws InterruptedException {
        System.out.println("# broken version, with watchdog");
        startWatchdog();

        Account[] accounts = new Account[4];
        for (int i = 0; i < accounts.length; i++) accounts[i] = new Account(i, 1_000);

        Thread[] workers = new Thread[8];
        for (int w = 0; w < workers.length; w++) {
            workers[w] = new Thread(() -> {
                var rnd = ThreadLocalRandom.current();
                for (int i = 0; i < 100_000; i++) {
                    Account a = accounts[rnd.nextInt(accounts.length)];
                    Account b = accounts[rnd.nextInt(accounts.length)];
                    if (a != b) Bank.transfer(a, b, rnd.nextInt(1, 50));
                }
            }, "transfer-" + w);
            workers[w].start();
        }

        System.out.println("(process hangs - Ctrl+C)");
        for (Thread t : workers) t.join();
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0 && (args[0].equals("--broken") || args[0].equals("--deadlock"))) {
            runBrokenVersion();
            return;
        }

        // Step 4: Fix A (OrderedBank)
        testOrderedBank();

        // Step 5: Fix B (TryLockBank)
        testTryLockBank();

        // Step 6: Graceful Shutdown
        testGracefulShutdown();
    }
}
