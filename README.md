# Java Concurrent Animated - Visualizing the Java Concurrent API
## A Swing Application Demonstrating Core Java Concurrency Concepts

Have you ever found yourself pondering how multiple threads work in Java and how to effectively utilize concepts like "notify", "notifyAll", and "wait"? Moreover, why is the usage of the "synchronized" keyword crucial in concurrent programming? Wouldn't it be amazing to witness this in action?

Introducing Java Concurrent Animated, a Swing application crafted by the Java Champion **[Victor Grazi](https://blogs.oracle.com/java/post/victor-grazi-java-champion)**. This program serves as an illustrative showcase of core Java Concurrency concepts, including but not limited to AtomicInteger, CountDownLatch, Semaphore, ReentrantLock, ReadWriteLock, and more.

```
Soon link to Medium Article
```

### 1. AtomicIntegerDemo
Description:
```
/**
 * Simple example of using AtomicInteger without any locks or synchronized method/block to fix data race[1]
 * [1] Two or more concurrent thread access the same memory location and at least one thread is modifying it
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/AtomicIntegerDemo.java)

Sample output:
```console
Total sum int (not correct): 1738590
Total sum AtomicInteger (correct): 2000000
```
### 2. ConditionVariableDemo
Description:
```
/**
 * Alternate 3 threads while consuming of a share resource using Condition variable
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/ConditionVariableDemo.java)

Sample output:
```console
Consumer 1 took the item 10
Consumer 0 took the item 9
Consumer 2 took the item 8
Consumer 1 took the item 7
Consumer 0 took the item 6
Consumer 2 took the item 5
Consumer 1 took the item 4
Consumer 0 took the item 3
Consumer 2 took the item 2
Consumer 1 took the item 1
```
### 3. CyclicBarrierDemo
Description:
```
/**
 * Order 10 threads to first operate the additions and then multiplications on a shared variable using CyclicBarrier[1]
 * [1] Releases when a number of threads are waiting
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/CyclicBarrierDemo.java)

Sample output:
```console
Counter Adder-0 changed the value and now is 5
Counter Adder-1 changed the value and now is 10
Counter Adder-2 changed the value and now is 15
Counter Adder-3 changed the value and now is 20
Counter Adder-4 changed the value and now is 25
Counter Multiplier-1 changed the value and now is 50
Counter Multiplier-0 changed the value and now is 100
Counter Multiplier-2 changed the value and now is 200
Counter Multiplier-3 changed the value and now is 400
Counter Multiplier-4 changed the value and now is 800
Final value and is 800
```
### 4. CountDownLatchDemo
Description:
```
/**
 * Order 10 threads to first operate the additions and then multiplications on a shared variable using CountDownLatch[1]
 * [1] Releases when a count value reaches zero
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/CountDownLatchDemo.java)

Sample output:
```console
Counter Adder-1 changed the value and now is 5
Counter Adder-0 changed the value and now is 10
Counter Adder-2 changed the value and now is 15
Counter Adder-3 changed the value and now is 20
Counter Adder-4 changed the value and now is 25
Counter Multiplier-4 changed the value and now is 50
Counter Multiplier-0 changed the value and now is 100
Counter Multiplier-1 changed the value and now is 200
Counter Multiplier-2 changed the value and now is 400
Counter Multiplier-3 changed the value and now is 800
Final value and is 800
```
### 5. SemaphoreDemo.java
Description:
```
/**
 * Simple example of using Semaphore[1] to control access to shared resources
 * [1] Can be used by multiple threads at the same time and includes a counter to track availability
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/SemaphoreDemo.java)

Sample output:
```console
EV0 is charging..
EV1 is charging..
EV2 is charging..
EV3 is charging..
EV4 is charging..
EV2 finished charging.
EV5 is charging..
EV4 finished charging.
EV6 is charging..
EV1 finished charging.
EV7 is charging..
EV0 finished charging.
EV8 is charging..
EV3 finished charging.
EV9 is charging..
EV7 finished charging.
EV8 finished charging.
EV5 finished charging.
EV6 finished charging.
EV9 finished charging.
```
### 6. BlockingQueueDemo
Description:
```
/**
 * Simple sample of Producer/Consumer using ArrayBlockingQueue (ThreadSafe)
 * - One Producer adds items in a pipeline in a frequency of 200ms
 * - The pipeline has a limit of 5 items
 * - Two consumers take items from the pipeline and consumes it in 500ms
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/BlockingQueueDemo.java)

Sample output:
```console
Producer is adding item1 [1/5]
Consumer1 took item1
Consumer0 took item2
Producer is adding item2 [1/5]
Producer is adding item3 [1/5]
Consumer1 took item3
Producer is adding item4 [1/5]
Consumer0 took item4
Producer is adding item5 [1/5]
Producer is adding item6 [2/5]
Consumer1 took item5
Consumer0 took item6
Producer is adding item7 [1/5]
Producer is adding item8 [2/5]
Consumer1 took item7
Producer is adding item9 [2/5]
Consumer0 took item8
Producer is adding item10 [2/5]
Producer is adding item11 [3/5]
Consumer1 took item9
Consumer0 took item10
Producer is adding item12 [2/5]
Producer is adding item13 [3/5]
Consumer1 took item11
Producer is adding item14 [3/5]
Consumer0 took item12
Producer is adding item15 [3/5]
Producer is adding item16 [4/5]
Consumer1 took item13
Consumer0 took item14
Producer is adding item17 [3/5]
Producer is adding item18 [4/5]
Consumer1 took item15
Producer is adding item19 [4/5]
Consumer0 took item16
Producer is adding item20 [4/5]
Consumer1 took item17
Consumer0 took item18
Consumer1 took item19
Consumer0 took item20
```
### 7. ForkJoinRecursiveTaskDemo
Description:
```
/**
 * Fork/Join Framework
 * Framework for executing recursive, divide-and-conquer work with multiple processors
 * Sample of recursive sum of a range of numbers
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/ForkJoinRecursiveTaskDemo.java)

Sample output:
```console
500000000500000000
```
### 8. ForkJoinRecursiveActionDemo
Description:
```
/**
 * Fork/Join Framework
 * Framework for executing recursive, divide-and-conquer work with multiple processors
 * Replace the values of a ArrayList from 0 to 1, or from 1 to 0
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/ForkJoinRecursiveActionDemo.java)

Sample output:
```console
[0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, <truncated result>
[1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, <truncated result>
```
### 9. ReentrantReadWriteLockDemo
Description:
```
/**
 * Sample of using ReentrantReadWriteLock
 * A box with some items and 5 worker threads that can randomly add, remove, peek or print the items
 * Each worker has 5 of those above operations that is chosen also randomly
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/ReentrantReadWriteLockDemo.java)

Sample output:
```console
Worker3 [1/5] removed item 1
Worker4 [1/5] peeked item 2
Worker5 [1/5] peeked item 2
Worker1 [1/5] printed list |2|3|4|5|
Worker2 [1/5] printed list |2|3|4|5|
Worker2 [2/5] peeked item 2
Worker4 [2/5] peeked item 2
Worker3 [2/5] peeked item 2
Worker1 [2/5] removed item 2
Worker5 [2/5] peeked item 3
Worker5 [3/5] added item 8
Worker2 [3/5] removed item 3
Worker4 [3/5] peeked item 4
Worker3 [3/5] peeked item 4
Worker1 [3/5] added item 8
Worker2 [4/5] printed list |4|5|8|8|
Worker5 [4/5] printed list |4|5|8|8|
Worker4 [4/5] removed item 4
Worker3 [4/5] removed item 5
Worker5 [5/5] printed list |8|8|
Worker2 [5/5] printed list |8|8|
Worker3 [5/5] peeked item 8
Worker1 [4/5] printed list |8|8|
Worker4 [5/5] removed item 8
Worker1 [5/5] printed list |8|
```
### 10. CountDownLatchDemo2
Description:
```
/**
 * Starts 10 Callable tasks in parallel, which one will wait from 1s to 5s and increment an AtomicInteger.
 * Wait for all 10 threads using CountDownLatch[1] and print the result at the same time in the end.
 * [1] Releases when a count value reaches zero
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/CountDownLatchDemo2.java)

Sample output:
```console
2023-07-23T16:29:25.091Z | counter 1
2023-07-23T16:29:26.428Z | counter 6
2023-07-23T16:29:25.192Z | counter 2
2023-07-23T16:29:28.022Z | counter 10
2023-07-23T16:29:27.134Z | counter 8
2023-07-23T16:29:25.205Z | counter 3
2023-07-23T16:29:26.668Z | counter 7
2023-07-23T16:29:25.511Z | counter 5
2023-07-23T16:29:25.390Z | counter 4
2023-07-23T16:29:27.178Z | counter 9
```
### 11. ThreadLocalDemo
Description:
```
/**
 * In this example, 3 threads will print the 3 consecutive exponential values of 1, 2 and 3.
 * All of them will be using the SharedUtil class for it.
 * P.S. SharedUtil uses Java ThreadLocal class which enables us to create variables that can only be read and written by the same thread.
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/ThreadLocalDemo.java)

Sample output:
```console
Thread exp2: 2
Thread exp3: 3
Thread exp1: 1
Thread exp1: 1
Thread exp1: 1
Thread exp2: 4
Thread exp3: 9
Thread exp2: 8
Thread exp3: 27
```
### 12. SynchronizedDemo
Description:
```
/**
 * In this example, we have two threads, threadA and threadB, that share a common object lock. threadA enters a synchronized
 * block, does some work, then calls lock.wait() to wait for threadB to notify it. threadB, in its synchronized block, does some work,
 * and then calls lock.notify() to notify threadA to resume.
 * When you run this code, you'll observe that threadA will wait until threadB calls lock.notify(), demonstrating how synchronization
 * using synchronized, wait, and notify ensures proper coordination between the two threads.
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/SynchronizedDemo.java)

Sample output:
```console
Thread A is doing some work.
Thread A is waiting for Thread B to notify.
Thread B is doing some work.
Thread B is notifying Thread A to resume.
Thread A is resuming its work.
Both threads have completed.
```
