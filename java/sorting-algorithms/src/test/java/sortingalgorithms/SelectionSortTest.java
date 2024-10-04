package sortingalgorithms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sortingalgorithms.SelectionSort;

import java.util.Arrays;

public class SelectionSortTest {

    @Test
    public void test1() {
        int[] arr = { 5,4,3,2,1 };
        System.out.println("test array:   " + Arrays.toString(arr));
        new SelectionSort().selectionSort(arr);
        System.out.println("result array: " + Arrays.toString(arr));
        Assertions.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, arr);
    }

    @Test
    public void test2() {
        int[] arr = { -5,4,-3,2,1 };
        System.out.println("test array:   " + Arrays.toString(arr));
        new SelectionSort().selectionSort(arr);
        System.out.println("result array: " + Arrays.toString(arr));
        Assertions.assertArrayEquals(new int[]{ -5, -3, 1, 2, 4 }, arr);
    }

    @Test
    public void test3() {
        int[] arr = { 1,2,300,4,5 };
        System.out.println("test array:   " + Arrays.toString(arr));
        new SelectionSort().selectionSort(arr);
        System.out.println("result array: " + Arrays.toString(arr));
        Assertions.assertArrayEquals(new int[]{ 1, 2, 4, 5, 300 }, arr);
    }
}
