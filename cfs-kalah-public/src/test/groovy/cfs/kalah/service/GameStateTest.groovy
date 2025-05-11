package cfs.kalah.service

import spock.lang.Specification
import spock.lang.Unroll

/**
 * GameStateTest
 */
class GameStateTest extends Specification {

    GameState gameState = new GameState()

    def "test GameState functions"() {
        given: 'we have a gameState'
        
        when: 'we check the pits count'
            def result = gameState.getPitsSize()
        then: 'the result is correct'
            assert result == 14

        and: 'when we check each pit'
        then: 'the results are correct'
            // Player 1
            checkPit(0, 1, 1, false, 0)
            checkPit(1, 2, 1, false, 0)
            checkPit(2, 3, 1, false, 0)
            checkPit(3, 4, 1, false, 0)
            checkPit(4, 5, 1, false, 0)
            checkPit(5, 6, 1, false, 0)
            checkPit(6, 7, 1, true,  0)
            // Player 2
            checkPit(7, 8, 2, false, 0)
            checkPit(8, 9, 2, false, 0)
            checkPit(9, 10, 2, false, 0)
            checkPit(10, 11, 2, false, 0)
            checkPit(11, 12, 2, false, 0)
            checkPit(12, 13, 2, false, 0)
            checkPit(13, 14, 2, true,  0)
        
        and: 'when we get the pits contents as a list'
        then: 'the results are correct'
            assert gameState.getPitsContents() == [0,0,0,0,0,0,0, 0,0,0,0,0,0,0]
        
        // cfstodo: More testing?
    }
    
    def checkPit(pitIdx, expectedPitNum, expectedPlayerNum, expectedHome, expectedContents) {
        assert gameState.pits[pitIdx].pitNum == expectedPitNum
        assert gameState.pits[pitIdx].playerNum == expectedPlayerNum
        assert gameState.pits[pitIdx].home == expectedHome
        assert gameState.pits[pitIdx].contents == expectedContents
        true
    }

    @Unroll
    def "test getOppositePit"() {
        given: 'we have a gameState'
        when: 'we get the opposite pit'
            def resultOpt = gameState.getPit(pitNum).getOppositePit()
        then: 'the result is correct'
            def result = resultOpt.isPresent() ? resultOpt.get().pitNum : null
            assert result == expectedOppositePitNum
        where:
            pitNum | expectedOppositePitNum
            1      | 13
            2      | 12
            3      | 11
            4      | 10
            5      | 9
            6      | 8
            7      | null
            8      | 6
            9      | 5
            10     | 4
            11     | 3
            12     | 2
            13     | 1
            14     | null
    }

    @Unroll
    def "test getHomePitForPlayer"() {
        given: 'we have a gameState'
        when: 'we get the players home pit'
            def result = gameState.getHomePitForPlayer(playerNum)
        then: 'the result is correct'
            assert result.home
            assert result.pitNum == expectedHomePitNum
        where:
            playerNum | expectedHomePitNum
            1         | 7
            2         | 14

    }

    @Unroll
    def "test getScoreForPlayer"() {
        given: 'we have a gameState'
            gameState.setPitsContents([6,5,4,3,2,1,1,  2,3,4,5,6,7,8])
        when: 'we get the players score'
            def result = gameState.getScoreForPlayer(playerNum)
        then: 'the result is correct'
            assert result.playerNum == playerNum
            assert result.inPlay == expectedInPlay
            assert result.inHome == expectedInHome
            assert result.getTotalScore() == expectedInPlay + expectedInHome
        where:
            playerNum | expectedInPlay | expectedInHome
            1         | 6+5+4+3+2+1    | 1
            2         | 2+3+4+5+6+7    | 8
    }

    @Unroll
    def "test isOneSideRunOutOfStones"() {
        given: 'we have a gameState'
            gameState.setPitsContents(pitContents)
        when: 'we get the isOneSideRunOutOfStones'
            def result = gameState.isOneSideRunOutOfStones()
        then: 'the result is correct'
            assert result == expectedResult
        where:
            pitContents                         | expectedResult
            [6,5,4,3,2,1,1,  2,3,4,5,6,7,8]     | false
            [0,0,0,0,0,0,1,  2,3,4,5,6,7,8]     | true
            [6,5,4,3,2,1,1,  0,0,0,0,0,0,8]     | true
            
    }
}
