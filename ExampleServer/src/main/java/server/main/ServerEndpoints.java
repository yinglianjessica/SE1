package server.main;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import messagesbase.ResponseEnvelope;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import server.control.Control;
import server.exceptions.GenericExampleException;
import server.exceptions.InvalidMapException;

import server.rules.CorrectCoordinatesRule;
import server.rules.CorrectMapNodeCountRule;
import server.rules.GameFullRule;
import server.rules.IRule;
import server.rules.NoIslandsRule;
import server.rules.OneFortIsPresentRule;
import server.rules.PlayerHasNotSendMapBeforeRule;
import server.rules.TerrainCountRule;
import server.rules.UniqueCoordinatesRule;
import server.rules.ValidGameIDRule;
import server.rules.ValidPlayerIDRule;
import server.rules.ValidPlayerTurnRule;

@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {
	private final List<IRule> rules = List.of(new ValidGameIDRule(), 
			new GameFullRule(), new ValidPlayerIDRule(), 
			new ValidPlayerTurnRule(), new PlayerHasNotSendMapBeforeRule(), 
			new CorrectCoordinatesRule(), new CorrectMapNodeCountRule(), 
			new NoIslandsRule(), new OneFortIsPresentRule(),
			new TerrainCountRule(), new UniqueCoordinatesRule());
	private static final Control control = new Control();
    private static final Logger logger = LoggerFactory.getLogger(ServerEndpoints.class);
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {

		UniqueGameIdentifier newGameId = control.getNewGame();
		return newGameId;
	}

	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {

		for (IRule rule : rules) {
			try {
				rule.validateRegistration(gameID.getUniqueGameID(), playerRegistration);
			} catch (GenericExampleException e) {
				logger.error("Business rule error thrown: ", e);
				throw e;
			}
		}
		
		UniquePlayerIdentifier newPlayerId = control.postRegisterPlayer(gameID.getUniqueGameID(), playerRegistration);
		return new ResponseEnvelope<>(newPlayerId);
	}
	
	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<Object> receiveHalfMap(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerHalfMap halfMap) {
		
		for (IRule rule : rules) {
			try {
				rule.validateMap(gameID.getUniqueGameID(), halfMap);
			} catch (InvalidMapException e) {
				control.setLoser(gameID.getUniqueGameID(), halfMap.getUniquePlayerID());
				logger.error("Invalid HalfMap, loser set: ", e);
				throw e;
			} catch (GenericExampleException e) {
				logger.error("Business rule error thrown: ", e);
				throw e;
			}
		}
		
		control.postReceiveHalfMap(gameID.getUniqueGameID(), halfMap);
		return new ResponseEnvelope<Object>();
	}
	
	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> returnGameState(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) {

		for (IRule rule : rules) {
			try {
				rule.validateState(gameID.getUniqueGameID(), playerID);
			} catch (GenericExampleException e) {
				logger.error("Business rule error thrown: ", e);
				throw e;
			}
		}
		
		GameState gamestate = control.getReturnGameState(gameID.getUniqueGameID(), playerID.getUniquePlayerID());
		return new ResponseEnvelope<>(gamestate);
	}
	
	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());
		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}