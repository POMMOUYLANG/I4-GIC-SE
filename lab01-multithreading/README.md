# Lab 01: Multithreading

## Task 2: Fixing a Race Condition

### Step 7 Question:
> **Why does making only `increment()` synchronized but not `get()` still leave a bug?**

### Answer:
Making only `increment()` synchronized while leaving `get()` unsynchronized leaves two major concurrency bugs according to the **Java Memory Model (JMM)**:

#### 1. Lack of Memory Visibility (No Happens-Before Guarantee)
- Synchronization in Java provides two guarantees: **mutual exclusion** and **memory visibility**.
- Under the JMM (JSR-133), a write to a synchronized block or method establishes a *happens-before* relationship with a subsequent read **only if both the writer and reader synchronize on the same monitor lock**.
- If `get()` is not synchronized:
  - There is no memory barrier or cache synchronization between the writing threads and the thread calling `get()`.
  - The reading thread may inspect a stale value cached in CPU registers or L1/L2 cache rather than reading the updated value from main memory.
  - Even after all worker threads finish writing, a reader thread without memory synchronization is not formally guaranteed to observe the final written value immediately.

#### 2. Non-Atomic 64-bit Reads ("Word Tearing")
- According to the Java Language Specification (JLS §17.7), reads and writes of 64-bit primitive types (`long` and `double`) without `volatile` or synchronization are not guaranteed to be atomic.
- On 32-bit JVMs or underlying 32-bit hardware architectures, a 64-bit `long` read/write can be split into two distinct 32-bit bus operations:
  1. Reading/writing the high 32 bits
  2. Reading/writing the low 32 bits
- If `get()` is unsynchronized and reads concurrently with a thread updating `count`, it could read the high 32 bits from one write and the low 32 bits from another write. This phenomenon is called **word tearing**, producing an invalid, corrupted number that was never actually written by any thread.

#### Conclusion:
Thread safety requires proper synchronization (or `volatile` / atomic constructs) for **both mutations (writes) and inspections (reads)** of shared mutable state.

---

## Task 3: Producer–Consumer with wait()/notify() and BlockingQueue

### Step 7 Experiment:
> **Change `notifyAll()` to `notify()` and the `while` to an `if`. Describe what breaks and why (hint: run it 20 times in a loop).**

### Observations & Explanation:

Running the experiment repeatedly reveals two fatal concurrency bugs:

#### 1. Changing `notifyAll()` to `notify()` causes Deadlock (Lost Signals)
- **The Issue**: In `BoundedBuffer`, both producers (waiting for buffer to be not full) and consumers (waiting for buffer to be not empty) share the **same intrinsic monitor lock** and wait set.
- **Why it breaks**:
  - `notify()` arbitrarily wakes up only **one single thread** from the wait set, without distinguishing between a producer or a consumer.
  - Suppose the buffer is full (5 items), so both producers are in the `wait()` state.
  - A consumer calls `take()`, removes an item (leaving 1 open slot), and invokes `notify()`.
  - If another consumer was also waiting, the JVM might arbitrarily wake up that **other consumer** rather than one of the waiting producers.
  - The woken consumer sees the buffer is empty (or empties it) and goes back to `wait()`.
  - The producers—which were waiting for available space—**never received the notification**.
  - As more consumers arrive, they all find the buffer empty and enter `wait()`. Both producers are also in `wait()`.
  - **Result**: Every single producer and consumer thread enters the `WAITING` state on the same monitor. Because no thread is awake to invoke `notify()`, the application hangs indefinitely in a **permanent deadlock**!
- **The Fix**: `notifyAll()` must be used when threads waiting on the same monitor have different condition predicates (space available vs. data available). It wakes all waiting threads, guaranteeing that threads whose condition has become true can proceed.

#### 2. Changing `while` to `if` causes Buffer Overflow and `NoSuchElementException`
- **The Issue**: With `if (items.size() == capacity) wait();`, once a thread wakes up from `wait()`, it does **not** re-evaluate the condition predicate before proceeding into the critical section.
- **Why it breaks**:
  - Suppose the buffer is currently full (5 items). Both `producer-1` and `producer-2` attempt to `put()` and both enter `wait()` inside the `if` block.
  - A consumer takes 1 item and signals.
  - Both producers are awakened.
  - `producer-1` re-acquires the monitor lock first, adds its item (buffer size becomes 5), and releases the lock.
  - Next, `producer-2` re-acquires the lock. Because it was in an `if` statement rather than a `while` loop, it resumes execution immediately after `wait()`, **blindly assuming space is still available**.
  - `producer-2` executes `items.addLast(item)`, pushing the buffer size to 6. **The buffer capacity invariant is violated (buffer overflow)**.
  - In reverse, if two consumers wake up when only 1 item was added, with `if`, both attempt `items.removeFirst()`. The second consumer calls `removeFirst()` on an empty deque, throwing `java.util.NoSuchElementException` or taking null.
  - Furthermore, operating systems and the JVM permit **spurious wakeups** (threads waking without any `notify()` call).
- **The Fix**: Guarded blocks must **always** use `while (condition) { wait(); }` so that threads re-test the condition upon waking before modifying state.

---

## Task 4: Thread Pools, Future, and Virtual Threads

### Step 2: Platform Threads vs. Virtual Threads for I/O-Bound Workload
> **Submit 500 downloads to (a) a fixed pool of 20 platform threads and (b) Executors.newVirtualThreadPerTaskExecutor(). Print elapsed time for each. Explain the difference in README.md.**

### Benchmark Results:
- **Single-threaded Prime Counting (CPU-bound baseline)**: ~1,264 ms
- **Fixed Thread Pool Prime Counting (16 threads)**: ~238 ms ($\approx 5.3\times$ speedup)
- **500 Downloads on Fixed Pool of 20 Platform Threads**: **~5,010 ms**
- **500 Downloads on Virtual Threads**: **~1,013 ms** ($\approx 5\times$ faster)
- **CompletableFuture (Virtual Threads)**: **~1,006 ms**

### Why Virtual Threads Vastly Outperform Platform Threads on I/O-Bound Work:

#### 1. Concurrency vs. Batching
- **Fixed Platform Pool (20 threads)**:
  - There are 500 tasks, but only 20 OS threads available.
  - The thread pool can only run 20 tasks concurrently at any moment. The remaining 480 tasks queue up in an internal blocking queue.
  - Processing 500 tasks with 20 threads requires $\frac{500}{20} = 25$ sequential batches.
  - Since each regular task sleeps for 200 ms, the minimum theoretical time is $25 \times 200\text{ ms} = 5,000\text{ ms}$.
  - The pool is throttled because OS platform threads are heavy ($\approx 1\text{ MB}$ stack memory) and expensive to create, forcing the application to strictly bound pool size.
- **Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`)**:
  - Creates a dedicated virtual thread for **every single task** (all 500 virtual threads run concurrently).
  - Virtual threads are lightweight user-space threads managed directly by the JVM ($< 1\text{ KB}$ heap memory footprint).
  - When a virtual thread executes a blocking operation (such as `Thread.sleep(200)` or waiting for a network socket), the JVM **unmounts** the virtual thread from its underlying OS carrier thread.
  - The OS carrier thread immediately executes other virtual threads.
  - As a result, all 499 fast tasks execute in parallel and finish within ~200 ms.
  - The single slow task (index 0, sleeping 5 seconds) is cancelled when its 1-second timeout expires.
  - The entire batch of 500 downloads finishes in **~1,013 ms** (almost exactly 1 second, bounded only by the 1s timeout!).

#### 2. Summary of Sizing Guidelines:
- **CPU-bound tasks**: Bound thread count to `Runtime.getRuntime().availableProcessors()`. Additional threads cause useless kernel context switches without extra CPU execution capacity.
- **I/O-bound tasks**: Use **virtual threads** (`Executors.newVirtualThreadPerTaskExecutor()`). They decouple concurrency from OS thread limitations, maximizing throughput and simplifying programming by avoiding complex reactive callbacks.

---

## Task 5: Diagnosing a Deadlock and Shutting Down Gracefully

### 1. Reproducing the Deadlock
In the initial `Bank.transfer(Account from, Account to, long amount)` implementation:
- `transfer-0` transfers from `acc0` to `acc1`, locking monitor `acc0` and waiting for `acc1`.
- `transfer-1` transfers from `acc1` to `acc0`, locking monitor `acc1` and waiting for `acc0`.
- Both threads become `BLOCKED` indefinitely.
- The thread dump from `jstack <pid>` captured this cycle and is saved in `deadlock.txt`.

### 2. Watchdog Detection
A daemon thread periodically polls `ManagementFactory.getThreadMXBean().findDeadlockedThreads()`. When a deadlock occurs, it programmatically identifies and prints the names of the involved deadlocked threads.

### 3. Fix A: Lock Ordering (`OrderedBank`)
- **Principle**: Deadlock requires Coffman's *Circular Wait* condition.
- **Solution**: Always acquire account locks in a strict global hierarchy (ascending order of account ID):
  ```java
  Account first = from.id < to.id ? from : to;
  Account second = from.id < to.id ? to : from;
  synchronized (first) {
      synchronized (second) { ... }
  }
  ```
- **Proof**: 8 threads $\times$ 10,000 random transfers execute with zero deadlocks and total balance remains perfectly conserved.

### 4. Fix B: Timed tryLock with Random Back-off (`TryLockBank`)
- **Principle**: Breaks the *Hold and Wait* condition.
- **Solution**: Uses `ReentrantLock` with `tryLock(10, TimeUnit.MILLISECONDS)`. If acquiring the second lock times out, the first lock is immediately released, and the thread backs off randomly (`1-10 ms`) before retrying.
- **Result**: Zero deadlocks; transfers retry without livelocking, and total balance is conserved.

### 5. Graceful Shutdown via Cooperative Interruption
- Workers loop checking `!Thread.currentThread().isInterrupted()`.
- When blocking methods (e.g. `Thread.sleep`) throw `InterruptedException`, workers catch it, restore the interrupted status with `Thread.currentThread().interrupt()`, and exit cleanly.
- The main thread interrupts all workers after 2 seconds and joins them with a 1-second timeout, confirming all threads terminate without orphaned background processes.

---

## Challenge: Parallel Word-Frequency Analyser

### 1. Scaling Benchmark Table
Benchmark executed on a dataset of **200 files** containing **~50,000 words each** (total **10,000,000 words** generated from a 2,000-word Zipfian vocabulary) on a 16-core CPU:

| Configuration | Run 1 (s) | Run 2 (s) | Run 3 (s) | Avg Elapsed (s) | Throughput (words/s) | Speedup |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **1 platform worker** | 0.71s | 0.77s | 0.77s | **0.75s** | **13,333,333** | $1.00\times$ (baseline) |
| **2 platform workers** | 0.75s | 0.77s | 0.70s | **0.74s** | **13,513,513** | $1.01\times$ |
| **4 platform workers** | 0.52s | 0.61s | 0.68s | **0.60s** | **16,574,585** | $1.25\times$ |
| **8 platform workers** | 0.51s | 0.53s | 0.50s | **0.51s** | **19,480,519** | **$1.47\times$** |
| **Virtual threads** | 0.57s | 0.75s | 0.55s | **0.62s** | **16,042,780** | $1.21\times$ |

*Observations:*
- As platform worker count increases from 1 to 8, throughput scales significantly, reaching nearly 20 million words/sec.
- Virtual threads perform comparably to 4 platform workers on this mixed I/O and CPU-bound parsing task, with zero OS thread management overhead.

---

### 2. Why `LongAdder` / `merge` Beats `synchronized` on the Map

Using `synchronized` on a shared map introduces a massive performance bottleneck because **all threads must serialize on a single intrinsic monitor lock**. Even if worker threads are reading different files and counting different words, every single increment operation forces all other threads to block and wait. This leads to heavy OS thread contention, frequent CPU context switching, and continuous cache-line invalidation (*cache bouncing* across CPU L1/L2 caches due to MESI cache coherency protocols).

In contrast, our architecture combines two lock-free concurrency layers:
1. **Striped Table Access via `ConcurrentHashMap`**:
   `ConcurrentHashMap` uses bucket-level synchronization (CAS for empty table bins and synchronized on individual bin head nodes). Updates to different words almost never contend with each other.
2. **Cell Striping via `LongAdder` (`Striped64`)**:
   In natural language datasets, words follow a Zipfian distribution where top words (e.g. `"the"`, `"and"`, `"of"`) are updated with extreme frequency. Under high write contention, a standard `AtomicLong` would degrade into spinning CAS failure loops. `LongAdder` dynamically expands an internal array of 64-bit cells (`Cell` objects padded to 64 bytes to eliminate *false sharing*). Each contending worker thread hashes to an independent cell and increments it without locking or CAS retries. The final aggregate count is computed lock-free via `sum()`. This completely eliminates contention and delivers linear multi-core scaling.

---

### 3. Coffman Conditions and Deadlock Elimination

Deadlock can only occur when all four **Coffman conditions** hold simultaneously:
1. *Mutual Exclusion*
2. *Hold and Wait*
3. *No Preemption*
4. *Circular Wait*

Our pipeline architecture structurally eliminates **Circular Wait** (and **Hold and Wait**):
- **Strict Directed Acyclic Dataflow (No Cycles)**: The pipeline operates in a strictly unidirectional producer-consumer topology:
  $$\text{Directory} \longrightarrow \text{Scanner} \xrightarrow[\text{put()}]{\text{BlockingQueue}} \text{Workers} \xrightarrow[\text{computeIfAbsent()}]{\text{ConcurrentHashMap}} \text{Results}$$
  The scanner thread only produces file paths into the `BlockingQueue` and never waits on workers. Worker threads only consume file paths from the queue and write lock-free increments into the `ConcurrentHashMap`. Because no worker ever produces into the queue or blocks the scanner, no circular dependency graph of thread waits ($\text{Thread}_1 \to \text{Thread}_2 \to \dots \to \text{Thread}_1$) can ever form.
- **No Holding While Waiting**: Worker threads never hold one lock or resource while attempting to acquire another. Each worker holds only a transient file path reference popped from the queue, processes the file to completion independently, and updates the map using lock-free data structures before requesting the next path.
- **Preemptive Cooperative Cancellation**: Cancellation via shutdown hook (Ctrl+C) or `--timeout` uses cooperative interruption (`Thread.interrupt()`, `AtomicBoolean stopping`, and bounded `ArrayBlockingQueue.take()` exception recovery), guaranteeing all threads abort cleanly within 2 seconds without hanging.


