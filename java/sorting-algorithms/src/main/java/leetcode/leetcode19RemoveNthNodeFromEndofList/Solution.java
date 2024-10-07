package leetcode.leetcode19RemoveNthNodeFromEndofList;

import java.util.Deque;
import java.util.LinkedList;

/**
 * https://leetcode.com/problems/remove-nth-node-from-end-of-list/description/?envType=study-plan-v2&envId=top-interview-150
 */

class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        Deque<ListNode> storage = new LinkedList<ListNode>();
        ListNode node = head;
        while (node != null) {
            // Keep the most recent 'n+1' nodes in 'storage'
            // This is because we'll need to manipulate the 'next' pointer of the node BEFORE the one we remove, hence needing 'n+1' nodes in storage not just 'n'
            if (storage.size() == n+1) {
                storage.removeLast();
            }
            storage.addFirst(node);
            node = node.next;
        }
        // Now we have storage as:
        //    n+1 nodes
        // or n nodes
        if (storage.size() == n+1) {
            // e.g. we have 3 nodes and n==2
            // We need to tweak the pointer of the node before the one to be removed
            ListNode nodeBeforeRemovalNode = storage.getLast();
            ListNode nodeToRemove = nodeBeforeRemovalNode.next;
            nodeBeforeRemovalNode.next = nodeToRemove.next;
            nodeToRemove.next = null; // Remember to clear pointer of node being removed, just in case
            return head;
        } else {
            // e.g. we only have 2 nodes and n==2
            // The last node in the list becomes the head of the new list
            ListNode nodeToRemove = storage.getLast();
            ListNode result = nodeToRemove.next;
            nodeToRemove.next = null; // Remember to clear pointer of node being removed, just in case
            return result;
        }


    }
}
