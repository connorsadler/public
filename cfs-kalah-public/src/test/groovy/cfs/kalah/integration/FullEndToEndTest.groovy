package cfs.kalah.integration

import cfs.kalah.controller.GamesController
import cfs.kalah.repository.GameRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles

/**
 * Full end to end test
 * 
 * Stands up the full backend
 * 
 * Makes http calls to it, simulating a real person with curl or similar http tool
 * Checks the results are correct
 * 
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FullEndToEndTest {

    @Autowired
    private GamesController controller
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private GameRepository gameRepository;

    @LocalServerPort
    private int port; // The random port that our backend is listening on

    // Note: Wiremock could be used if any external services were being communicated with

    @BeforeEach
    public void setUp() {

    }
    
    @Test
    public void 'full end to end test - happy path'() throws Exception {
        
        // Check games list is initially empty
        assert gameRepository.findAll().size() == 0

        // Create game
        def postResult = postCreateGame()
        assert postResult.statusCode == HttpStatus.CREATED
        assert postResult.headers["Location"] != null
        assert postResult.body.id != null
        assert postResult.body.uri.startsWith("http://localhost:${port}/games/")
        def gameId = postResult.body.id
        
        // Check game is there
        assert gameRepository.findAll().size() == 1
        def gameEntity = gameRepository.findById(UUID.fromString(gameId)).get()
        // assert gameEntity.pits == Arrays.asList(6,6,6,6,6,6, 0, 6,6,6,6,6,6, 0);   // Don't check the game pits - that can be done in the service test, or we can create a GET endpoint
        
        // TODO: Maybe check game is setup correctly?
        
        // Make a move
        def makeMoveResult = putMakeMove(gameId, 1)
        // Check game state after first move
        checkMakeMoveResultStatus(makeMoveResult, [ 0,7,7,7,7,7,[1],  6,6,6,6,6,6,[0] ] )
        
        // Further moves
        makeMoveAndCheck(gameId, 2, [1,0,8,8,8,8,[9], 6,6,6,6,0,6,[0]])  // player 1 uses their extra turn
        makeMoveAndCheck(gameId, 8, [1,0,8,8,8,8,[9], 0,7,7,7,1,7,[1]])
        makeMoveAndCheck(gameId, 9, [1,0,8,8,0,8,[9], 1,0,8,8,2,8,[11]])
        makeMoveAndCheck(gameId, 1, [0,0,8,8,0,8,[12], 1,0,8,8,0,8,[11]])
        makeMoveAndCheck(gameId, 8, [0,0,8,8,0,8,[12], 0,0,8,8,0,8,[12]])
        makeMoveAndCheck(gameId, 3, [1,1,1,10,1,9,[13], 0,0,8,8,0,8,[12]])
        makeMoveAndCheck(gameId, 10,[1,1,1,10,1,9,[13], 1,1,1,10,1,9,[13]])
        makeMoveAndCheck(gameId, 4, [2,2,2,1,3,11,[15], 1,1,1,10,1,9,[13]])
        makeMoveAndCheck(gameId, 4, [2,2,2,0,4,11,[15], 1,1,1,10,1,9,[13]])
        makeMoveAndCheck(gameId, 11, [2,2,2,0,4,11,[15], 2,2,2,1,3,11,[15]])
        makeMoveAndCheck(gameId, 11, [2,2,2,0,4,11,[15], 2,2,2,0,4,11,[15]])
        makeMoveAndCheck(gameId, 2, [2,0,3,0,4,11,[18], 2,2,0,0,4,11,[15]])
        makeMoveAndCheck(gameId, 8, [2,0,3,0,4,11,[18], 0,3,0,0,4,11,[16]])
        makeMoveAndCheck(gameId, 6, [4,2,5,1,5,1,[20], 0,3,0,0,4,11,[16]])
        makeMoveAndCheck(gameId, 13, [4,2,5,1,5,1,[20], 2,5,2,1,5,1,[18]])
        makeMoveAndCheck(gameId, 6, [4,2,5,1,5,0,[21], 2,5,2,1,5,1,[18]])
        makeMoveAndCheck(gameId, 4, [4,2,5,0,6,0,[21], 2,5,2,1,5,1,[18]])
        makeMoveAndCheck(gameId, 13, [4,2,5,0,6,0,[21], 2,5,2,1,5,0,[19]])
        makeMoveAndCheck(gameId, 11, [4,2,5,0,6,0,[21], 2,5,2,0,6,0,[19]])
        makeMoveAndCheck(gameId, 2, [4,0,6,0,6,0,[24], 2,5,0,0,6,0,[19]])
        makeMoveAndCheck(gameId, 8, [4,0,6,0,6,0,[24], 0,6,0,0,6,0,[20]])
        makeMoveAndCheck(gameId, 1, [0,1,7,1,7,0,[24], 0,6,0,0,6,0,[20]])
        makeMoveAndCheck(gameId, 9, [0,1,7,1,7,0,[24], 0,0,1,1,7,1,[22]])
        makeMoveAndCheck(gameId, 3, [1,2,0,2,8,1,[27], 0,0,1,0,7,1,[22]])
        makeMoveAndCheck(gameId, 13, [1,2,0,2,8,1,[27], 0,0,1,0,7,0,[23]])
        makeMoveAndCheck(gameId, 10, [1,2,0,2,8,1,[27], 0,0,0,0,7,0,[24]])
        makeMoveAndCheck(gameId, 6, [1,2,0,2,8,0,[28], 0,0,0,0,7,0,[24]])
        makeMoveAndCheck(gameId, 4, [1,2,0,0,9,0,[29], 0,0,0,0,7,0,[24]])
        makeMoveAndCheck(gameId, 12, [1,0,0,0,9,0,[29], 1,1,1,1,0,1,[28]])
        makeMoveAndCheck(gameId, 1, [0,0,0,0,9,0,[30], 1,1,1,1,0,1,[28]])
        makeMoveAndCheck(gameId, 13, [0,0,0,0,9,0,[30], 1,1,1,1,0,0,[29]])
        makeMoveAndCheck(gameId, 11, [0,0,0,0,9,0,[30], 1,1,1,0,0,0,[30]])

        // cfstodo: keep making moves and win the game - ran out of time here

        // cfstodo: Make an invalid move (e.g. wrong player, or game already ended) and check error
        
    }
    
    // Convenience method to check current game state after a move is made
    def checkMakeMoveResultStatus(makeMoveResult, expectedPitsContents) {
        //assert makeMoveResult.body.status == ['1':'0', '2':'7', '3':'7', '4':'7', '5':'7', '6':'7', '7':'1', '8':'6', '9':'6', '10':'6', '11':'6', '12':'6', '13':'6', '14':'0']
        def expectedMap = [:]
        expectedPitsContents.eachWithIndex { item, index ->
            if (item instanceof List) item = item[0]    // Nice tweak to allow you to specify your "home" pit contents as (say) [1] rather than 1, to distinguish it from the other pits
            expectedMap[String.valueOf(index+1)] = String.valueOf(item)
        }
        assert makeMoveResult.body.status == expectedMap
    }
    
    def makeMoveAndCheck(gameId, pitId, expectedPitsContents) {
        def makeMoveResult = putMakeMove(gameId, pitId)
        // Check game state
        checkMakeMoveResultStatus(makeMoveResult, expectedPitsContents )
    }

    def postCreateGame() {
        println "postCreateGame"
        HttpHeaders headers = new HttpHeaders()
        ResponseEntity postResult = restTemplate.postForEntity("http://localhost:${port}/games", null, Map, [:])
        println "postResult = $postResult"
        postResult
    }

    def putMakeMove(gameId, pitId) {
        println "putMakeMove"
        HttpHeaders headers = new HttpHeaders()
        //ResponseEntity result = restTemplate.put("http://localhost:${port}/games/${gameId}/pits/${pitId}", null)
        ResponseEntity result = restTemplate.exchange("http://localhost:${port}/games/${gameId}/pits/${pitId}", HttpMethod.PUT, new HttpEntity<>(), Map)
        println "result = $result"

        assert result.statusCode == HttpStatus.OK
        
        result
    }


}
