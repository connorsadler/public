package cfs.adventofcode2025.day08;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Main {

    static File inputFile = new File("./src/main/java/cfs/adventofcode2025/day08/input.txt");

    
    public static void main(String[] args) {
        Main m = new Main();
        m.runMain();
    }
    
    void runMain() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));

            // Cant use this for now, as the .txt file is not marked as a resource, so is not in the classpath at runtime
//            InputStream resourceInputStream = Main.class.getResourceAsStream("./input.txt");
//            BufferedReader br = new BufferedReader(new InputStreamReader(resourceInputStream));

            Helper h = new Helper();
            h.processLines(br);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static class Helper {
        int numberOfConnectionsToProcess = 10;

        private List<JunctionBox> junctionBoxes = new ArrayList<>();
        private SortedSet<PossibleConnection> possibleConnections = new TreeSet<>();
        private int finalAnswer;
        private long finalAnswerPartTwo;

        public void processLines(BufferedReader br) throws IOException {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                processLine(lineNum++, line);
            }
        }

        void processLine(int lineNum, String line) {
            System.out.println("processLine " + lineNum + " : " + line);

            JunctionBox l = parseLine(lineNum, line);
            junctionBoxes.add(l);
        }

        // e.g. 162,817,812
        JunctionBox parseLine(int lineNum, String line) {
            int circuitId = lineNum;
            String[] elements = line.split(",");
            int x = Integer.parseInt(elements[0]);
            int y = Integer.parseInt(elements[1]);
            int z = Integer.parseInt(elements[2]);
            JunctionBox result = new JunctionBox(circuitId, x,y,z);
            return result;
        }

        public int getLineCount() {
            return junctionBoxes.size();
        }

        public void produceFinalAnswer() {
            //calcFinalAnswerPartTwo();
            calcFinalAnswer();

            System.out.println("--------------------------------------------------");
            System.out.println(String.format("Final answer: %s", finalAnswer));
            System.out.println(String.format("Final answer part two: %s", finalAnswerPartTwo));
            System.out.println("--------------------------------------------------");
        }
        
        public void calcFinalAnswer() {
            System.out.println("calcFinalAnswer");

            int numCircuits = junctionBoxes.size();

//            double minDistanceSoFar = -1;
//            JunctionBox result1 = null, result2 = null;
            for (int i=0; i < junctionBoxes.size(); i++) {
                JunctionBox jb1 = junctionBoxes.get(i);
                for (int j=i+1; j < junctionBoxes.size(); j++) {
                    JunctionBox jb2 = junctionBoxes.get(j);
                    double dist = jb1.distanceTo(jb2);

                    PossibleConnection possibleConnection = new PossibleConnection(jb1, jb2, dist);
                    possibleConnections.add(possibleConnection);

//                    // Store min one... might not need this
//                    if (minDistanceSoFar < 0 || dist < minDistanceSoFar) {
//                        minDistanceSoFar = dist;
//                        result1 = jb1;
//                        result2 = jb2;
//                    }
                }
            }
//            System.out.println(String.format("min dist found, %s to %s", result1.toString(), result2.toString()));


            System.out.println(String.format("possibleConnections set size: %s", possibleConnections.size()));

            // Part One
//            System.out.println("Checking min distances");
//            Iterator<PossibleConnection> iter = possibleConnections.iterator();
//            for (int i=1; i <= numberOfConnectionsToProcess; i++) {
//                PossibleConnection item = iter.next();
//                System.out.println(String.format("item %s = %s", i, item));
//
//                if (item.isSameCircuit()) {
//                    // Do nothing, already in same circuit
//                    System.out.println("=> Already in same circuit, do nothing");
//                } else {
//                    System.out.println("=> In different circuits, move one");
//                    moveAllInCircuitToAnotherCircuit(item.jb2.circuitId, item.jb1.circuitId);
//                    numCircuits--;
//                }
//            }
//
//            // Check unique circuits
//            Map<Integer, Integer> circuitIdToJunctionBoxCount = new HashMap<>();
//            junctionBoxes.stream().forEach(jb -> {
//                int currentCount = circuitIdToJunctionBoxCount.getOrDefault(jb.circuitId, 0);
//                circuitIdToJunctionBoxCount.put(jb.circuitId, currentCount+1);
//            });
//            System.out.println(String.format("circuitIdToJunctionBoxCount: %s", circuitIdToJunctionBoxCount));
//
//
//            // Sort them by size, descending
//            List<Integer> circuitSizes = new ArrayList(circuitIdToJunctionBoxCount.values());
//            Collections.sort(circuitSizes);
//            Collections.reverse(circuitSizes);
//
//            // Find largest 3 circuits
//            // Multiple sizes together
//            finalAnswer = circuitSizes.get(0) * circuitSizes.get(1) * circuitSizes.get(2);

            // Part Two
            System.out.println("Checking min distances");
            Iterator<PossibleConnection> iter = possibleConnections.iterator();
            PossibleConnection finalConnection = null;
            int i = 1;
            while (true) {
                PossibleConnection item = iter.next();
                System.out.println(String.format("item %s = %s", i, item));
                i++;

                if (item.isSameCircuit()) {
                    // Do nothing, already in same circuit
                    System.out.println("=> Already in same circuit, do nothing");
                } else {
                    System.out.println("=> In different circuits, move one");
                    moveAllInCircuitToAnotherCircuit(item.jb2.circuitId, item.jb1.circuitId);
                    numCircuits--;
                    System.out.println(String.format("=> numCircuits is now: %s", numCircuits));
                    if (numCircuits == 1) {
                        finalConnection = item;
                        break;
                    }
                }
            }

            finalAnswerPartTwo = finalConnection.jb1.x * finalConnection.jb2.x;
        }

        private void moveAllInCircuitToAnotherCircuit(int fromCircuitId, int toCircuitId) {
            for (JunctionBox jb : junctionBoxes) {
                if (jb.circuitId == fromCircuitId) {
                    System.out.println(String.format("  ... moving %s to circuit %s", jb, toCircuitId));
                    jb.circuitId = toCircuitId;
                }
            }
        }

        public int getFinalAnswer() {
            return finalAnswer;
        }

        public void calcFinalAnswerPartTwo() {
        }


        public long getFinalAnswerPartTwo() {
            return finalAnswerPartTwo;
        }
    }

    static class JunctionBox {
        private int circuitId;
        private int x;
        private int y;
        private int z;

        public JunctionBox(int circuitId, int x, int y, int z) {
            this.circuitId = circuitId;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JunctionBox that = (JunctionBox) o;
            return circuitId == that.circuitId && x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(circuitId, x, y, z);
        }

        public double distanceTo(JunctionBox other) {
            long dx = x - other.x;
            long dy = y - other.y;
            long dz = z - other.z;
            return Math.sqrt(dx*dx + dy*dy + dz*dz);
        }

        @Override
        public String toString() {
            return "JunctionBox{" + x + ", " + y + ", " + z + ", circuit: " + circuitId + " }";
        }
    }

    static class PossibleConnection implements Comparable<PossibleConnection> {
        JunctionBox jb1;
        JunctionBox jb2;
        double distance;

        public PossibleConnection(JunctionBox jb1, JunctionBox jb2, double distance) {
            this.jb1 = jb1;
            this.jb2 = jb2;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "PossibleConnection{" +
                    "jb1=" + jb1 +
                    ", jb2=" + jb2 +
                    ", distance=" + distance +
                    '}';
        }

        // Not really needed but I included it anyway
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PossibleConnection that = (PossibleConnection) o;
            return Double.compare(that.distance, distance) == 0 && Objects.equals(jb1, that.jb1) && Objects.equals(jb2, that.jb2);
        }

        // Not really needed but I included it anyway
        @Override
        public int hashCode() {
            return Objects.hash(jb1, jb2, distance);
        }

        @Override
        public int compareTo(PossibleConnection other) {
            if (this.distance < other.distance) {
                return -1;
            } else if (this.distance > other.distance) {
                return 1;
            } else {
                return 0;
            }
        }

        public boolean isSameCircuit() {
            return jb1.circuitId == jb2.circuitId;
        }
    }

}
