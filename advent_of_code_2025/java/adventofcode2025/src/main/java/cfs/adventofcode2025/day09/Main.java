package cfs.adventofcode2025.day09;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;


public class Main {

    static File inputFile = new File("./src/main/java/cfs/adventofcode2025/day09/input.txt");

    
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

    static class Grid {

        List<GridRow> rows = new ArrayList<>();

        // Either a single char or a horizontal line
        class Entry {
            Character ch;
            int fromIdx;
            int toIdx;

            public Entry(Character ch, int fromIdx, int toIdx) {
                this.ch = ch;
                this.fromIdx = fromIdx;
                this.toIdx = toIdx;
            }

            @Override
            public String toString() {
                return "Entry{" +
                        "ch=" + ch +
                        ", fromIdx=" + fromIdx +
                        ", toIdx=" + toIdx +
                        '}';
            }

            public boolean isRange() {
                return ch == null;
            }

            public boolean contains(int x1, int x2) {
                return fromIdx != -1 && (fromIdx <= x1 && toIdx >= x2);
            }
        }

        class GridRow {
            //SortedMap<Integer, Character> colIdxToChar = new TreeMap<>();
            SortedMap<Integer, Entry> colIdxToEntry = new TreeMap<>();

            public String showAsString() {
                StringBuilder sb = new StringBuilder();
                //sb.append("TODO - GridRow.showAsString");
                sb.append(colIdxToEntry);
//            if (colIdxToChar.size() == 0) {
//                sb.append("_");
//            } else {
//                int maxCol = colIdxToChar.lastKey();
//                for (int i = 0; i <= maxCol; i++) {
//                    sb.append(".");
//                }
//            }

                return sb.toString();
            }

            public void joinHorizLine(int x1, int x2) {
                if (x2 < x1) {
                    throw new RuntimeException("Bad args: " + x1 + " to " + x2);
                }
                colIdxToEntry.put(x1, new Entry(null, x1, x2));
            }

            public void joinSingleChar(int x) {
                if (colIdxToEntry.containsKey(x)) {
                    //throw new RuntimeException("error, key already exists");
                    // Dont replace a range or otherwise with a single char - ignore
                    return;
                }
                colIdxToEntry.put(x, new Entry('X', -1, -1));
            }

            public void collapseToRanges(int rowIdx) {
                // Collapse each pair of 'single char' entries to a range
                // After this, all Entry items will be 'range' type
                Set<Map.Entry<Integer, Entry>> oldEntries = colIdxToEntry.entrySet();
                SortedMap<Integer, Entry> newEntries = new TreeMap<>();

                Map.Entry<Integer, Entry> pendingEntry = null;
                for (Map.Entry<Integer, Entry> currentEntry : oldEntries) {
                    if (currentEntry.getValue().isRange()) {
                        newEntries.put(currentEntry.getKey(), currentEntry.getValue());
                    } else {
                        if (pendingEntry == null) {
                            pendingEntry = currentEntry;
                        } else {
                            // Combine this entry with pendingEntry to make a range entry
                            Entry combined = new Entry(null, pendingEntry.getKey(), currentEntry.getKey());
                            newEntries.put(combined.fromIdx, combined);
                            pendingEntry = null;
                        }
                    }
                }

                // Trailing pending entry - this is in the example on rowIdx 5 - we must add this to our range
                if (pendingEntry != null) {
                    //throw new RuntimeException("Hmmm problem on row when collapsing on rowIdx: " + rowIdx);
                    Entry lastEntry = newEntries.get(newEntries.lastKey());
                    lastEntry.toIdx = pendingEntry.getKey();
                }

                // Merge adjacent ranges which overlap
                // Needed for testExampleProblemPartTwoAdvanced, as row 3 has a split range like this:
                //   row:3={2=Entry{ch=null, fromIdx=2, toIdx=7}, 7=Entry{ch=null, fromIdx=7, toIdx=11}}
                SortedMap<Integer, Entry> newEntries2 = new TreeMap<>();
                Entry prev = null;
                for (Map.Entry<Integer, Entry> currentEntry : newEntries.entrySet()) {
                    if (prev != null && prev.toIdx == currentEntry.getValue().fromIdx) {
                        // Expand previous range
                        prev.toIdx = currentEntry.getValue().toIdx;
                    } else {
                        newEntries2.put(currentEntry.getKey(), currentEntry.getValue());
                        prev = currentEntry.getValue();
                    }
                }

                colIdxToEntry.clear();
                colIdxToEntry.putAll(newEntries2);
            }

            // Check the x1->x2 inclusive is contained in one of our ranges
            public boolean checkAllInsideRanges(int x1, int x2) {
                if (colIdxToEntry.isEmpty()) {
                    return false;
                }
                Set<Map.Entry<Integer, Entry>> oldEntries = colIdxToEntry.entrySet();
                for (Map.Entry<Integer, Entry> currentEntry : oldEntries) {

                    // Not found yet and we are past the end of our search - failed
                    if (currentEntry.getValue().fromIdx > x2) {
                        return false;
                    }
                    // Current entry contains our range - found
                    if (currentEntry.getValue().contains(x1,x2)) {
                        return true;
                    }
                }
                return false;
            }
        }

        public Grid() {
        }

        private GridRow getRow(int y) {
            while (rows.size() < y+1) {
                rows.add(new GridRow());
            }
            return rows.get(y);
        }

        public void join(RedTile rtFrom, RedTile rtTo) {
            if (rtFrom.y == rtTo.y) {
                // Horizontal line
                RedTile from, to; // from is smaller x coord, to is larger x coord
                if (rtFrom.x < rtTo.x) {
                    from = rtFrom;
                    to = rtTo;
                } else {
                    to = rtFrom;
                    from = rtTo;
                }
                getRow(from.y).joinHorizLine(from.x, to.x);
            } else {
                // Vertical line
                RedTile from, to; // from is smaller y coord, to is larger y coord
                if (rtFrom.y < rtTo.y) {
                    from = rtFrom;
                    to = rtTo;
                } else {
                    to = rtFrom;
                    from = rtTo;
                }
                for (int rowIdx = from.y; rowIdx <= to.y; rowIdx++) {
                    getRow(rowIdx).joinSingleChar(from.x);
                }
            }
        }

        public void collapseToRanges() {
            //System.out.println("collapseToRanges for rows size: " + rows.size());

            long start = System.currentTimeMillis();
            int rowIdx = 0;
            for (GridRow row : rows) {
                row.collapseToRanges(rowIdx);
                rowIdx++;
            }

            long timeTaken = System.currentTimeMillis()-start;
            //System.out.println("collapseToRanges timeTaken = " + timeTaken);
        }

        public void show() {
            if (rows.size() > 100) {
                System.out.println("too big to show");
                return;
            }

            int rowIdx = 0;
            for (GridRow row: rows) {
                System.out.println("row:" + rowIdx + "=" + row.showAsString());
                rowIdx++;
            }
        }

    }

    static class Helper {

        private List<RedTile> redTiles = new ArrayList<>();
        Grid grid = new Grid();
        private long finalAnswer;
        private long finalAnswerPartTwo;
        private int comparisonCount;
        private Rectangle resultPartTwoRectangle = null;

        public void processLines(BufferedReader br) throws IOException {
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        }

        void processLine(String line) {
            //System.out.println("processLine: " + line);

            RedTile l = parseLine(line);
            redTiles.add(l);
        }

        // e.g. 7,1
        RedTile parseLine(String line) {
            String[] elements = line.split(",");
            int x = Integer.parseInt(elements[0]);
            int y = Integer.parseInt(elements[1]);
            RedTile result = new RedTile(x,y);
            return result;
        }

        public int getLineCount() {
            return redTiles.size();
        }

        public void produceFinalAnswer() {
            calcFinalAnswer();
            calcFinalAnswerPartTwo();

            System.out.println("--------------------------------------------------");
            System.out.println(String.format("red tiles count: %s", redTiles.size()));
            System.out.println(String.format("comparisonCount: %s", comparisonCount));
            System.out.println("--------------------------------------------------");
            System.out.println(String.format("Final answer: %s", finalAnswer));
            System.out.println(String.format("Final answer part two: %s", finalAnswerPartTwo));
            System.out.println("--------------------------------------------------");
        }

        public void calcFinalAnswer() {
            System.out.println("calcFinalAnswer");

            System.out.println(String.format("Number of red tiles: %s", redTiles.size()));
            long  maxSoFar = 0;
            for (int i=0; i < redTiles.size(); i++) {
                RedTile rt1 = redTiles.get(i);
                for (int j=i+1; j < redTiles.size(); j++) {
                    RedTile rt2 = redTiles.get(j);
                    long area = rt1.calcAreaTo(rt2);
                    if (area > maxSoFar) {
                        maxSoFar = area;
                    }
                    comparisonCount++;
                }
            }

            finalAnswer = maxSoFar;
            finalAnswerPartTwo = -1;
        }

        public long getFinalAnswer() {
            return finalAnswer;
        }

        final long answer1 = 1637556834L;
        final long answer2 = 1632252213L;

        public void calcFinalAnswerPartTwo() {
            System.out.println("calcFinalAnswerPartTwo");

            System.out.println(String.format("Number of red tiles: %s", redTiles.size()));

            // Setup
            calcRedGreenTileMap();

            // Calc loop
            long maxSoFar = 0;
            for (int i=0; i < redTiles.size(); i++) {
                RedTile rt1 = redTiles.get(i);
                for (int j=i+1; j < redTiles.size(); j++) {
                    RedTile rt2 = redTiles.get(j);
                    long area = rt1.calcAreaTo(rt2);

                    boolean debug = false;
                    if (area == answer1 || area == answer2) {
                        System.out.println("Got an area equal to one of the answers: " + area);
                        debug = true;
                        System.out.println("  rt1: " + rt1);
                        System.out.println("  rt2: " + rt2);
                    }

                    if (area > maxSoFar) {
                        boolean okForPartTwoResult = okForPartTwo(rt1, rt2, debug);
                        if (debug) {
                            System.out.println("  okForPartTwoResult: " + okForPartTwoResult);
                        }
                        if (okForPartTwoResult) {
                            maxSoFar = area;
                        }
                    }
                    comparisonCount++;
                }
            }

            finalAnswerPartTwo = maxSoFar;
            System.out.println("resultPartTwoRectangle: " + resultPartTwoRectangle);
            System.out.println("checking this with okForPartTwoAdvancedRect...");
            boolean resultOkForPartTwoAdvancedRect = okForPartTwoAdvancedRect(resultPartTwoRectangle, false);
            System.out.println("resultOkForPartTwoAdvancedRect: " + resultOkForPartTwoAdvancedRect);


        }

        private Rectangle calcRectForCoords(int x1, int y1, int x2, int y2) {
            int x = Math.min(x1, x2);
            int y = Math.min(y1, y2);
            int width = Math.abs(x1 - x2) + 1;
            int height = Math.abs(y1 - y2) + 1;
            return new Rectangle(x,y,width,height);
        }

        private boolean okForPartTwo(RedTile rt1, RedTile rt2, boolean debug) {
            int x = Math.min(rt1.x, rt2.x);
            int y = Math.min(rt1.y, rt2.y);
            int width = Math.abs(rt1.x - rt2.x) + 1;
            int height = Math.abs(rt1.y - rt2.y) + 1;
            Rectangle rectangle = new Rectangle(x,y,width,height);

            for (RedTile otherRt : redTiles) {
                if (otherRt != rt1 && otherRt != rt2) {
                    if (rectangle.contains(otherRt.x, otherRt.y)) {

                        // it can be one of the corners of the rectangle
                        // There was a bug in here... see 09.svg where there is another rt just near the bottom left of the purple square
                        // - The purple square is the correct answer square
                        // - actually it can be on any edge of the rectange also, so I changed this to "eitherx OR eithery" rather than AND
                        boolean eitherx = otherRt.x == rt1.x || otherRt.x == rt2.x;
                        boolean eithery = otherRt.y == rt1.y || otherRt.y == rt2.y;
                        if (eitherx || eithery) {
                            // this one is OK
                            //System.out.println("ok...");
                            continue;
                        }

                        if (debug) {
                            System.out.println("Returning false on first bit...");
                            System.out.println("  rt1: " + rt1);
                            System.out.println("  rt2: " + rt2);
                            System.out.println("  otherRt: " + otherRt);
                            System.out.println("  eitherx: " + eitherx);
                            System.out.println("  eithery: " + eithery);
                        }
                        return false;
                    }
                }
            }

            // Advanced check
            if (debug) {
                System.out.println("Doing advanced check");
            }
            if (!okForPartTwoAdvancedRect(rectangle, debug)) {
                return false;
            }

            resultPartTwoRectangle = rectangle;
            return true;
        }

        public boolean okForPartTwoAdvanced(int x1, int y1, int x2, int y2) {
            Rectangle rect = calcRectForCoords(x1, y1, x2, y2);
            return okForPartTwoAdvancedRect(rect, false);
        }

        public boolean okForPartTwoAdvancedRect(Rectangle rect, boolean debug) {
            if (debug) {
                System.out.println(">>> okForPartTwoAdvanced, rect: " + rect);
            }

            for (int row = rect.y; row <= rect.y + rect.height - 1; row++) {
                boolean rowResult = grid.getRow(row).checkAllInsideRanges(rect.x, rect.x+rect.width-1);
                if (!rowResult) {
                    if (debug) {
                        System.out.println("<<< okForPartTwoAdvanced, return false on row: " + row);
                    }
                    return false;
                }
            }

            if (debug) {
                System.out.println("<<< okForPartTwoAdvanced, return true");
            }
            return true;
        }


        private void calcRedGreenTileMap() {

            // Place red tiles in grid
            // Join list of tiles with green
            RedTile prev = null;
            for (RedTile rt : redTiles) {
                //grid.putTile(rt.x, rt.y, '#');
                if (prev != null) {
                    grid.join(prev, rt);
                }
                prev = rt;
            }
            grid.join(prev, redTiles.get(0));

            System.out.println("Showing grid - BEFORE COLLAPSE:");
            grid.show();
            //System.out.println("too big... not showing it");

            // Collapse
            grid.collapseToRanges();

            System.out.println("Showing grid - AFTER COLLAPSE:");
            grid.show();

        }


        public long getFinalAnswerPartTwo() {
            return finalAnswerPartTwo;
        }
    }

    static class RedTile {
        private int x;
        private int y;

        public RedTile(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "RedTile{" + x + ", " + y + " }";
        }

        public long calcAreaTo(RedTile rt2) {
            int dx = Math.abs(this.x - rt2.x);
            int dy = Math.abs(this.y - rt2.y);
            return (long) (dx + 1) * (dy+1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RedTile redTile = (RedTile) o;
            return x == redTile.x && y == redTile.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }


}
