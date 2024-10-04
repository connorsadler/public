package sortingalgorithms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sortingalgorithms.InsertionSort;

import java.util.Arrays;

public class InsertionSortTest {

    @Test
    public void test1() {
        int[] arr = { 5,4,3,2,1 };
        System.out.println("test array:   " + Arrays.toString(arr));
        new InsertionSort().insertionSort(arr);
        System.out.println("result array: " + Arrays.toString(arr));
        Assertions.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, arr);
    }

    @Test
    public void test2() {
        int[] arr = { -5,4,-3,2,1 };
        System.out.println("test array:   " + Arrays.toString(arr));
        new InsertionSort().insertionSort(arr);
        System.out.println("result array: " + Arrays.toString(arr));
        Assertions.assertArrayEquals(new int[]{ -5, -3, 1, 2, 4 }, arr);
    }

    @Test
    public void test3() {
        int[] arr = { 1,2,300,4,5 };
        System.out.println("test array:   " + Arrays.toString(arr));
        new InsertionSort().insertionSort(arr);
        System.out.println("result array: " + Arrays.toString(arr));
        Assertions.assertArrayEquals(new int[]{ 1, 2, 4, 5, 300 }, arr);
    }

    // See file: InsertionSort-example.png
    @Test
    public void test4_InsertionSort_example_png() {
        int[] arr = { 85,12,59,45,72,51 };
        System.out.println("test array:   " + Arrays.toString(arr));
        new InsertionSort().insertionSort(arr);
        System.out.println("result array: " + Arrays.toString(arr));
        Assertions.assertArrayEquals(new int[]{ 12,45,51,59,72,85 }, arr);
    }

}
