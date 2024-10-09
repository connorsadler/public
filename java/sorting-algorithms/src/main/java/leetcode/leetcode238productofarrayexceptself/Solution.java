package leetcodeplayground.productofarrayexceptself;

class Solution {
    public int[] productExceptSelf(int[] nums) {

        // naive answer... times out
        //int[] result = new int[nums.length];
        // for (int i=0; i < nums.length; i++) {
        //     int resultFori = 1;
        //     for (int j=0; j < nums.length; j++) {
        //         if (j != i) {
        //             resultFori *= nums[j];
        //         }
        //     }
        //     result[i] = resultFori;
        // }

        // better answer
        // - example:
        //    0  1  2  3  4  5      indexes in array
        //    v0 v1 v2 v3 v4 v5     values in each array element
        //          ^^
        //    lets say we want the result for index 2
        //    we need: 
        //      (A) the product of all elements before index 2
        //      multiplied by 
        //      (B) the product of all elements after index 2
        //    
        //    To get (A) we can keep track of this figure as we work forward through the array
        //    To get (B) we first precompute this in a separate array, by working backwards through the array and keep track of it going backwards

        // Step 1 - precompute and store (B) figures
        int[] productsFromIdxToEnd = new int[nums.length];
        int runningProductFromIdxToEnd = 1;
        for (int i=nums.length-1; i >= 0 ; i--) {
            runningProductFromIdxToEnd *= nums[i];
            productsFromIdxToEnd[i] = runningProductFromIdxToEnd;
        }

        // Step 2 - computer running (A) figure as we go along
        //          and use this with the (B) figure to calculate all the answers
        int[] result = new int[nums.length];
        int runningProductFromStartToIdx = 1;
        for (int i=0; i < nums.length-1; i++) {
            //System.out.println("i: " + i);
            //System.out.println("  runningProductFromStartToIdx: " + runningProductFromStartToIdx);
            //System.out.println("  productsFromIdxToEnd[i+1]: " + productsFromIdxToEnd[i+1]);
            result[i] = runningProductFromStartToIdx * productsFromIdxToEnd[i+1];
            runningProductFromStartToIdx *= nums[i];
        }
        result[nums.length-1] = runningProductFromStartToIdx;

        return result;
    }
}
