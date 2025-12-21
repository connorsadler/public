package cfs.adventofcode2025.day07;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testParseLine() {
        Main.Helper h = new Main.Helper();
        Main.Line actualLine = h.parseLine("...^");
        
        Main.Line expectedLine = new Main.Line();
        expectedLine.addCell('.');
        expectedLine.addCell('.');
        expectedLine.addCell('.');
        expectedLine.addCell('^');
        
        assertEquals(expectedLine, actualLine);
    }

    @Test
    void testExampleProblem() throws Exception {
        Main.Helper h = new Main.Helper();

        String exampleInput = """
              .......S.......
              ...............
              .......^.......
              ...............
              ......^.^......
              ...............
              .....^.^.^.....
              ...............
              ....^.^...^....
              ...............
              ...^.^...^.^...
              ...............
              ..^...^.....^..
              ...............
              .^.^.^.^.^...^.
              ...............""";
        System.out.println("exampleInput = " + exampleInput);

        BufferedReader br = new BufferedReader(new StringReader(exampleInput));
        h.processLines(br);
        // Check parsing
        assertEquals(16, h.getLineCount());
        assertEquals(7, h.getStartPointOnFirstRow());
        
        // Produce final answer
        h.produceFinalAnswer();
        
        // Check final answer
        assertEquals(21, h.getFinalAnswer());
        assertEquals(40, h.getFinalAnswerPartTwo());
    }

    @Test
    void testFullProblem() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Main.inputFile));
        Main.Helper h = new Main.Helper();
        h.processLines(br);
        h.produceFinalAnswer();

        // Check final answer
        assertEquals(1562, h.getFinalAnswer());
        assertEquals(24292631346665L, h.getFinalAnswerPartTwo());
    }
    
}
