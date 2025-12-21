package cfs.adventofcode2025.day07;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

    static File inputFile = new File("./src/main/java/cfs/adventofcode2025/day07/input.txt");

    
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
        private List<Line> lines = new ArrayList<>();
        private int finalAnswer;
        private long finalAnswerPartTwo;
        private long countit;
        private long largestRowVisited;
        private Cache cache = new Cache();

        public void processLines(BufferedReader br) throws IOException {
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        }

        void processLine(String line) {
            System.out.println("processLine: " + line);

            Line l = parseLine(line);
            lines.add(l);
        }

        // e.g. .......S.......
        // e.g. .......^.......
        Line parseLine(String line) {
            Line result = new Line();
            
            line.chars().forEach(chInt -> {
                char ch = (char) chInt;
                //System.out.println("ch = " + ch);
                result.addCell(ch);
            });
            
            return result;
        }

        public int getLineCount() {
            return lines.size();
        }

        public int getStartPointOnFirstRow() {
            Line firstRow = lines.get(0);
            return firstRow.findFirstCell('S');
        }

        public void produceFinalAnswer() {
            calcFinalAnswerPartTwo(); // Do this first as it doesnt mutate the data structure
            calcFinalAnswer();
            System.out.println("--------------------------------------------------");
            System.out.println(String.format("Final answer: %s", finalAnswer));
            System.out.println(String.format("Final answer part two: %s", finalAnswerPartTwo));
            System.out.println("--------------------------------------------------");
        }
        
        public void calcFinalAnswer() {
            int splitCount = 0;

            System.out.println("Processing rows");
            // Process each row after the first one
            for (int rowIdx = 1; rowIdx < getLineCount(); rowIdx++) {
                System.out.println(String.format("Processing rowIdx: %s", rowIdx));
                Line line = lines.get(rowIdx);
                Line previousLine = lines.get(rowIdx-1);
                // For each row, propogate rays from row above
                for (int colIdx = 0; colIdx < line.getCellCount(); colIdx++) {
                    char cell = line.getCell(colIdx);
                    char cellAbove = previousLine.getCell(colIdx);
                    if (cellAbove == 'S' || cellAbove == '|') {
                        // ray needs to come down to current row
                        // but it might be split
                        if (cell == '^') {
                            // split
                            splitCount++;
                            if (line.getCell(colIdx-1) != '|') {
                                line.setCell(colIdx - 1, '|');
                            }
                            if (line.getCell(colIdx+1) != '|') {
                                line.setCell(colIdx + 1, '|');
                            }
                        } else {
                            // dont split
                            line.setCell(colIdx, '|');
                        }
                    }
                }
                
                // Show result for this row
                System.out.println(String.format("result for row: %s", line.getLineString()));
            }
            
            finalAnswer = splitCount;
        }

        public int getFinalAnswer() {
            return finalAnswer;
        }

        public void calcFinalAnswerPartTwo() {
            int startColumnIdx = getStartPointOnFirstRow();
            finalAnswerPartTwo = countProjectedPossibleRaysPartTwo(1, startColumnIdx);
            cache.showStats();
        }
        
        // Start from the row specified and column specified, assuming there is a ray hitting that point
        // - If there is no split here, project ray down a row and recurse once
        // - If there is a split here, project ray down a row and recurse twice
        private long countProjectedPossibleRaysPartTwo(int rowIdx, int colIdx) {
            
            countit++;
            if (countit % 100 == 0) {
                System.out.println(String.format("countit: %s", countit));
            }
            
            CacheEntry cachedResult = cache.getResult(rowIdx, colIdx);
            if (cachedResult != null) {
                return cachedResult.result;
            }
            
            long result;
            Line line = lines.get(rowIdx);
            char cell = line.getCell(colIdx);
            if (cell == '^') {
                // split - count possibles left and right and sum them
                long left = countProjectedPossibleRaysPartTwo(rowIdx+1, colIdx-1);
                long right = countProjectedPossibleRaysPartTwo(rowIdx+1, colIdx+1);
                result = left+right;
            } else {
                // dont split
                if (isFinalRow(rowIdx)) {
                    result =  1;
                } else {
                    result = countProjectedPossibleRaysPartTwo(rowIdx+1, colIdx);
                }
            }

            cache.putResult(rowIdx, colIdx, result);
            
            return result;
        }

        private boolean isFinalRow(int rowIdx) {
            return rowIdx == lines.size()-1;
        }

        public long getFinalAnswerPartTwo() {
            return finalAnswerPartTwo;
        }
    }
    
    /**
     *     A
     *    B C
     * - we calc A
     * - lets imagine it has to calc B, but C is cached
     * - calc B takes 50ms
     * - calc C originally took 100ms
     * - calc time for A is around 50ms, but we must add on the original 100ms, so total effective time for A is 150ms
     *
     * TODO: Not quite sure how to do this, leaving it for now - an exercise for a later date
     */
    static class CacheEntry {
        long result;

        public CacheEntry(long result) {
            this.result = result;
        }
    }
    
    static class Cache {
        private final Map<Integer, Map<Integer, CacheEntry>> map = new HashMap<>();

        private long cacheHits;

        public CacheEntry getResult(int rowIdx, int colIdx) {
            Map<Integer, CacheEntry> rowMap = map.get(rowIdx);
            if (rowMap != null) {
                CacheEntry cacheEntry = rowMap.get(colIdx);

                if (cacheEntry != null) {
                    cacheHits++;
                    if (cacheHits % 100 == 0) {
                        System.out.println(String.format("cacheHits: %s", cacheHits));
                    }
                }

                return cacheEntry;
            }
            return null;
        }

        public void putResult(int rowIdx, int colIdx, long result) {
            Map<Integer, CacheEntry> rowMap = map.computeIfAbsent(rowIdx, k -> new HashMap<>());
            rowMap.put(colIdx, new CacheEntry(result));
        }

        public void showStats() {
            System.out.println(String.format("Cache stats:"));
            System.out.println(String.format("  cacheHits: %s", cacheHits));
        }
    }

    static class Line {
        private List<Character> cells = new ArrayList<>();

        public void addCell(Character cell) {
            this.cells.add(cell);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Line line = (Line) o;
            return Objects.equals(cells, line.cells);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(cells);
        }

        // Find first cell with the char specified
        public int findFirstCell(char s) {
            return cells.indexOf(s);
        }

        public int getCellCount() {
            return cells.size();
        }

        public char getCell(int colIdx) {
            return cells.get(colIdx).charValue();
        }

        public void setCell(int colIdx, char ch) {
            cells.set(colIdx, ch);
        }

        public String getLineString() {
            return cells.stream().map(Object::toString).collect(Collectors.joining());
        }
    }

}
