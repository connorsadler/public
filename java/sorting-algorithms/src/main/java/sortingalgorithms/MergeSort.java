package sortingalgorithms;

import java.util.Arrays;

/**
 * A divide-and-conquer algorithm that recursively divides the input into two halves, sorts each half, and then merges the 
 * sorted halves to produce the final sorted output.
 */
public class MergeSort {

    public void mergeSort(int[] arr) {
        int n = arr.length;
        if (n == 1) {
            return;
        }
        
        mergeSort(arr, 0, arr.length-1, 0);
        
//        int temp;
//        for (int i=n-1; i >= 0; i--) {
//            for (int j=0; j < i; j++) {
//                if (arr[j] > arr[j + 1]) {
//                    temp = arr[j];
//                    arr[j] = arr[j + 1];
//                    arr[j + 1] = temp;
//                }
//            }
//        }
    }

    // Note: The indexes are INCLUSIVE, so leftIdx->rightIdx as 0->2 means 3 elements, at indices 0,1,2
    private void mergeSort(int[] arr, int leftIdx, int rightIdx, int depth) {

        String indent = "  ".repeat(depth * 2);
        String prefix = indent + "[depth: " + depth + "]";
        
        System.out.println(prefix + " >>> mergeSort, l->r: " + leftIdx + "->" + rightIdx);
        
        if (leftIdx == rightIdx) {
            // Single element, already sorted, nothing to do
            System.out.println(prefix + " <<< mergeSort, single element (" + arr[leftIdx] + "), nothing to do");
            return;
        }

        int len = rightIdx-leftIdx+1;
        int mid = (leftIdx+rightIdx) / 2 + 1;
        System.out.println(prefix + " mid: " + mid);
        mergeSort(arr, leftIdx, mid-1, depth+1);
        mergeSort(arr, mid, rightIdx, depth+1);

        // merge results
        System.out.println(prefix + " merge step, for sublists " + leftIdx + "->" + (mid-1) + " and " + mid + "->" + rightIdx);
        int[] result = new int[len];
        int p1 = leftIdx;
        int p2 = mid;
        int resultIdx = 0;
        while (resultIdx < len) {
            boolean p1_hasItemsLeft = p1 < mid;
            boolean p2_hasItemsLeft = p2 <= rightIdx;
            if (!p1_hasItemsLeft && !p2_hasItemsLeft) {
                throw new RuntimeException("Something went wrong - neither p1 or p2 has items left");
            }
            boolean chooseFromp1;
            if (!p1_hasItemsLeft) {
                chooseFromp1 = false; // p1 exhausted, choose from p2
            } else if (!p2_hasItemsLeft) {
                chooseFromp1 = true; // p2 exhausted, choose from p1
            } else {
                chooseFromp1 = arr[p1] <= arr[p2]; // both p1 and p2 have items, check next items from each list and choose lowest
            }
            if (chooseFromp1) {
                result[resultIdx] = arr[p1];
                resultIdx++;
                p1++;
            } else {
                result[resultIdx] = arr[p2];
                resultIdx++;
                p2++;
            }
        }
        System.out.println(prefix + " merge step done, result: " + Arrays.toString(result));
        // Write result array into original array
        System.arraycopy(result, 0, arr, leftIdx, len);


        System.out.println(prefix + " <<< mergeSort");
    }

}
