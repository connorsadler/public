package leetcode.leetcode209minimumsizesubarraysum;

class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        
        //
        // Create a sliding window into the array
        // e.g.
        //    idx:  0 1 2 3 4 5
        //    arr: [2,3,1,2,4,3]
        //            ^   ^         left = 1, right = 3
        //    currentSum will be current sum between left->right inclusive - in this case, currentSum = 6
        //    
        // We move the window across, depending on whether we need to grow it to include more items or shrink it to include less items
        //
        int n = nums.length;
        int lastIdx = nums.length-1;
        int left = 0;
        int right = 0;
        int currentSum = nums[0];
        int result = Integer.MAX_VALUE;
        while (left <= lastIdx || right <= lastIdx) {
            if (currentSum < target) {
                // expand rightwards - if possible
                if (right < lastIdx) {
                    right++;
                    if (right <= lastIdx) {
                        currentSum += nums[right];
                    }
                } else {
                    // no room to grow rightwards, so current answer must be our answer
                    break;
                }
            } else {
                // our current sum has reached target - maybe current array size is our minimum size?
                int currentSize = right-left+1;
                if (currentSize < result) {
                    result = currentSize;
                }
                // shrink from left?
                if (left < right) {
                    currentSum -= nums[left];
                    left++;
                } else {
                    // shift whole window across
                    currentSum -= nums[left];
                    left++;
                    right++;
                    if (left <= lastIdx) {
                        currentSum += nums[left];
                    }
                }

            }
        }
        if (result == Integer.MAX_VALUE) {
            result = 0;
        }
        return result;
    }
}
