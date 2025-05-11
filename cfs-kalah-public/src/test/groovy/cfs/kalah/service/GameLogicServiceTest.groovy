package cfs.kalah.service

import cfs.kalah.TestData
import spock.lang.Specification

/**
 * Service layer test
 * Uses Spock framework
 * All dependencies are mocked
 */
class GameLogicServiceTest extends Specification {

    // The service we're testing
    GameLogicService service = new GameLogicService()

    GameState gameState = TestData.createGameState()

    def "test makeMoveGameLogic - happy path 1 - simple move"() {
        given: 'we have an existing gameState'
        and: 'we know the pits contents'
            assert gameState.getPitsContents() == [6,5,4,0,0,0,0,  0,0,0,0,0,0,0]
            //                                     ^move
        when: 'makeMoveGameLogic is called'
            def makeMoveResult = service.makeMoveGameLogic(gameState, 1, 1)
        then: 'the result is correct'
            assert makeMoveResult.gameState.getPitsContents() == [0,6,5,1,1,1,1,  0,0,0,0,0,0,0]
            assert makeMoveResult.extraTurn == true     // last stone landed in player's home/kalah, so they get an extra turn
    }
    
    def "test makeMoveGameLogic - happy path 2 - wrap around players last pit, lands on empty pit so steals from opponent"() {
        given: 'we have an existing gameState'
        and: 'we know the pits contents'
            gameState.setPitContents(5, 6)
            gameState.setPitContents(8, 2)
            gameState.setPitContents(9, 2)
            gameState.setPitContents(10, 2)
            gameState.setPitContents(11, 2)
            gameState.setPitContents(12, 2)
            gameState.setPitContents(13, 2)
            assert gameState.getPitsContents() == [6,5,4,0,6,0,0,  2,2,2,2,2,2,0]
            //                                             ^move
        when: 'makeMoveGameLogic is called'
            def makeMoveResult = service.makeMoveGameLogic(gameState, 1, 5)
        then: 'the result is correct'
            assert makeMoveResult.gameState.getPitsContents() == [7,6,5,0,0,1,4,  2,2,0,2,2,2,0]
            assert makeMoveResult.extraTurn == false
    }

    def "test makeMoveGameLogic - happy path 3 - wrap around for player 2"() {
        given: 'we have an existing gameState'
        and: 'we know the pits contents'
            gameState.setPitContents(12, 6)
            assert gameState.getPitsContents() == [6,5,4,0,0,0,0,  0,0,0,0,6,0,0]
            //                                                             ^move
        when: 'makeMoveGameLogic is called'
            def makeMoveResult = service.makeMoveGameLogic(gameState, 2, 12)
        then: 'the result is correct'
            assert makeMoveResult.gameState.getPitsContents() == [6,5,0,0,0,0,0,  1,1,1,0,0,1,6]
            assert makeMoveResult.extraTurn == false
    }


    def "test makeMoveGameLogic - bad pitNum"() {
        given: 'we have an existing gameState'
        when: 'makeMoveGameLogic is called for a bad pitNum'
            def makeMoveResult = service.makeMoveGameLogic(gameState, 1, usePitNum)

        then: 'the expected exception is thrown'
            def e = thrown(RuntimeException)
            assert e.message == "Invalid pit number: ${usePitNum}"
        
        where:
            desc | usePitNum
            "0"  | 0
            "15" | 15
    }

    def "test makeMoveGameLogic - bad pit for player"() {
        given: 'we have an existing gameState'
        when: 'makeMoveGameLogic is called for the other players pit'
            def makeMoveResult = service.makeMoveGameLogic(gameState, 1, 10)

        then: 'the expected exception is thrown'
            def e = thrown(RuntimeException)
            assert e.message == "Player 1 cannot make a move for pit 10"
    }

    def "test makeMoveGameLogic - empty pit"() {
        given: 'we have an existing gameState'
        and: 'a pit is empty'
            gameState.setPitContents(1, 0)
        when: 'makeMoveGameLogic is called for an empty pit'
            def makeMoveResult = service.makeMoveGameLogic(gameState, 1, 1)

        then: 'the expected exception is thrown'
            def e = thrown(RuntimeException)
            assert e.message == "Cannot make a move for pit 1 as it is empty"
    }

}
