package cfs.kalah.service

import cfs.kalah.TestData
import cfs.kalah.domain.Game
import cfs.kalah.repository.GameRepository
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Service layer test
 * Uses Spock framework
 * All dependencies are mocked
 */
class GameServiceTest extends Specification {

    GameRepository gameRepository = Mock()
    GameLogicService gameLogicService = Mock()

    GameService service = new GameService(gameRepository: gameRepository, gameLogicService: gameLogicService)

    def "test countGames"() {
        given: 'the service is available'
        and: 'the repository contains some games'
            1 * gameRepository.count() >> 2

        when: 'countGames is called'
            def result = service.countGames()

        then: 'the result is correct'
            result == 2
    }

    def "test createGame - success"() {
        given: 'the service is available'
        
        and: 'the repository will be called to create a game'
            1 * gameRepository.save(_) >> { args ->
                Game game = args.first()
                assert game.id == null
                assert game.currentPlayerTurn == 1
                game
            }

        when: 'createGame is called'
            service.createGame()

        then: 'the interactions above will take place'
    }

    @Unroll
    def "test makeMove - success - #desc"() {
        given: 'the service and ids are available'
            def gameId = TestData.GAME_ID
            Game game = new Game()
            game.setId(gameId)
            game.setCurrentPlayerTurn(1)
            game.setPits([1,1,1,1,1,1,0, 1,1,1,1,1,1,0])
            def pitId = 2
        
        and: 'the game is found'
            1 * gameRepository.findById(gameId) >> Optional.of(game)

        and: 'the gameLogicService responds appropriately'
            1 * gameLogicService.makeMoveGameLogic(_, _, _) >> { args ->
                // Check args
                GameState gameStateParam = args[0]
                int currentPlayerArg = args[1]
                assert currentPlayerArg == 1
                int pitNumArg = args[2]
                assert pitNumArg == pitId
                
                // Make a dummy change as part of the game logic making the move
                //gameStateParam.setPitContents(1, 9)
                gameStateParam.setPitsContents(changedPitsContents)
                
                return new MakeMoveGameLogicResult(gameStateParam, extraTurn)
            }

        when: 'makeMove is called'
            GameState result = service.makeMove(gameId, pitId)

        then: 'the returned gameState is the changed one'
            assert result.getPitsContents() == changedPitsContents
        and: 'the game pits have been updated with the new gameState'
            assert game.pits == changedPitsContents
        and: 'the game player only moves on if there is no extra turn'
            assert game.currentPlayerTurn == expectedNewCurrentPlayerTurn
        and: 'the game may have a winner'
            assert game.winner == expectedWinner
        
        where:
            desc            | changedPitsContents               | extraTurn | expectedNewCurrentPlayerTurn  | expectedWinner
            "no extraTurn"  | [9,1,1,1,1,1,0, 1,1,1,1,1,1,0]    | false     | 2                             | null
            "extraTurn"     | [9,1,1,1,1,1,0, 1,1,1,1,1,1,0]    | true      | 1                             | null
            "winner 1"      | [9,1,1,1,1,1,0, 0,0,0,0,0,0,1]    | false     | 2                             | 1
            "winner 2"      | [0,0,0,0,0,0,1, 9,1,1,1,1,1,0]    | false     | 2                             | 2
            "draw"          | [0,0,0,0,0,0,1, 1,0,0,0,0,0,0]    | false     | 2                             | 0
    }

    def "test makeMove - game is already over"() {
        given: 'the service and ids are available'
            def gameId = TestData.GAME_ID
            Game game = new Game()
            game.setId(gameId)
            game.setCurrentPlayerTurn(1)
            game.setPits([1,1,1,1,1,1,0, 1,1,1,1,1,1,0])
            def pitId = 2
            game.setWinner(1)

        and: 'the game is found'
            1 * gameRepository.findById(gameId) >> Optional.of(game)

        when: 'makeMove is called'
            GameState result = service.makeMove(gameId, pitId)

        then: 'the expected exception is thrown'
            def e = thrown(RuntimeException)
            assert e.message == "Game is already over, cannot make a move for game: ${gameId}"
            
        and: 'the gameLogicService is not called'
            0 * gameLogicService._
    }


    def "test calcWinner"() {
        given: 'the service and ids are available'
            GameState gameState = new GameState().tap { it.setPitsContents(usePitContents) };

        when: 'calcWinner is called'
            def result = service.calcWinner(gameState)

        then: 'the result is as expected'
            assert result == expectedResult

        where:
            desc            | usePitContents                 | expectedResult
            "player 1 wins" | [9,1,1,1,1,1,0, 1,1,1,1,1,1,0] | 1
            "player 2 wins" | [1,1,1,1,1,1,0, 9,1,1,1,1,1,0] | 2
            "draw"          | [1,1,1,1,1,1,0, 1,1,1,1,1,1,0] | 0
    }


}
