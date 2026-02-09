# Sorting Algorithms Performance Analysis

**Date:** February 9, 2026

## 1. Project Overview
This report analyzes the performance of four different sorting strategies applied to datasets of 1,000,000 integers. The objective is to evaluate how algorithmic complexity theories translate to real-world execution time on modern hardware, specifically comparing manual implementations against the highly optimized Java standard library.

## 2. Algorithm Implementations

### Bubble Sort (Baseline)
Bubble Sort is an elementary sorting algorithm with O(N^2) complexity.
- **Optimization**: An "early exit" flag was implemented. If a pass completes without swapping any elements, the array is considered sorted, allowing O(N) best-case performance.
- **Constraint**: Due to the quadratic time complexity, sorting 1,000,000 elements would take hours. For benchmarking purposes, this algorithm was tested on a reduced dataset of N=50,000, and results are extrapolated where necessary for comparison.

### Quick Sort (Custom)
A standard in-place divide-and-conquer algorithm with O(N log N) average complexity.
- **Pivot Strategy**: To ensure robustness, a "Median-of-Three" pivot selection strategy was used (median of the first, middle, and last elements). This prevents the catastrophic O(N^2) worst-case scenario that occurs when sorting already-sorted or reverse-sorted data with a naive pivot.
- **Recursion**: The implementation partitions the array and recursively sorts the sub-arrays.

### LSD Radix Sort
A non-comparative integer sorting algorithm with O(N) complexity.
- **Mechanism**: The algorithm processes the integers byte-by-byte (Base 256), performing four passes of Counting Sort.
- **Negative Number Handling**: Standard Radix Sort logic fails with negative integers because the sign bit in 2's complement representation makes negative numbers appear "larger" than positives. This implementation solves the issue by flipping the sign bit (XOR 0x80) during the final pass (the most significant byte), ensuring correct ordering.

### Arrays.sort (Java Standard Library)
The reference implementation provided by the JDK. For primitive integer arrays, this uses a Dual-Pivot Quicksort (by Vladimir Yaroslavskiy). It is heavily optimized for instruction pipelining and CPU cache locality, and it includes heuristics to detect runs of sorted data.

## 3. Benchmarking Methodology
All tests were executed on a 12-core Windows machine running Java 21.

To ensure the validity of the results:
- **Warm-up**: The JVM was warmed up with 3 full runs before measurement to trigger JIT (Just-In-Time) compilation.
- **Isolation**: Each test run operated on a fresh copy of the data to prevent previous sorts from influencing the result.
- **Verification**: An `isSorted` check was performed after every run to guarantee correctness.
- **Measurement**: results represent the average of 5 timed executions.

## 4. Results Discussion

The following table summarizes the execution time for sorting.

| Dataset Type | Radix Sort | Arrays.sort | Quick Sort | Bubble Sort (50k) |
| :--- | :--- | :--- | :--- | :--- |
| **Random** | 9 ms | 73 ms | 85 ms | ~4,000 ms |
| **Sorted** | 27 ms | ~1 ms | 21 ms | ~0.05 ms |
| **Reverse** | 89 ms | ~2 ms | 82 ms | ~4,000 ms |
| **Nearly Sorted** | 46 ms | 24 ms | 107 ms | ~1,000 ms |

### Deep Dive Analysis

**1. Random Distributions: The Power of O(N)**
On random data, **LSD Radix Sort** outperformed all other algorithms by a significant margin (nearly 8x faster than `Arrays.sort`). This demonstrates the advantage of non-comparative sorting for primitive types. While Quick Sort requires O(N log N) comparisons—which involve unpredictable branches that can stall the CPU pipeline—Radix Sort performs a fixed number of linear passes (4 passes for 32-bit ints). This linear memory access pattern is extremely friendly to the CPU prefetcher.

**2. Ordered Distributions (Sorted / Reverse)**
Java's **Arrays.sort** exhibited near-instant performance on sorted (~1ms) and reverse (~2ms) data. This indicates the library explicitly checks for existing order or strictly ascending/descending runs.
The custom **Quick Sort** maintained stable performance (~20-80ms) on these inputs. This validates the effectiveness of the Median-of-Three pivot; without it, the partition sizes would have been unbalanced (1 vs N-1), leading to a stack overflow or timeout.

**3. Nearly Sorted Data**
**Arrays.sort** again showed superior adaptability here (24ms). The custom Quick Sort (107ms) slowed down slightly compared to the random case, likely because the partitioning logic does not inherently benefit from "almost sorted" regions as much as the sophisticated Dual-Pivot implementation in the standard library.

## 5. Conclusion
The choice of sorting algorithm depends heavily on the data properties:
- **General Purpose**: **Arrays.sort** is the optimal choice for most applications. It provides excellent performance on random data and exceptional (near-instant) performance on correctly or partially structured data.
- **High-Throughput Primitives**: For applications processing massive streams of random integers (e.g., rendering, scientific computing), **LSD Radix Sort** is superior. Its linear complexity allows it to scale better than comparison-based sorts, making it the most efficient option for raw number crunching.
- **Algorithm Design**: The experiment highlights that theoretical worst-cases (like Quick Sort's O(N^2)) must be actively mitigated in implementation (e.g., via pivot selection) to ensure reliability in production systems.
