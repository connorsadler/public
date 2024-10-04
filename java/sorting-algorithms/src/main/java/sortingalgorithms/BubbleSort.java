package sortingalgorithms;

/**
 * A simple comparison-based algorithm that repeatedly steps through the list, compares adjacent elements,
 * and swaps them if they are in the wrong order.
 * This process is repeated until the list is sorted.
 */
public class BubbleSort {

    public void bubbleSort(int[] arr) {
        int n = arr.length;
        if (n == 1) {
            return;
        }
        int temp;
        for (int i=n-1; i >= 0; i--) {
            for (int j=0; j < i; j++) {
                if (arr[j] > arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
    
}
