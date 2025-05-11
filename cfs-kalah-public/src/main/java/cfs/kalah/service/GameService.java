package cfs.kalah.service;

import cfs.kalah.domain.Game;
import cfs.kalah.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Transactional
public class GameService {
    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);
    
    @Autowired
    GameRepository gameRepository;
    @Autowired
    GameLogicService gameLogicService;
    
    public long countGames() {
        return gameRepository.count();
    }

    public UUID createGame() {
        Game game = new Game();
        game.setCurrentPlayerTurn(1);
        GameState gameState = GameState.createInitialGameState();
        game.setPits(gameState.getPitsContents());
        game = gameRepository.save(game);
        return game.getId();
    }

    // pitId is 1-based
    public GameState makeMove(UUID gameId, int pitId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found: " + gameId));
        
        // Game already over?
        if (game.getWinner() != null) {
            throw new RuntimeException("Game is already over, cannot make a move for game: " + gameId);
        }

        // Read current GameState
        GameState gameState = new GameState();
        gameState.setPitsContents(game.getPits());
        
        // Game Logic
        // Move the stones around the pits - done by another service
        MakeMoveGameLogicResult makeMoveGameLogicResult = gameLogicService.makeMoveGameLogic(gameState, game.getCurrentPlayerTurn(), pitId);

        // Update the game state in the database
        GameState newGameState = makeMoveGameLogicResult.getGameState();
        game.setPits(newGameState.getPitsContents());
        
        if (LOG.isDebugEnabled()) LOG.debug("newGameState summary: {}", newGameState.getSummary());

        //
        // after move checks
        //
        
        if (makeMoveGameLogicResult.isExtraTurn()) {
            // Dont change player
            LOG.debug("Extra turn - don't change player");
        } else {
            int nextPlayer = game.getCurrentPlayerTurn() == 1 ? 2 : 1;
            LOG.debug("Changing to next player {}", nextPlayer);
            game.setCurrentPlayerTurn(nextPlayer);
        }

        // The game is over as soon as one of the sides run out of stones. The player who still has stones in his/her pits keeps
        //  them and puts them in his/hers Kalah. The winner of the game is the player who has the most stones in his Kalah.
        if (gameState.isOneSideRunOutOfStones()) {
            // We have a winner - who is it?
            LOG.debug("Game over - checking winner");
            int winner = calcWinner(newGameState);
            game.setWinner(winner);
        }

        return makeMoveGameLogicResult.getGameState();
    }

    // Maybe this method could be on GameState, consider later
    int calcWinner(GameState newGameState) {
        int player1Score = newGameState.getScoreForPlayer(1).getTotalScore();
        int player2Score = newGameState.getScoreForPlayer(2).getTotalScore();
        LOG.debug("Player 1 score: {}", player1Score);
        LOG.debug("Player 2 score: {}", player2Score);

        int winner;
        if (player1Score > player2Score) {
            winner = 1;
        } else if (player2Score > player1Score) {
            winner = 2;
        } else {
            winner = 0;
        }
        LOG.debug("winner: {}", winner);
        return winner;
    }
}
