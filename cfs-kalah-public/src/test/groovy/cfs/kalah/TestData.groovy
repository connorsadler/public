package cfs.kalah

import cfs.kalah.service.GameState

class TestData {

    static UUID GAME_ID = UUID.fromString('00000000-0000-0000-0000-000000000123')

    /**
     * A known sample GameState used for testing
     */
    static GameState createGameState() {
        def result = new GameState()
        result.setPitContents(1, 6)
        result.setPitContents(2, 5)
        result.setPitContents(3, 4)
        result
    }
}
