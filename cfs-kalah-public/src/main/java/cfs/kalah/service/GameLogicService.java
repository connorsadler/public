package cfs.kalah.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameLogicService {
    
    private static final Logger LOG = LoggerFactory.getLogger(GameLogicService.class);

    /**
     * Performs the movement of stones around the pits
     * Does NOT check for a winner of the game, this is done outside this method
     * 
     * Note: pitNum aka pitId is 1-based
     */
    public MakeMoveGameLogicResult makeMoveGameLogic(GameState gameState, int currentPlayer, int pitNum) throws RuntimeException {
        LOG.debug(">>> makeMoveGameLogic");
        
        GameState.Pit pit = gameState.getPit(pitNum);
        
        // Validate
        if (pit.getPlayerNum() != currentPlayer) {
            // TODO: I would create explicit exceptions but RuntimeException will do for now
            throw new RuntimeException("Player " + currentPlayer + " cannot make a move for pit " + pitNum);
        }
        if (pit.getContents() == 0) {
            throw new RuntimeException("Cannot make a move for pit " + pitNum + " as it is empty");
        }

        // Main logic to remove stones from chosen pit and sow them into subsequent pits
        int remainingStones = pit.getContents();
        pit.setContents(0);
        while (remainingStones > 0) {
            // Move to next pit for player
            pit = pit.nextPitForPlayer();
            // Put a stone in the pit
            pit.addToContents(1);
            remainingStones--;
        }

        boolean extraTurn = false;
        // If the players last stone lands in his own Kalah, he gets another turn.
        // This can be repeated any number of times before it's the other player's turn.
        if (pit.isHome()) {
            LOG.debug("final pit {} is home pit - extra turn", pit.getPitNum());
            extraTurn = true;
        } else {
            LOG.debug("final pit {} is not home pit - no extra turn", pit.getPitNum());
            // When the last stone lands in an own empty pit, the player captures this stone and all stones in the opposite pit (the
            // other players' pit) and puts them in his own Kalah.
            if (pit.getContents() == 1) {
                LOG.debug("final pit {} was empty - adding to home", pit.getPitNum());
                int forMyHome = 1;
                pit.setContents(0);
                GameState.Pit oppositePit = pit.getOppositePit().get();
                if (oppositePit.getContents() > 0) {
                    LOG.debug("stealing from opposite pit {}, stealing contents: {}", oppositePit.getPitNum(), oppositePit.getContents());
                    forMyHome += oppositePit.getContents();
                    oppositePit.setContents(0);
                } else {
                    LOG.debug("nothing to steal from opposite pit {}", oppositePit.getPitNum());
                }
                // Add to my home
                GameState.Pit homePit = gameState.getHomePitForPlayer(pit.getPlayerNum());
                homePit.addToContents(forMyHome);
            }
        }

        LOG.debug("<<< makeMoveGameLogic");
        
        return new MakeMoveGameLogicResult(gameState, extraTurn);
    }
}
