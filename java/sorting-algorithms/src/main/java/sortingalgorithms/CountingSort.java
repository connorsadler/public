package sortingalgorithms;

/**
 * An algorithm that does not use comparisons.
 * Instead a frequency count array is built up first, then used to place items in order
 * 
 * e.g. for this list [1,2,3]
 *      we can see we have 0 no times, 1 one time, 2 one time, and 3 one time
 *      so the frequency array would look like this:
 *      [0, 1, 1, 1]
 * 
 */
public class CountingSort {
    
    private static int MIN_VALUE = 0;
    private static int MAX_VALUE = 499;

    public void countingSort(int[] arr) {
        int n = arr.length;
        if (n == 1) {
            return;
        }

        // Make a frequency array containing the number of times we saw each number
        // TODO: This only works for a certain range of allowed input items - we can fix that, and at least provide a reasonable error message 
        int[] freqCount = new int[MAX_VALUE-MIN_VALUE+1];
        for (Integer item : arr) {
            if (item < MIN_VALUE || item > MAX_VALUE) {
                throw new IllegalArgumentException("value out of allowed item range: " + item + ", but range is: " + MIN_VALUE + " to " + MAX_VALUE + " inclusive");
            }
            freqCount[item]++;
        }
        
        // Repopulate the original array from the frequency counts
        int i=0;
        int freqPtr = 0;
        while (i<n) {
            if (freqCount[freqPtr] == 0) {
                // Find next item in freqCount with a positive number
                while (freqCount[freqPtr] == 0) {
                    freqPtr++;
                }
            }
            arr[i] = freqPtr;
            freqCount[freqPtr]--;
            i++;
        }
    }
    
}
