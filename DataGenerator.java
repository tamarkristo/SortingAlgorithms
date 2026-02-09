import java.util.Random;

public class DataGenerator {

    private static final Random rand = new Random();

    // Generate N random integers (uniform distribution)
    public static int[] generateRandom(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt();
        }
        return arr;
    }

    // Generate N integers sorted in ascending order
    public static int[] generateSorted(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        return arr;
    }

    // Generate N integers sorted in descending order
    public static int[] generateReverseSorted(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - i;
        }
        return arr;
    }

    // Generate mostly sorted array, with 1% random swaps
    public static int[] generateNearlySorted(int n) {
        int[] arr = generateSorted(n);
        int swaps = (int) (n * 0.01); // 1%
        if (swaps < 1)
            swaps = 1;

        for (int i = 0; i < swaps; i++) {
            int index1 = rand.nextInt(n);
            int index2 = rand.nextInt(n);

            // Swap
            int temp = arr[index1];
            arr[index1] = arr[index2];
            arr[index2] = temp;
        }
        return arr;
    }
}
