package sortingalgorithms;

import java.util.Arrays;

/**
 * Another comparison-based algorithm that works by dividing the input into a sorted and an unsorted region.
 * It repeatedly selects the smallest (or largest) element from the unsorted region and moves it to the end of the sorted region.
 */
public class SelectionSort {

    public void selectionSort(int[] arr) {
        int n = arr.length;
        if (n == 1) {
            return;
        }
        int temp;
        
        for (int i=0; i < n-1; i++) {
            int minIdx = i;
            for (int j=i+1; j < n; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            String prefix = "[pass: " + i + "] ";
            System.out.println(prefix + " found min: " + arr[minIdx] + " at minIdx: " + minIdx);
            if (minIdx != i) {
                // Swap
                temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
            }
            
            System.out.println(prefix + " arr is now: " + Arrays.toString(arr));
        }
        
    }
    
}
