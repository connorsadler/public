package cfs.adventofcode2025.day09;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void testParseLine() {
        Main.Helper h = new Main.Helper();
        Main.RedTile actualRedTile = h.parseLine("7,1");
        Main.RedTile expectedRedTile = new Main.RedTile(7,1);
        assertEquals(expectedRedTile, actualRedTile);
    }

    @Test
    void testCalcAreaTo() {
        assertEquals(24L, new Main.RedTile(2,5).calcAreaTo(new Main.RedTile(9,7)));
        assertEquals(35L, new Main.RedTile(7,1).calcAreaTo(new Main.RedTile(11,7)));
        assertEquals(6L, new Main.RedTile(7,3).calcAreaTo(new Main.RedTile(2,3)));
    }

    @Test
    void testExampleProblem() throws Exception {
        Main.Helper h = new Main.Helper();

        String exampleInput = """
               7,1
               11,1
               11,7
               9,7
               9,5
               2,5
               2,3
               7,3""";
        System.out.println("exampleInput = " + exampleInput);

        BufferedReader br = new BufferedReader(new StringReader(exampleInput));
        h.processLines(br);

        // Produce final answer
        h.produceFinalAnswer();
        
        // Check final answer
        assertEquals(50, h.getFinalAnswer());
        assertEquals(24, h.getFinalAnswerPartTwo());
    }

    @Test
    void testExampleProblemPartTwoAdvanced() throws Exception {
        Main.Helper h = new Main.Helper();

        String exampleInput = """
               7,1
               11,1
               11,7
               9,7
               9,5
               2,5
               2,3
               7,3""";
        System.out.println("exampleInput = " + exampleInput);

        BufferedReader br = new BufferedReader(new StringReader(exampleInput));
        h.processLines(br);

        // Produce final answer
        h.produceFinalAnswer();

        // Check some rectangle checks
        // A - this rect starts outside our shape
        assertEquals(false, h.okForPartTwoAdvanced(0,0,5,5));
        // B - example in text - 7,3 and 11,1
        assertEquals(true, h.okForPartTwoAdvanced(7,3,11,1));
        // B extend by 1 row down
        assertEquals(true, h.okForPartTwoAdvanced(7,4,11,1));
        // B extend by 1 row down
        assertEquals(true, h.okForPartTwoAdvanced(7,5,11,1));
        // B extend by 1 row down
        assertEquals(false, h.okForPartTwoAdvanced(7,6,11,1));

        // C
        // row 3 has a split range like this:
        //   row:3={2=Entry{ch=null, fromIdx=2, toIdx=7}, 7=Entry{ch=null, fromIdx=7, toIdx=11}}
        assertEquals(true, h.okForPartTwoAdvanced(3,3,4,4));
        assertEquals(true, h.okForPartTwoAdvanced(3,3,11,4));
    }

    @Test
    void testFullProblem() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Main.inputFile));
        Main.Helper h = new Main.Helper();

        h.processLines(br);
        h.produceFinalAnswer();

        // Check final answer
        assertEquals(4725826296L, h.getFinalAnswer());
        assertEquals(1637556834L, h.getFinalAnswerPartTwo());
    }

    // https://www.reddit.com/r/adventofcode/comments/1pi3hff/comment/ntbse5p/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
    @Test
    void testRedditDevious() throws Exception {
        Main.Helper h = new Main.Helper();

        String exampleInput = """
                1,1
                10,1
                10,10
                1,10
                1,9
                4,9
                4,8
                9,8
                9,2
                2,2
                2,7
                1,7""";
        System.out.println("exampleInput = " + exampleInput);
        // Shape:
        // ┌────────┐
        // │┌──────┐│
        // ││      ││
        // ││      ││
        // ││      ││
        // ││      ││
        // └┘      ││
        //    ┌────┘│
        // ┌──┘     │
        // └────────┘

        BufferedReader br = new BufferedReader(new StringReader(exampleInput));
        h.processLines(br);

        // Produce final answer
        h.produceFinalAnswer();

        // Check final answer
        assertEquals(100, h.getFinalAnswer());
        assertEquals(21, h.getFinalAnswerPartTwo());
    }

}
