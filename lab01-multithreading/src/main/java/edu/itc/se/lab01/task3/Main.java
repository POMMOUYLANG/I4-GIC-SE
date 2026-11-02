package edu.itc.se.lab01.task3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * Task 3: Producer-Consumer with wait()/notify() and BlockingQueue
 * ============================================================================
 * 
 * GOAL:
 * - Implement a bounded buffer by hand using guarded blocks (synchronized,
 *   while-wait loop, and notifyAll).
 * - Replace the custom buffer with java.util.concurrent.BlockingQueue and
 *   observe how much concurrency boilerplate disappears.
 * - Coordinate graceful termination across multiple producers and consumers
 *   using the Poison Pill pattern.
 * - Understand why guarded blocks must use while loops (not if) and why
 *   multi-predicate monitors must use notifyAll() (not notify()).
 * 
 * ----------------------------------------------------------------------------
 * ARCHITECTURE & DATA FLOW:
 * ----------------------------------------------------------------------------
 *   [producer-1] \                    /---> [consumer-1]
 *                 ===> [Buffer (5)] ===---> [consumer-2]
 *   [producer-2] /                    \---> [consumer-3]
 * 
 *   - Producers produce 50 orders each (100 orders total, IDs 1..100).
 *   - Buffer capacity is bounded at 5 items:
 *       * When full:  producers wait() until consumers take items.
 *       * When empty: consumers wait() until producers put items.
 *   - Shutdown via Poison Pills: 3 poison pills (Order with id = -1) are sent,
 *     allowing each of the 3 consumers to gracefully stop upon consumption.
 * 
 * ----------------------------------------------------------------------------
 * GUARDED BLOCKS PATTERN:
 * ----------------------------------------------------------------------------
 * Guarded blocks synchronize on a shared monitor and guard critical sections
 * with a condition predicate:
 * 
 *     synchronized (lock) {
 *         while (!condition) {
 *             lock.wait();
 *         }
 *         // perform action modifying state
 *         lock.notifyAll();
 *     }
 * 
 * ----------------------------------------------------------------------------
 * STEP 7 EXPERIMENT: WHAT BREAKS WHEN CHANGING notifyAll() -> notify()
 *                    AND while -> if?
 * ----------------------------------------------------------------------------
 * 1. Changing notifyAll() to notify() -> Livelock / Deadlock (Lost Signals):
 *    - Both producers (waiting for 'not full') and consumers (waiting for 'not empty')
 *      share the same wait set on the monitor lock.
 *    - notify() wakes only ONE arbitrary thread from the wait set.
 *    - Example scenario leading to deadlock:
 *        1) Buffer is full; both producers are waiting.
 *        2) A consumer takes an item and calls notify().
 *        3) If another consumer was waiting (or wakes up), the signal wakes a CONSUMER
 *           instead of a PRODUCER!
 *        4) The woken consumer finds the buffer empty (or takes the only item) and goes
 *           back to wait().
 *        5) The producers were NEVER signaled, so they stay in WAITING state forever.
 *        6) Eventually, all consumers consume remaining items and wait; all producers
 *           are waiting for space. Entire program hangs permanently in DEADLOCK!
 *    - notifyAll() solves this by waking ALL threads, ensuring both producers and
 *      consumers re-evaluate their respective condition predicates.
 * 
 * 2. Changing while to if -> Buffer Overflow & NoSuchElementException:
 *    - In an if block: `if (items.size() == capacity) wait();`
 *    - Suppose buffer is full (5 items) and both Producer 1 and Producer 2 are waiting.
 *    - Consumer takes 1 item and signals.
 *    - Both producers wake up.
 *    - Producer 1 acquires lock first, puts an item (buffer size is now 5), and releases lock.
 *    - Producer 2 acquires lock. Because it was an `if`, it does NOT re-check the condition!
 *      It immediately proceeds and adds its item, resulting in buffer size = 6 (> capacity)!
 *    - Similarly for consumers: if two consumers wake up when 1 item is available, with `if`,
 *      both call `removeFirst()`. The second consumer throws `NoSuchElementException`!
 *    - Furthermore, Java threads can experience "spurious wakeups" without any notify call.
 *      A `while` loop re-checks the predicate after waking, guarding against invalid state.
 * ============================================================================
 */

// Step 1: Order record
record Order(int id, String item, int qty) {}

// Step 6: Queue abstraction interface so both custom and BlockingQueue implementations plug in
interface Buffer<T> {
    void put(T item) throws InterruptedException;
    T take() throws InterruptedException;
}

// Step 6 Adapter for java.util.concurrent.ArrayBlockingQueue
final class BlockingBuffer<T> implements Buffer<T> {
    private final BlockingQueue<T> q;

    public BlockingBuffer(int capacity) {
        this.q = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void put(T item) throws InterruptedException {
        q.put(item);
    }

    @Override
    public T take() throws InterruptedException {
        return q.take();
    }
}

// Step 2: Custom bounded buffer BoundedBuffer<T> with guarded blocks
class BoundedBuffer<T> implements Buffer<T> {
    private final ArrayDeque<T> items = new ArrayDeque<>();
    private final int capacity;

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public synchronized void put(T item) throws InterruptedException {
        while (items.size() == capacity) {
            wait();
        }
        items.addLast(item);
        assert items.size() <= capacity : "Buffer exceeded capacity!";
        notifyAll();
    }

    @Override
    public synchronized T take() throws InterruptedException {
        while (items.isEmpty()) {
            wait();
        }
        T item = items.removeFirst();
        assert item != null : "Took null from buffer!";
        notifyAll();
        return item;
    }
}

// Step 3: Producer implementation
class Producer implements Runnable {
    private final Buffer<Order> queue;
    private final int startId;
    private final int count;
    private final int poisonPillsToSend;
    private final Random random = new Random();

    public Producer(Buffer<Order> queue, int startId, int count, int poisonPillsToSend) {
        this.queue = queue;
        this.startId = startId;
        this.count = count;
        this.poisonPillsToSend = poisonPillsToSend;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < count; i++) {
                int id = startId + i;
                Order order = new Order(id, "item-" + id, random.nextInt(5) + 1);
                queue.put(order);
                Thread.sleep(random.nextInt(21)); // 0-20 ms random pause
            }
            // Put poison pill per consumer (id = -1)
            for (int i = 0; i < poisonPillsToSend; i++) {
                queue.put(new Order(-1, "POISON_PILL", 0));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Step 4: Consumer implementation
class Consumer implements Runnable {
    private final Buffer<Order> queue;
    private final AtomicInteger processedCounter;
    private final Random random = new Random();

    public Consumer(Buffer<Order> queue, AtomicInteger processedCounter) {
        this.queue = queue;
        this.processedCounter = processedCounter;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        try {
            while (true) {
                Order order = queue.take();
                if (order.id() == -1) {
                    System.out.println(name + " received poison pill, stopping");
                    break;
                }
                // Simulate processing order
                Thread.sleep(random.nextInt(26) + 5); // 5-30 ms sleep
                System.out.println(name + " processed " + order);
                processedCounter.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Step 5: Main using BoundedBuffer
public class Main {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Buffer<Order> queue = new BoundedBuffer<>(5);
        AtomicInteger totalProcessed = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();

        // 3 Consumers
        for (int i = 1; i <= 3; i++) {
            Thread c = new Thread(new Consumer(queue, totalProcessed), "consumer-" + i);
            threads.add(c);
        }

        // 2 Producers: 50 items each; 3 poison pills total (producer-1 sends 2, producer-2 sends 1)
        Thread p1 = new Thread(new Producer(queue, 1, 50, 2), "producer-1");
        Thread p2 = new Thread(new Producer(queue, 51, 50, 1), "producer-2");
        threads.add(p1);
        threads.add(p2);

        // Start all threads
        for (Thread t : threads) {
            t.start();
        }

        // Wait for all threads to terminate
        for (Thread t : threads) {
            t.join();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.printf("Total processed: %d (expected 100) in %d ms%n", totalProcessed.get(), elapsed);
        assert totalProcessed.get() == 100 : "Expected 100 processed items, got " + totalProcessed.get();
    }
}

// Step 6: Main2 reusing Producer/Consumer with ArrayBlockingQueue
class Main2 {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Buffer<Order> queue = new BlockingBuffer<>(5);
        AtomicInteger totalProcessed = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            threads.add(new Thread(new Consumer(queue, totalProcessed), "consumer-" + i));
        }

        threads.add(new Thread(new Producer(queue, 1, 50, 2), "producer-1"));
        threads.add(new Thread(new Producer(queue, 51, 50, 1), "producer-2"));

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.printf("Total processed: %d (expected 100) in %d ms%n", totalProcessed.get(), elapsed);
        assert totalProcessed.get() == 100 : "Expected 100 processed items, got " + totalProcessed.get();
    }
}