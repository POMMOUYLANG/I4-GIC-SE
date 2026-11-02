# 🎮 Kahoot! — Java Basics → Lab 01 Multithreading
### 40 Questions | From Zero Java → Lab 1 Expert

> [!NOTE]
> Progress from absolute Java beginner → Lab 1 multithreading expert.
> - ✅ = correct | ❌ = wrong
> - **Quiz** = 4 choices | **True/False** = 2 choices

---

# 🟩 LEVEL 1 — Java Absolute Basics (Q1–Q8)
> *For students who are brand new to Java*

---

### Q1 — Quiz | ⏱ 15 sec

**Question:**
> Which keyword is used to create an object from a class in Java?

| Color | Answer | |
|---|---|---|
| 🔴 Red | `create` | ❌ |
| 🔵 Blue | `new` | ✅ |
| 🟡 Yellow | `make` | ❌ |
| 🟢 Green | `object` | ❌ |

---

### Q2 — True or False | ⏱ 15 sec

**Question:**
> In Java, a `String` is a primitive data type, just like `int` and `boolean`.

| | Answer | |
|---|---|---|
| ✔ | True | ❌ |
| ✘ | False | ✅ |

> 💡 `String` is a **class** (reference type). Primitive types are: `int`, `long`, `double`, `boolean`, `char`, `byte`, `short`, `float`.

---

### Q3 — Quiz | ⏱ 15 sec

**Question:**
> What does this Java code print?
> ```java
> System.out.println(10 / 3);
> ```

| Color | Answer | |
|---|---|---|
| 🔴 Red | `3.33` | ❌ |
| 🔵 Blue | `3` | ✅ |
| 🟡 Yellow | `4` | ❌ |
| 🟢 Green | Error | ❌ |

> 💡 Integer division in Java truncates the decimal part. Use `10.0 / 3` for `3.33`.

---

### Q4 — Quiz | ⏱ 20 sec

**Question:**
> Which access modifier makes a class member accessible ONLY within its own class?

| Color | Answer | |
|---|---|---|
| 🔴 Red | `public` | ❌ |
| 🔵 Blue | `protected` | ❌ |
| 🟡 Yellow | `private` | ✅ |
| 🟢 Green | `default` | ❌ |

---

### Q5 — True or False | ⏱ 15 sec

**Question:**
> In Java, every class automatically extends `java.lang.Object` if no other parent class is specified.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

> 💡 `Object` is the root of the Java class hierarchy. All classes inherit `toString()`, `equals()`, `hashCode()` from it.

---

### Q6 — Quiz | ⏱ 20 sec

**Question:**
> What is the correct way to define a method that returns nothing in Java?

| Color | Answer | |
|---|---|---|
| 🔴 Red | `null methodName()` | ❌ |
| 🔵 Blue | `void methodName()` | ✅ |
| 🟡 Yellow | `empty methodName()` | ❌ |
| 🟢 Green | `return methodName()` | ❌ |

---

### Q7 — Quiz | ⏱ 20 sec

**Question:**
> What does the `interface` keyword mean in Java?

| Color | Answer | |
|---|---|---|
| 🔴 Red | A class that cannot be instantiated | ❌ |
| 🔵 Blue | A contract defining method signatures that classes must implement | ✅ |
| 🟡 Yellow | A type of loop | ❌ |
| 🟢 Green | A way to create database connections | ❌ |

---

### Q8 — True or False | ⏱ 15 sec

**Question:**
> In Java, you can extend multiple classes at the same time using `extends ClassA, ClassB`.

| | Answer | |
|---|---|---|
| ✔ | True | ❌ |
| ✘ | False | ✅ |

> 💡 Java only allows **single inheritance** for classes. You can implement multiple **interfaces** though.

---

# 🟨 LEVEL 2 — Java Fundamentals (Q9–Q16)
> *Exception handling, collections, lambdas — needed for Lab 1*

---

### Q9 — Quiz | ⏱ 20 sec

**Question:**
> What happens if a `checked exception` is thrown but NOT caught or declared with `throws`?

| Color | Answer | |
|---|---|---|
| 🔴 Red | The program continues normally | ❌ |
| 🔵 Blue | The JVM ignores it silently | ❌ |
| 🟡 Yellow | The code will NOT compile | ✅ |
| 🟢 Green | The exception is automatically caught by `main()` | ❌ |

---

### Q10 — True or False | ⏱ 15 sec

**Question:**
> `InterruptedException` is a checked exception, meaning you MUST handle it with `try-catch` or `throws`.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

> 💡 `InterruptedException extends Exception`, so it is **checked**. This is why all `Thread.sleep()` calls require a `try-catch`.

---

### Q11 — Quiz | ⏱ 20 sec

**Question:**
> What does a **lambda expression** `() -> { ... }` represent in Java?

| Color | Answer | |
|---|---|---|
| 🔴 Red | A new class definition | ❌ |
| 🔵 Blue | An anonymous implementation of a functional interface (like `Runnable`) | ✅ |
| 🟡 Yellow | A type of `for` loop | ❌ |
| 🟢 Green | A method that returns void only | ❌ |

---

### Q12 — Quiz | ⏱ 20 sec

**Question:**
> In Lab 1, threads use `Thread.sleep(200)`. What does this do?

| Color | Answer | |
|---|---|---|
| 🔴 Red | Permanently stops the thread | ❌ |
| 🔵 Blue | Pauses the current thread for at least 200 milliseconds | ✅ |
| 🟡 Yellow | Waits for another thread to finish | ❌ |
| 🟢 Green | Makes the thread run 200 times faster | ❌ |

---

### Q13 — True or False | ⏱ 15 sec

**Question:**
> A `try-finally` block in Java guarantees that the `finally` block ALWAYS runs, even if an exception is thrown.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

> 💡 This is why `lock.unlock()` is always placed in a `finally` block — to guarantee the lock is released even if an exception occurs.

---

### Q14 — Quiz | ⏱ 20 sec

**Question:**
> What is an `ArrayList` in Java?

| Color | Answer | |
|---|---|---|
| 🔴 Red | A fixed-size array that cannot grow | ❌ |
| 🔵 Blue | A dynamic resizable list backed by an array | ✅ |
| 🟡 Yellow | A type of HashMap | ❌ |
| 🟢 Green | A queue that blocks when empty | ❌ |

---

### Q15 — Quiz | ⏱ 20 sec

**Question:**
> What is a `record` in Java (used in Lab 1 Task 3 as `record Order(int id, String item, int qty){}`)?

| Color | Answer | |
|---|---|---|
| 🔴 Red | A special type of loop | ❌ |
| 🔵 Blue | A compact immutable data class that auto-generates constructor, getters, equals, hashCode | ✅ |
| 🟡 Yellow | A way to write to a file | ❌ |
| 🟢 Green | A type of exception | ❌ |

---

### Q16 — True or False | ⏱ 15 sec

**Question:**
> `Callable<T>` is like `Runnable`, except `Callable` can return a value AND throw a checked exception.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

> 💡 `Runnable.run()` returns `void`. `Callable<T>.call()` returns `T` and can throw `Exception`.

---

# 🟧 LEVEL 3 — Lab Task 1 & 2: Threads & Race Conditions (Q17–Q24)

---

### Q17 — Quiz | ⏱ 20 sec

**Question:**
> In Lab Task 1, what are the FOUR ways to create a thread in Java?

| Color | Answer | |
|---|---|---|
| 🔴 Red | `Thread.create()`, `Runnable`, `Lambda`, `CompletableFuture` | ❌ |
| 🔵 Blue | Extending `Thread`, implementing `Runnable`, Lambda expression, Virtual thread | ✅ |
| 🟡 Yellow | `new Thread()`, `Thread.fork()`, `Thread.spawn()`, `Thread.virtual()` | ❌ |
| 🟢 Green | `synchronized`, `wait()`, `notify()`, `join()` | ❌ |

---

### Q18 — Quiz | ⏱ 20 sec

**Question:**
> What is the difference between calling `thread.start()` vs `thread.run()` in Java?

| Color | Answer | |
|---|---|---|
| 🔴 Red | They are identical — both start a new thread | ❌ |
| 🔵 Blue | `start()` creates a new thread; `run()` executes on the CURRENT thread as a regular method call | ✅ |
| 🟡 Yellow | `run()` creates a new thread; `start()` just registers the thread | ❌ |
| 🟢 Green | `start()` is for daemon threads only | ❌ |

---

### Q19 — True or False | ⏱ 15 sec

**Question:**
> Virtual threads (introduced in Java 21) are managed by the JVM and use less memory than platform threads.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

> 💡 Platform threads use ~1 MB stack. Virtual threads use only a few hundred bytes on the heap.

---

### Q20 — Quiz | ⏱ 20 sec

**Question:**
> In Lab Task 2, `count++` on a `long` is NOT thread-safe. Why?

| Color | Answer | |
|---|---|---|
| 🔴 Red | `long` is too large to store in memory | ❌ |
| 🔵 Blue | `count++` is 3 separate steps: READ, ADD 1, WRITE — threads can interleave and overwrite each other | ✅ |
| 🟡 Yellow | Java's JVM automatically prevents concurrent reads | ❌ |
| 🟢 Green | `long` cannot be incremented with `++` | ❌ |

---

### Q21 — Quiz | ⏱ 20 sec

**Question:**
> Which of these counter implementations is the FASTEST under high thread contention? (from Lab Task 2)

| Color | Answer | |
|---|---|---|
| 🔴 Red | `SynchronizedCounter` (synchronized methods) | ❌ |
| 🔵 Blue | `LockCounter` (ReentrantLock) | ❌ |
| 🟡 Yellow | `AtomicCounter` (AtomicLong) | ❌ |
| 🟢 Green | `LongAdderCounter` (LongAdder — striped cells) | ✅ |

---

### Q22 — True or False | ⏱ 15 sec

**Question:**
> After calling `thread.join()`, the main thread is guaranteed that the joined thread has fully completed its `run()` method.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

---

### Q23 — Quiz | ⏱ 25 sec

**Question:**
> According to the Java Memory Model (JMM), why must BOTH `increment()` AND `get()` be `synchronized`, not just `increment()`?

| Color | Answer | |
|---|---|---|
| 🔴 Red | Because `get()` takes longer to execute | ❌ |
| 🔵 Blue | Without synchronized `get()`, a reading thread may see a stale cached value — no happens-before guarantee exists | ✅ |
| 🟡 Yellow | Because `increment()` is slower and needs help from `get()` | ❌ |
| 🟢 Green | `get()` must be synchronized to prevent the counter from resetting to 0 | ❌ |

---

### Q24 — True or False | ⏱ 15 sec

**Question:**
> `AtomicLong.incrementAndGet()` is safe to call from multiple threads without `synchronized` because it uses a hardware CAS (Compare-And-Swap) instruction.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

---

# 🟥 LEVEL 4 — Lab Task 3 & 4: Producer-Consumer & Thread Pools (Q25–Q32)

---

### Q25 — Quiz | ⏱ 20 sec

**Question:**
> In Lab Task 3, `BoundedBuffer` uses `while (items.isEmpty()) { wait(); }`. Why is `while` used instead of `if`?

| Color | Answer | |
|---|---|---|
| 🔴 Red | `while` is faster than `if` | ❌ |
| 🔵 Blue | After waking from `wait()`, the thread must re-check the condition because spurious wakeups can occur and another thread may have taken the item | ✅ |
| 🟡 Yellow | `if` cannot be used inside a `synchronized` block | ❌ |
| 🟢 Green | `while` allows the thread to notify other threads automatically | ❌ |

---

### Q26 — Quiz | ⏱ 20 sec

**Question:**
> In Task 3, why is `notifyAll()` used instead of `notify()`?

| Color | Answer | |
|---|---|---|
| 🔴 Red | `notifyAll()` is faster than `notify()` | ❌ |
| 🔵 Blue | Because both producers and consumers share the same wait set, `notify()` might wake the wrong type of thread causing deadlock | ✅ |
| 🟡 Yellow | `notify()` is deprecated in Java 21 | ❌ |
| 🟢 Green | `notifyAll()` only wakes threads that are producers | ❌ |

---

### Q27 — True or False | ⏱ 15 sec

**Question:**
> The Poison Pill pattern (sending a special sentinel value like `Order(id=-1)`) is a way to signal consumers to stop gracefully without using `interrupt()`.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

---

### Q28 — Quiz | ⏱ 20 sec

**Question:**
> In Lab Task 4, what interface must a task implement to be submitted to an `ExecutorService` and return a result?

| Color | Answer | |
|---|---|---|
| 🔴 Red | `Runnable` | ❌ |
| 🔵 Blue | `Callable<T>` | ✅ |
| 🟡 Yellow | `Thread` | ❌ |
| 🟢 Green | `Future<T>` | ❌ |

---

### Q29 — Quiz | ⏱ 20 sec

**Question:**
> What exception does `future.get(1, TimeUnit.SECONDS)` throw when the task does not finish within 1 second?

| Color | Answer | |
|---|---|---|
| 🔴 Red | `ExecutionException` | ❌ |
| 🔵 Blue | `InterruptedException` | ❌ |
| 🟡 Yellow | `TimeoutException` | ✅ |
| 🟢 Green | `RuntimeException` | ❌ |

---

### Q30 — Quiz | ⏱ 25 sec

**Question:**
> In Task 4, 500 downloads are submitted to a **fixed pool of 20 platform threads**. Each download takes 200 ms. What is the approximate total time?

| Color | Answer | |
|---|---|---|
| 🔴 Red | ~200 ms (all run at once) | ❌ |
| 🔵 Blue | ~1,000 ms | ❌ |
| 🟡 Yellow | ~5,000 ms (500 ÷ 20 = 25 batches × 200 ms) | ✅ |
| 🟢 Green | ~10,000 ms | ❌ |

---

### Q31 — True or False | ⏱ 15 sec

**Question:**
> `Executors.newVirtualThreadPerTaskExecutor()` creates one virtual thread per submitted task, making it ideal for I/O-bound workloads.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

---

### Q32 — Quiz | ⏱ 20 sec

**Question:**
> For CPU-bound tasks, what is the IDEAL number of threads to use in a thread pool?

| Color | Answer | |
|---|---|---|
| 🔴 Red | As many as possible (1000+) | ❌ |
| 🔵 Blue | Equal to the number of available CPU cores (`Runtime.getRuntime().availableProcessors()`) | ✅ |
| 🟡 Yellow | Always exactly 2 | ❌ |
| 🟢 Green | 1 thread only | ❌ |

---

# 🔴 LEVEL 5 — Lab Task 5: Deadlock & Graceful Shutdown (Q33–Q40)

---

### Q33 — Quiz | ⏱ 20 sec

**Question:**
> In Lab Task 5, `Bank.transfer()` uses nested `synchronized` blocks. What is the root cause of the deadlock?

| Color | Answer | |
|---|---|---|
| 🔴 Red | The `balance` field is `private` | ❌ |
| 🔵 Blue | Thread A locks `acc0` waiting for `acc1`, while Thread B locks `acc1` waiting for `acc0` — Circular Wait | ✅ |
| 🟡 Yellow | The `withdraw()` method is not synchronized | ❌ |
| 🟢 Green | There are too many threads running at the same time | ❌ |

---

### Q34 — True or False | ⏱ 15 sec

**Question:**
> `OrderedBank` prevents deadlock by always acquiring account locks in ascending order of `Account ID`, regardless of transfer direction.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

---

### Q35 — Quiz | ⏱ 20 sec

**Question:**
> `TryLockBank` uses `tryLock(10, TimeUnit.MILLISECONDS)`. What does it do if it CANNOT get the second lock?

| Color | Answer | |
|---|---|---|
| 🔴 Red | Waits forever until the lock is available | ❌ |
| 🔵 Blue | Releases the first lock, sleeps a random time, then retries from scratch | ✅ |
| 🟡 Yellow | Throws an exception and crashes | ❌ |
| 🟢 Green | Steals the lock from the other thread by force | ❌ |

---

### Q36 — True or False | ⏱ 15 sec

**Question:**
> In Lab Task 5, `ReentrantLock` can use `tryLock()` with a timeout, but `synchronized` cannot — it always waits forever.

| | Answer | |
|---|---|---|
| ✔ | True | ✅ |
| ✘ | False | ❌ |

---

### Q37 — Quiz | ⏱ 20 sec

**Question:**
> What is the purpose of the **watchdog thread** in Lab Task 5?

| Color | Answer | |
|---|---|---|
| 🔴 Red | To speed up the bank transfers | ❌ |
| 🔵 Blue | To periodically check for deadlocks using `ThreadMXBean.findDeadlockedThreads()` and report them | ✅ |
| 🟡 Yellow | To count the number of accounts | ❌ |
| 🟢 Green | To restart any threads that stop unexpectedly | ❌ |

---

### Q38 — Quiz | ⏱ 25 sec

**Question:**
> In the graceful shutdown demo, what is the CORRECT sequence when a worker receives an interrupt while sleeping?

| Color | Answer | |
|---|---|---|
| 🔴 Red | Ignore the `InterruptedException` and keep running | ❌ |
| 🔵 Blue | Catch `InterruptedException` → call `Thread.currentThread().interrupt()` → exit the loop | ✅ |
| 🟡 Yellow | Catch `InterruptedException` → call `Thread.sleep(1000)` again | ❌ |
| 🟢 Green | Catch `InterruptedException` → call `System.exit(0)` | ❌ |

---

### Q39 — True or False | ⏱ 15 sec

**Question:**
> In Lab Task 5, a thread blocked on a `synchronized` monitor lock is immediately unblocked when another thread calls `interrupt()` on it.

| | Answer | |
|---|---|---|
| ✔ | True | ❌ |
| ✘ | False | ✅ |

> 💡 `synchronized` does NOT respond to `interrupt()`. The thread stays blocked until it acquires the monitor, then checks `isInterrupted()` on the next loop iteration.

---

### Q40 — Quiz | ⏱ 25 sec

**Question:**
> Which of these is the COMPLETE set of Coffman's four necessary conditions for a deadlock?

| Color | Answer | |
|---|---|---|
| 🔴 Red | Thread safety, Race condition, Starvation, Livelock | ❌ |
| 🔵 Blue | Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait | ✅ |
| 🟡 Yellow | Synchronization, Notification, Interruption, Termination | ❌ |
| 🟢 Green | Lock, Unlock, Wait, Notify | ❌ |

---

## 📊 Full Question Index (Q1–Q40)

| Q | Level | Type | Topic |
|---|---|---|---|
| 1 | 🟩 Java Basics | Quiz | `new` keyword |
| 2 | 🟩 Java Basics | True/False | String is not a primitive |
| 3 | 🟩 Java Basics | Quiz | Integer division result |
| 4 | 🟩 Java Basics | Quiz | `private` access modifier |
| 5 | 🟩 Java Basics | True/False | Every class extends `Object` |
| 6 | 🟩 Java Basics | Quiz | `void` return type |
| 7 | 🟩 Java Basics | Quiz | `interface` keyword |
| 8 | 🟩 Java Basics | True/False | No multiple class inheritance |
| 9 | 🟨 Java Fundamentals | Quiz | Checked exception compile error |
| 10 | 🟨 Java Fundamentals | True/False | `InterruptedException` is checked |
| 11 | 🟨 Java Fundamentals | Quiz | Lambda as functional interface |
| 12 | 🟨 Java Fundamentals | Quiz | `Thread.sleep()` behavior |
| 13 | 🟨 Java Fundamentals | True/False | `finally` always runs |
| 14 | 🟨 Java Fundamentals | Quiz | `ArrayList` definition |
| 15 | 🟨 Java Fundamentals | Quiz | Java `record` keyword |
| 16 | 🟨 Java Fundamentals | True/False | `Callable` vs `Runnable` |
| 17 | 🟧 Task 1 & 2 | Quiz | 4 ways to create threads |
| 18 | 🟧 Task 1 & 2 | Quiz | `start()` vs `run()` |
| 19 | 🟧 Task 1 & 2 | True/False | Virtual threads lightweight |
| 20 | 🟧 Task 1 & 2 | Quiz | Why `count++` is not thread-safe |
| 21 | 🟧 Task 1 & 2 | Quiz | Fastest counter: LongAdder |
| 22 | 🟧 Task 1 & 2 | True/False | `join()` guarantees completion |
| 23 | 🟧 Task 1 & 2 | Quiz | JMM: both read and write must sync |
| 24 | 🟧 Task 1 & 2 | True/False | `AtomicLong` uses CAS |
| 25 | 🟥 Task 3 & 4 | Quiz | `while` vs `if` in guarded block |
| 26 | 🟥 Task 3 & 4 | Quiz | `notifyAll()` vs `notify()` |
| 27 | 🟥 Task 3 & 4 | True/False | Poison Pill pattern |
| 28 | 🟥 Task 3 & 4 | Quiz | `Callable` for results |
| 29 | 🟥 Task 3 & 4 | Quiz | `TimeoutException` from `future.get()` |
| 30 | 🟥 Task 3 & 4 | Quiz | 500 downloads / 20 threads = ~5000 ms |
| 31 | 🟥 Task 3 & 4 | True/False | `newVirtualThreadPerTaskExecutor()` |
| 32 | 🟥 Task 3 & 4 | Quiz | CPU-bound: match CPU core count |
| 33 | 🔴 Task 5 | Quiz | Deadlock cause in `Bank.transfer()` |
| 34 | 🔴 Task 5 | True/False | `OrderedBank` ascending lock order |
| 35 | 🔴 Task 5 | Quiz | `TryLockBank` retry behavior |
| 36 | 🔴 Task 5 | True/False | `synchronized` cannot timeout |
| 37 | 🔴 Task 5 | Quiz | Watchdog purpose |
| 38 | 🔴 Task 5 | Quiz | Correct `InterruptedException` handling |
| 39 | 🔴 Task 5 | True/False | `synchronized` ignores `interrupt()` |
| 40 | 🔴 Task 5 | Quiz | 4 Coffman conditions |

**Total: 40 Questions | 25 Quiz + 15 True/False**
