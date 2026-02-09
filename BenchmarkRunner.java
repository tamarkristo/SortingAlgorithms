import java.util.Arrays;
import java.util.function.Consumer;

public class BenchmarkRunner {

    private static final int WARMUP_ROUNDS = 3;
    private static final int MEASURE_ROUNDS = 5;
    private static final int N = 1_000_000;

    // Bubble sort is O(N^2), so 1M is too slow (~hours).
    // Assignment allows running on smaller N (e.g. 50k) if extrapolated.
    private static final int N_BUBBLE = 50_000;

    public static void main(String[] args) {
        printSystemInfo();

        // Datasets to test
        String[] types = { "Random", "Sorted", "Reverse", "Nearly Sorted" };

        System.out.printf("%-15s | %-12s | %-10s | %s\n", "Dataset", "Algorithm", "Time (ms)", "Result");
        System.out.println("---------------------------------------------------------------");

        for (String type : types) {
            runTestsForDataset(type);
            System.out.println("---------------------------------------------------------------");
        }
    }

    private static void runTestsForDataset(String type) {
        System.out.println("Running tests for: " + type);

        // 1. Bubble Sort (Small N due to O(N^2))
        int[] dataBubble = generateData(type, N_BUBBLE);
        runAlgorithm("BubbleSort*", dataBubble, BubbleSort::sort);

        // 2. Quick Sort
        int[] data = generateData(type, N);
        runAlgorithm("QuickSort", data, QuickSort::sort);

        // 3. Radix Sort
        runAlgorithm("RadixSortLSD", data, RadixSortLSD::sort);

        // 4. Arrays.sort
        runAlgorithm("Arrays.sort", data, Arrays::sort);
    }

    private static void runAlgorithm(String name, int[] original, Consumer<int[]> sorter) {
        // Warmup
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            int[] copy = Arrays.copyOf(original, original.length);
            sorter.accept(copy);
        }

        long totalTime = 0;
        for (int i = 0; i < MEASURE_ROUNDS; i++) {
            int[] copy = Arrays.copyOf(original, original.length);
            System.gc(); // Suggest GC to clean up before run

            long start = System.nanoTime();
            sorter.accept(copy);
            long end = System.nanoTime();

            totalTime += (end - start);

            if (i == 0 && !isSorted(copy)) {
                System.err.println("ERROR: " + name + " failed correctness check!");
            }
        }

        double avg = (totalTime / MEASURE_ROUNDS) / 1_000_000.0;
        System.out.printf("%-15s | %10.2f ms\n", name, avg);
    }

    private static int[] generateData(String type, int n) {
        if ("Random".equals(type))
            return DataGenerator.generateRandom(n);
        if ("Sorted".equals(type))
            return DataGenerator.generateSorted(n);
        if ("Reverse".equals(type))
            return DataGenerator.generateReverseSorted(n);
        if ("Nearly Sorted".equals(type))
            return DataGenerator.generateNearlySorted(n);
        return new int[0];
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1])
                return false;
        }
        return true;
    }

    private static void printSystemInfo() {
        System.out.println("Benchmark System Info:");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        System.out.println("Cores: " + Runtime.getRuntime().availableProcessors());
        System.out.println("BubbleSort* runs on N=" + N_BUBBLE + ", others on N=" + N);
        System.out.println("---------------------------------------------------------------");
    }
}
