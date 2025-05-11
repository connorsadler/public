package cfs.kalah.domain;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Game {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;

    /**
     * Who's move is it?
     * This will be 1 or 2
     */
    private int currentPlayerTurn;

    /**
     * null - no winner yet
     * 0 - draw
     * 1 - player 1 won
     * 2 - player 2 won
     */
    private Integer winner;
    
    // This is an easy way to store list of integers in JPA - good enough for now
    @ElementCollection
    private List<Integer> pits = new ArrayList<>();

    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public void setCurrentPlayerTurn(int currentPlayerTurn) {
        this.currentPlayerTurn = currentPlayerTurn;
    }

    public List<Integer> getPits() {
        return pits;
    }

    public void setPits(List<Integer> pits) {
        this.pits = pits;
    }

    public Integer getWinner() {
        return winner;
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return currentPlayerTurn == game.currentPlayerTurn &&
                Objects.equals(id, game.id) &&
                Objects.equals(winner, game.winner) &&
                Objects.equals(pits, game.pits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currentPlayerTurn, winner, pits);
    }
}
