package cfs.kalah.service;

import java.util.Objects;

/**
 * The result of making a move
 * Tells us the new game state and whether it's our move again
 */
public class MakeMoveGameLogicResult {
    
    private GameState gameState;
    private boolean extraTurn;

    public MakeMoveGameLogicResult(GameState gameState, boolean extraTurn) {
        this.gameState = gameState;
        this.extraTurn = extraTurn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isExtraTurn() {
        return extraTurn;
    }

    public void setExtraTurn(boolean extraTurn) {
        this.extraTurn = extraTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MakeMoveGameLogicResult that = (MakeMoveGameLogicResult) o;
        return extraTurn == that.extraTurn &&
                Objects.equals(gameState, that.gameState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameState, extraTurn);
    }
}
