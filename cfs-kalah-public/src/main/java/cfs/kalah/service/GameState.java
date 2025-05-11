package cfs.kalah.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Current state of a game
 * 
 * Pits are setup like this:
 * 
 *   [14] 13 12 11 10  9  8        Player 2
 *         1  2  3  4  5  6 [7]    Player 1
 *
 * The pits in square brackets are the 'home' pits of each player.
 * Each non-home pit has an "opposite pit" on the other player's side.
 * 
 */
public class GameState {
    
    private static final List INITIAL_STATE = Arrays.asList(6,6,6,6,6,6, 0, 6,6,6,6,6,6, 0);
    
    /**
     * Usually stores 14 integers
     * 1-7 are for player 1 - their home is at 7
     * 8-14 are for player 2 - their home is at 14
     */
    private final List<Pit> pits;
    
    private static final Map<Integer, Pit> playerNumToHomePit = new HashMap<>(); 

    public GameState() {
        pits = new ArrayList<>();
        initPitsForPlayer(1);
        initPitsForPlayer(2);
    }

    private void initPitsForPlayer(int playerNum) {
        int pitNum = pits.size() + 1;
        for (int i=0; i < 6; i++) {
            pits.add(new Pit(pitNum, playerNum, false));
            pitNum++;
        }
        Pit homePit = new Pit(pitNum, playerNum, true);
        pits.add(homePit);
        playerNumToHomePit.put(playerNum, homePit);
    }

    public static GameState createInitialGameState() {
        GameState result = new GameState();
        result.setPitsContents(INITIAL_STATE);
        return result;
    }

    // pitNum is 1-based
    public Pit getPit(int pitNum) {
        validatePitNum(pitNum);
        return pits.get(pitNum - 1);
    }

    // pitNum is 1-based
    private void validatePitNum(int pitNum) {
        if (pitNum <= 0 || pitNum > pits.size()) {
            throw new RuntimeException("Invalid pit number: " + pitNum);
        }
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        boolean space = false;
        for (Pit pit : pits) {
            if (!first) sb.append(",");
            if (space) sb.append(" ");
            space = false;
            first = false;
            if (pit.isHome()) {
                sb.append("[" + pit.contents + "]");
                space = true;
            } else {
                sb.append(pit.contents);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * A Pit within the game
     */
    class Pit {
        private final int pitNum;       // 1-based
        private final int playerNum;    // 1 or 2
        private final boolean home;     // Am I the player's "home" pit?
        private int contents;           // Number of stones/seeds in this pit
        
        public Pit(int pitNum, int playerNum, boolean home) {
            this.pitNum = pitNum;
            this.playerNum = playerNum;
            this.home = home;
            this.contents = 0;
        }

        public int getPitNum() {
            return pitNum;
        }

        public int getPlayerNum() {
            return playerNum;
        }

        public boolean isHome() {
            return home;
        }

        public int getContents() {
            return contents;
        }

        public void setContents(int contents) {
            this.contents = contents;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pit pit = (Pit) o;
            return pitNum == pit.pitNum &&
                    playerNum == pit.playerNum &&
                    home == pit.home &&
                    contents == pit.contents;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pitNum, playerNum, home, contents);
        }

        @Override
        public String toString() {
            return "Pit{" +
                    "pitNum=" + pitNum +
                    ", playerNum=" + playerNum +
                    ", home=" + home +
                    ", contents=" + contents +
                    '}';
        }

        public Pit nextPitForPlayer() {
            int nextPitNum = nextPitNumWithWrap(this.pitNum);
            Pit resultMaybe = pits.get(nextPitNum-1);
            if (resultMaybe.playerNum == this.playerNum) {
                return resultMaybe;
            } else {
                return firstPitForPlayer(this.playerNum);
            }
        }

        public void addToContents(int increment) {
            this.contents += increment;
        }

        // See diagram at top of this file for opposite pits
        // If called on a home pit, will return an empty Optional - otherwise returns the opposite pit
        public Optional<Pit> getOppositePit() {
            if (this.home) {
                return Optional.empty();
            }
            int diffToPlayer1Home = 7 - this.pitNum;
            int oppositePitNum = this.pitNum + 2 * diffToPlayer1Home;
            return Optional.of(getPit(oppositePitNum));
        }
    }

    Pit firstPitForPlayer(int playerNum) {
        return playerNum == 1 ? pits.get(0) : pits.get(7);
    }
    
    Pit getHomePitForPlayer(int playerNum) {
        // cfstodo: Validate playerNum
        return playerNumToHomePit.get(playerNum);
    }

    private int nextPitNumWithWrap(int pitNum) {
        if (pitNum < pits.size()) {
            return pitNum + 1;
        } else {
            return 1;
        }
    }

    // Sets out pits contents from a list of integers
    public void setPitsContents(List<Integer> pits) {
        for (int i=0; i < pits.size(); i++) {
            Integer newContents = pits.get(i);
            setPitContents(i+1, newContents);
        }
    }

    // Turn our list of Pits into a list of integers, where each integer is the 
    public List<Integer> getPitsContents() {
        return this.pits.stream().map(Pit::getContents).collect(Collectors.toList());
    }

    public int getPitsSize() {
        return pits.size();
    }

    /**
     * pitNum is 1-based
     */
    public int getPitContents(int pitNum) {
        validatePitNum(pitNum);
        return pits.get(pitNum-1).getContents();
    }

    /**
     * pitNum is 1-based
     */
    public void setPitContents(int pitNum, int contents) {
        validatePitNum(pitNum);
        pits.get(pitNum-1).setContents(contents);
    }

    /**
     * Get a Score instance for the specified player
     */
    public Score getScoreForPlayer(int playerNum) {
        int inPlay = this.pits.stream()
                .filter(p -> p.getPlayerNum() == playerNum)
                .filter(p -> !p.isHome())
                .mapToInt(p -> p.getContents())
                .sum();
        int inHome = getHomePitForPlayer(playerNum).getContents();
        return new Score(playerNum, inPlay, inHome);
    }

    /**
     * Shows how many stones are "in play" (in non-home pits)
     * and how many are "in home"
     */
    public static class Score {
        final int playerNum;
        final int inPlay;
        final int inHome;

        public Score(int playerNum, int inPlay, int inHome) {
            this.playerNum = playerNum;
            this.inPlay = inPlay;
            this.inHome = inHome;
        }

        public int getPlayerNum() {
            return playerNum;
        }

        public int getInPlay() {
            return inPlay;
        }

        public int getInHome() {
            return inHome;
        }
        
        public int getTotalScore() {
            return inPlay + inHome;
        }
    }

    /**
     * Check if one side has run out of stones
     */
    public boolean isOneSideRunOutOfStones() {
        return getScoreForPlayer(1).getInPlay() == 0 || getScoreForPlayer(2).getInPlay() == 0;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "pits=" + pits +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return Objects.equals(pits, gameState.pits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pits);
    }
}
