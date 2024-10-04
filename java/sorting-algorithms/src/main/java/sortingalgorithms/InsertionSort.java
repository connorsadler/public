package sortingalgorithms;

import java.util.Arrays;

/**
 * Insertion Sort
 *
 * This comparison-based algorithm builds the sorted output one item at a time.
 * It’s efficient for small data sets or data that is already partially sorted.
 */
public class InsertionSort {
    
    public void insertionSort(int[] arr) {
        int n = arr.length;
        if (n == 1) {
            return;
        }
        int temp;
        for (int i=1; i < n; i++) {

            String prefix = "[pass: " + i + "] start of pass";
            System.out.println(prefix + " arr is now: " + Arrays.toString(arr));

            int j = i;
            while (j > 0 && arr[j] < arr[j-1]) {
                prefix = "  [pass: " + i + " / " + j + "] ";
                System.out.println(prefix + " swapping");
                temp = arr[j-1];
                arr[j-1] = arr[j];
                arr[j] = temp;
                System.out.println(prefix + " arr is now: " + Arrays.toString(arr));
                j--;
            }

            prefix = "[pass: " + i + "] end of pass";
            System.out.println(prefix + " arr is now: " + Arrays.toString(arr));
        }
    }
}
