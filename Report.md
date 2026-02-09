# Comparative Analysis of Sorting Algorithms on Large Integer Datasets

**Date:** February 9, 2026

## 1. Executive Summary
This project evaluates the performance of four sorting algorithms—Bubble Sort, Quick Sort (Custom), LSD Radix Sort, and Java's standard `Arrays.sort`—on datasets of 1,000,000 integers. The benchmark covers four distinct data distributions: Random, Sorted, Reverse Sorted, and Nearly Sorted.
Our results demonstrate that while `Arrays.sort` (Dual-Pivot Quicksort) offers the best general-purpose performance and adaptability to pre-sorted data, **LSD Radix Sort** provides superior throughput for large random integer arrays, achieving speeds approximately **8x faster** than comparison-based approaches due to its O(N) linear time complexity and cache-friendly memory access patterns.

---

## 2. Theoretical Background & Complexity Analysis

### 2.1 Bubble Sort
Bubble Sort is a simple comparison-based algorithm that repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order.
- **Time Complexity**:
  - Worst Case: O(N^2) (Reverse Sorted)
  - Average Case: O(N^2)
  - Best Case: O(N) (Already Sorted, with early exit optimization)
- **Space Complexity**: O(1) (In-place)
- **Stability**: Stable (does not change relative order of equal elements).

### 2.2 Quick Sort
Quick Sort is a divide-and-conquer algorithm that selects a 'pivot' element and partitions the array into two sub-arrays: elements less than the pivot and elements greater than the pivot.
- **Time Complexity**:
  - Worst Case: O(N^2) (typically when pivot is min/max)
  - Average Case: O(N log N)
- **Space Complexity**: O(log N) stack space for recursion.
- **Stability**: Unstable (due to long-range swaps during partitioning).

### 2.3 LSD Radix Sort (Base 256)
Radix Sort is a non-comparative sorting algorithm. It sorts integers by processing individual digits. Least Significant Digit (LSD) Radix Sort processes from the least significant byte to the most significant.
- **Time Complexity**: O(W * (N + K)), where W is the number of passes (4 for 32-bit integers) and K is the range of the digit (256). Since W and K are constants, this is effectively **O(N)**.
- **Space Complexity**: O(N + K) for the auxiliary output array and count array.
- **Stability**: Stable (crucial for correct multi-pass sorting).

### 2.4 Java Arrays.sort
The standard library implementation for primitive `int[]` arrays uses a Dual-Pivot Quicksort algorithm by Vladimir Yaroslavskiy, Jon Bentley, and Joshua Bloch.
- **Time Complexity**: O(N log N).
- **Features**: Highly optimized handling of pivots, instruction pipelining, and detection of "runs" (already sorted sequences).

---

## 3. Implementation Details

### 3.1 Custom Quick Sort Strategy
A naive Quick Sort implementation often fails on sorted or reverse-sorted data because picking the first or last element as a pivot creates unbalanced partitions (size 0 and N-1), degrading performance to O(N^2).
**Mitigation Strategy**: This implementation uses a **Median-of-Three** pivot selection. By calculating the median of `arr[low]`, `arr[rec_mid]`, and `arr[high]`, the algorithm ensures a balanced partition even for sorted inputs.

### 3.2 Radix Sort & Negative Numbers
Standard Radix Sort logic relies on bitwise operations. In Java, integers are signed (2's complement). This poses a problem because negative numbers have their Most Significant Bit (MSB) set to 1, making them appear "larger" than positive numbers (MSB 0) when treated as unsigned bytes.
**Solution**: During the final pass (processing the sign byte), we **flip the sign bit** (XOR `0x80`). This maps:
- Negative numbers (`1xxxxxxx`) -> `0xxxxxxx` (Sorted first)
- Positive numbers (`0xxxxxxx`) -> `1xxxxxxx` (Sorted last)
This correctly orders the entire range of 32-bit signed integers.

### 3.3 Benchmarking Harness
To ensure fair and reproducible results:
- **Warm-up Phase**: 3 full sorting runs are executed before measurement. This triggers the JVM's C2 (HotSpot) compiler to optimize the code paths (JIT compilation).
- **Measurement**: Methods are timed using `System.nanoTime()`. The final time is the average of 5 executed runs.
- **Data Isolation**: input arrays are cloned (`Arrays.copyOf()`) before each run to prevent any algorithm from receiving essentially free sorted data from a previous run.
- **GC Invocation**: `System.gc()` is suggested between runs to minimize Garbage Collection pauses during measurement.

---

## 4. Experimental Results

**Machine Specifications**: Windows 11 (ARM64), 12 Cores, Java 21.

### 4.1 Execution Time (Milliseconds)

| Dataset Distribution | Bubble Sort (N=50k)* | Quick Sort (N=1M) | Radix Sort (N=1M) | Arrays.sort (N=1M) |
| :--- | :--- | :--- | :--- | :--- |
| **Uniform Random** | ~3,956 ms | 85 ms | **9 ms** | 73 ms |
| **Sorted** | ~0.05 ms | 21 ms | 27 ms | **~0.89 ms** |
| **Reverse Sorted** | ~3,971 ms | 82 ms | 89 ms | **~2.08 ms** |
| **Nearly Sorted** (1% Swaps) | ~1,087 ms | 107 ms | 46 ms | **24 ms** |

*\*Note: Bubble Sort times are for N=50,000. Extrapolating to N=1,000,000 (a factor of 20x input size means 400x time) would yield ~1,600 seconds, or ~26 minutes.*

---

## 5. Discussion & Analysis

### 5.1 The Dominance of Radix Sort (Random Data)
On random data, **LSD Radix Sort** is the clear winner, outperforming `Arrays.sort` by a factor of 8.
**Reasoning**:
- **Branch Prediction**: QuickSort involves `if (arr[i] < pivot)` comparisons. On random data, this branch is unpredictable (50/50 chance), causing frequent CPU pipeline flushes. Radix Sort is "branch-free" in its core loop; it simply computes an index and writes to memory.
- **Memory Access**: Radix Sort performs strictly sequential reads and predictable writes, maximizing cache locality.

### 5.2 The Robustness of Quick Sort
The custom Quick Sort implementation showed consistent performance (~80ms) on Random and Reverse data. It did **not** degrade to O(N^2) on Sorted/Reverse inputs (~21ms/82ms). This confirms the effectiveness of the **Median-of-Three** pivot strategy. Without this, the recursion depth would have exceeded stack limits or timed out.

### 5.3 The Optimizations of Arrays.sort
Java's standard library is incredibly sophisticated.
- On **Sorted/Reverse** data, it clocks ~1-2ms. This implies it checks for ascending/descending runs during partitioning or initialization. It effectively recognizes the array is already sorted and terminates immediately.
- On **Nearly Sorted** data, it beat custom Quick Sort (24ms vs 107ms). Dual-Pivot QuickSort handles "runs" of sorted data much better than Single-Pivot implementations.

### 5.4 The Cost of Comparisions vs Counting
Radix Sort proves that for primitive types (int, long), avoiding the comparison overhead is a massive win. However, Radix Sort requires O(N) auxiliary memory, whereas Quick Sort partitions in-place. This is a classic Time-Space trade-off.

---

## 6. Conclusion
1.  **Production Readiness**: `Arrays.sort()` is the optimal choice for general-purpose applications. It is robust, handles structured data (sorted/reverse) almost instantly, and requires zero maintenance.
2.  **Specialized Performance**: For applications requiring the sorting of massive arrays of random integers (e.g., cryptographic nonce generation, scientific simulations, or high-frequency trading logs), **LSD Radix Sort** offers a significant performance advantage (8x speedup).
3.  **Algorithmic Safety**: Implementing Quick Sort manually in production is risky without careful pivot selection (e.g., Median-of-Three or localized shuffling), as worst-case data patterns are common in real-world scenarios.
