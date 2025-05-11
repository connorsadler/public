package cfs.kalah.controller;

import cfs.kalah.api.CreateGameResponseDTO;
import cfs.kalah.api.GameDTO;
import cfs.kalah.service.GameService;
import cfs.kalah.service.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/games")
public class GamesController {
	private static final Logger LOG = LoggerFactory.getLogger(GamesController.class);
	
	@Autowired
	GameService gameService;

	/**
	 * createGame
	 */
	@RequestMapping(method = POST)
	public ResponseEntity<?> createGame() {
		LOG.debug("createGame");

		UUID gameId = gameService.createGame();
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(gameId).toUri();
		CreateGameResponseDTO createGameResponseDTO = new CreateGameResponseDTO();
		createGameResponseDTO.setId(gameId.toString());
		createGameResponseDTO.setUri(location.toString());

		return ResponseEntity.created(location).body(createGameResponseDTO);
	}

	/**
	 * makeMove
	 */
	@RequestMapping(method = PUT, path = "/{gameId}/pits/{pitId}")
	public ResponseEntity<?> makeMove(@PathVariable("gameId") UUID gameId, @PathVariable("pitId") int pitId) {

		// cfstodo: Validate pitId - basic initial validation
		
		GameState gameState = gameService.makeMove(gameId, pitId);
		
		// Create response
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.replacePath("/games/{id}")
				.buildAndExpand(gameId).toUri();
		GameDTO gameDTO = new GameDTO();
		gameDTO.setId(gameId.toString());
		gameDTO.setUrl(location.toString());
		Map<String, String> statusMap = new LinkedHashMap<>();		// LinkedHashMap is sorted on insertion order, which is what we want
		for (int i=0; i < gameState.getPitsSize(); i++) {
			int pitNum = i+1;
			int pitContents = gameState.getPitContents(pitNum);
			statusMap.put(String.valueOf(pitNum), String.valueOf(pitContents));
		}
		gameDTO.setStatus(statusMap);
		
		return ResponseEntity.ok(gameDTO);
	}

	/**
	 * Convert our RuntimeExceptions into reasonable error response JSON 
	 */
	@ResponseStatus(value= HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody ErrorInfo
	errorHandler(HttpServletRequest req, Exception ex) {
		return new ErrorInfo(req.getRequestURL().toString(), ex);
		
	}

}
