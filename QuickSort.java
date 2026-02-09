public class QuickSort {

    public static void sort(int[] arr) {
        if (arr == null || arr.length < 2)
            return;
        quickSort(arr, 0, arr.length - 1);
    }

    private static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // Cutoff to Insertion Sort for small subarrays
            if (high - low < 32) {
                insertionSort(arr, low, high);
                return;
            }

            // Using median-of-three to pick pivot
            int pivotIndex = partition(arr, low, high);

            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private static void insertionSort(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= low && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    // Partition using median-of-three pivot strategy
    private static int partition(int[] arr, int low, int high) {
        // Find median of low, mid, high to avoid worst case on sorted arrays
        int mid = low + (high - low) / 2;
        int pivotIndex = getMedianIndex(arr, low, mid, high);

        // Swap pivot to end (high) for standard processing
        swap(arr, pivotIndex, high);
        int pivot = arr[high];

        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return i + 1;
    }

    private static int getMedianIndex(int[] arr, int a, int b, int c) {
        // Return the index of the median of the values at a, b, c
        if (arr[a] > arr[b]) {
            if (arr[b] > arr[c])
                return b;
            else if (arr[a] > arr[c])
                return c;
            else
                return a;
        } else {
            if (arr[a] > arr[c])
                return a;
            else if (arr[b] > arr[c])
                return c;
            else
                return b;
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
