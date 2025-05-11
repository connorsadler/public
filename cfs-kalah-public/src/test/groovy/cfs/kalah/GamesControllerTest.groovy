package cfs.kalah

import cfs.kalah.controller.GamesController
import cfs.kalah.service.GameService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.ContentResultMatchers

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for Controller
 * Only the web layer is tested, the dependent service layer is mocked
 */
@WebMvcTest(controllers = GamesController.class)
public class GamesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    GameService gamesService;

    @Test
    public void 'create game - success'() {
        def gameId = TestData.GAME_ID
        
        when(gamesService.createGame()).thenReturn(gameId)
        
        mockMvc.perform(post("/games"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("\$.id").value(gameId.toString()))
                .andExpect(jsonPath("\$.uri").value("http://localhost/games/${gameId}".toString()))  // Note: Trailing toString() needed as otherwise it's a 'Groovy string' - small Groovy gotcha

        // Check createGame was called in service
        verify(gamesService).createGame()
    }

    
    @Test
    public void 'make move - success'() {
        def gameId = TestData.GAME_ID
        def pitId = 1

        when(gamesService.makeMove(gameId, pitId)).thenReturn(TestData.createGameState())
        
        // jsonPath assertion examples: https://github.com/spring-projects/spring-framework/blob/master/spring-test/src/test/java/org/springframework/test/web/servlet/samples/client/standalone/resultmatches/JsonPathAssertionTests.java
        // json assertion examples: https://www.codota.com/code/java/methods/org.springframework.test.web.servlet.result.ContentResultMatchers/json
        
        mockMvc.perform(put("/games/${gameId}/pits/${pitId}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("\$.id").value(gameId.toString()))
                .andExpect(jsonPath("\$.url").value("http://localhost/games/${gameId}".toString()))  // Note: Trailing toString() needed as otherwise it's a 'Groovy string' - small Groovy gotcha
                .andExpect(jsonPath("\$.status.length()").value("14"))
                //.andExpect(content().string("XXX"))  // For debugging
                .andExpect(content().json(""" {
                    "id": "${gameId}",
                    "url": "http://localhost/games/${gameId}",
                    "status": {"1":"6","2":"5","3":"4","4":"0","5":"0","6":"0","7":"0","8":"0","9":"0","10":"0","11":"0","12":"0","13":"0","14":"0"}
                }
                """, true))

        // Check service called
        verify(gamesService).makeMove(eq(gameId), eq(pitId))
    }

    // cfstodo: make move - gameId not found
    
    // cfstodo: make move - pitId invalid
    
    // cfstodo: make move - move invalid
}
