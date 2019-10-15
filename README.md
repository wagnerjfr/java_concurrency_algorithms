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
