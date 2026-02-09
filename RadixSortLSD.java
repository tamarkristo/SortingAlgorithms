public class RadixSortLSD {

    public static void sort(int[] arr) {
        if (arr == null || arr.length == 0)
            return;

        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[256];

        // 4 passes: bits [0-7], [8-15], [16-23], [24-31]
        for (int shift = 0; shift < 32; shift += 8) {

            // 1. Reset count
            for (int i = 0; i < 256; i++)
                count[i] = 0;

            boolean isSignPass = (shift == 24);
            int mask = 0xFF;

            // 2. Count frequencies
            for (int val : arr) {
                int byteVal = (val >> shift) & mask;
                // Flip sign bit on last pass to correctly order negatives before positives
                if (isSignPass)
                    byteVal ^= 0x80;
                count[byteVal]++;
            }

            // 3. Accumulate
            for (int i = 1; i < 256; i++) {
                count[i] += count[i - 1];
            }

            // 4. Build output (stable, reverse)
            for (int i = n - 1; i >= 0; i--) {
                int val = arr[i];
                int byteVal = (val >> shift) & mask;
                if (isSignPass)
                    byteVal ^= 0x80;

                output[count[byteVal] - 1] = val;
                count[byteVal]--;
            }

            // 5. Copy back
            System.arraycopy(output, 0, arr, 0, n);
        }
    }
}
