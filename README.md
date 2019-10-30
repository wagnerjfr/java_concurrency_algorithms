# java_concurrency_algorithms
Java algorithms examples using concurrency

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
 * - One Producer adds items in a pipeline in a frequency of 100ms
 * - The pipeline has a limit of 5 items
 * - When the pipeline is full, the Producer will wait for 500ms
 * - Two consumers take items from the pipeline and consumes it in 500ms
 */
```
[Code link](https://github.com/wagnerjfr/java_concurrency_algorithms/blob/master/BlockingQueueDemo.java)

Sample output:
```console
Producer is adding item1 [1/5]
Consumer0 took item1
Consumer1 took item2
Producer is adding item2 [1/5]
Producer is adding item3 [1/5]
Producer is adding item4 [2/5]
Producer is adding item5 [3/5]
Producer is adding item6 [4/5]
Consumer0 took item3
Consumer1 took item4
Producer is adding item7 [3/5]
Producer is adding item8 [4/5]
Producer is adding item9 [5/5]
Producer queue is full
Consumer0 took item5
Consumer1 took item6
Producer is adding item10 [4/5]
Producer is adding item11 [5/5]
Consumer0 took item7
Consumer1 took item8
Producer is adding item12 [4/5]
Producer is adding item13 [5/5]
Producer queue is full
Consumer0 took item9
Consumer1 took item10
Producer is adding item14 [4/5]
Producer is adding item15 [5/5]
Producer queue is full
Consumer0 took item11
Consumer1 took item12
Producer is adding item16 [4/5]
Consumer0 took item13
Consumer1 took item14
Producer is adding item17 [3/5]
Producer is adding item18 [4/5]
Producer is adding item19 [5/5]
Producer queue is full
Consumer0 took item15
Consumer1 took item16
Producer is adding item20 [4/5]
Consumer0 took item17
Consumer1 took item18
Consumer0 took item19
Consumer1 took item20
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
