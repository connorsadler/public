package cfs.adventofcode2025.day08;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void testParseLine() {
        Main.Helper h = new Main.Helper();
        Main.JunctionBox actualJunctionBox = h.parseLine(1, "162,817,812");
        Main.JunctionBox expectedJunctionBox = new Main.JunctionBox(1, 162,817,812);
        assertEquals(expectedJunctionBox, actualJunctionBox);
    }

    @Test
    void testDistanceTo() {
        Main.JunctionBox jb1 = new Main.JunctionBox(1, 162,817,812);
        Main.JunctionBox jb2 = new Main.JunctionBox(2, 163,827,812);
        double expectedDistance = 10.04987562112089;
        assertEquals(expectedDistance, jb1.distanceTo(jb2));
        assertEquals(expectedDistance, jb2.distanceTo(jb1));
    }

    @Test
    void testExampleProblem() throws Exception {
        Main.Helper h = new Main.Helper();

        String exampleInput = """
               162,817,812
               57,618,57
               906,360,560
               592,479,940
               352,342,300
               466,668,158
               542,29,236
               431,825,988
               739,650,466
               52,470,668
               216,146,977
               819,987,18
               117,168,530
               805,96,715
               346,949,466
               970,615,88
               941,993,340
               862,61,35
               984,92,344
               425,690,689""";
        System.out.println("exampleInput = " + exampleInput);

        BufferedReader br = new BufferedReader(new StringReader(exampleInput));
        h.processLines(br);
        // Check parsing
        assertEquals(20, h.getLineCount());

        h.numberOfConnectionsToProcess = 10; // Only relevant for Part One

        // Produce final answer
        h.produceFinalAnswer();
        
        // Check final answer
        //assertEquals(40, h.getFinalAnswer());
        assertEquals(25272, h.getFinalAnswerPartTwo());
    }

    @Test
    void testFullProblem() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Main.inputFile));
        Main.Helper h = new Main.Helper();

        h.numberOfConnectionsToProcess = 1000; // Only relevant for Part One

        h.processLines(br);
        h.produceFinalAnswer();

        // Check final answer
        //assertEquals(129564, h.getFinalAnswer());
        assertEquals(42047840, h.getFinalAnswerPartTwo());
    }
    
}
