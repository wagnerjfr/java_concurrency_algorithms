# java_concurrency_algorithms
Java algorithms examples using concurrency

### 1. AtomicIntegerDemo
Description:
```
/**
 * Simple example of using AtomicInteger without any locks or synchronized
   method/block to fix data race[1]
 * [1] Two or more concurrent thread access the same memory location and at
   least one thread is modifying it
 */
```
[Code link]()

Sample output:
```console
Total sum int (not correct): 1738590
Total sum AtomicInteger (correct): 2000000
```
