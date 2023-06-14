package client.control;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.exceptions.InvalidMapException;
import client.exceptions.MovementException;
import client.exceptions.ResponseEnvelopeException;
import client.map.ClientFullMap;
import client.map.Coordinates;
import client.map.HalfMap;
import client.map.MapGenerator;
import client.ui.CLI;
import client.map.MapNode;
import client.map.MapValidation;
import client.movement.MovementControl;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class Control {
	Logger logger = LoggerFactory.getLogger(Control.class);
    private Network network;
	private String playerId;
	
	private MovementControl movement;
	private boolean terminateGame;
    private CLI view;
    private static Data data;
    	
	public Control(String serverBaseUrl, String gameId) {
		this.network = new Network(serverBaseUrl, gameId);
		this.terminateGame = false;
	}
	
	public void setNetwork(Network network) {
		this.network = network;
	}

	public void playerRegistration() throws ResponseEnvelopeException {
		this.playerId = network.playerRegistration();
	}
	
	// extracting Gamestate of player
	public static EPlayerGameState getClientGameState(GameState gamestate, String playerId) throws InvalidParameterException {
		EPlayerGameState ret = null;
		if(gamestate == null || playerId == null) {
			throw new InvalidParameterException("gamestate or playerId is null in getClientGameState()");
		}
		Set<PlayerState> playerSet = gamestate.getPlayers();
		for (Iterator<PlayerState> iterator = playerSet.iterator(); iterator.hasNext();) {
			PlayerState playerState = (PlayerState) iterator.next();			
			if(playerState.getUniquePlayerID().equals(playerId)) {
				ret = playerState.getState();
			}
		}
		return ret;
	}
	
	public void sendMap() throws InvalidMapException {
		HalfMap halfMap = new HalfMap(MapGenerator.generateFields());
		if(MapValidation.validateMap(halfMap) == false) {
			logger.warn("Warning: Map is not valid, trying again");
			halfMap = new HalfMap(MapGenerator.generateFields());
		}
		GameState gamestate = null;
		boolean stoploop = false;
		try {
			while(!stoploop && !terminateGame) {
				gamestate = this.network.requestGameState();
				EPlayerGameState playerstate = getClientGameState(gamestate, playerId);
				if(!playerstate.equals(EPlayerGameState.MustWait)) {
					PlayerHalfMap playerHalfMap = new PlayerHalfMap();
					playerHalfMap = getNewPlayerHalfMap(playerId, halfMap);
					network.sendMap(playerHalfMap);
					stoploop = true;
				}
			}
		} catch (InvalidParameterException e) {
	        logger.error("Invalid Parameter: " + e.getMessage());
	    } catch (ResponseEnvelopeException e) {
	        logger.error("Error with network communication: " + e.getMessage());
	    } catch (Exception e) {
	        logger.error("Unexpected error: " + e.getMessage());
	    }
	}
	
	// get playerHalfMap from halfMap for sending it to the server
	public static PlayerHalfMap getNewPlayerHalfMap(String playerId, HalfMap halfMap) throws InvalidParameterException {
		if(playerId == null) {
			throw new InvalidParameterException("Error: null parameters in getNewPlayerHalfMap");
		}
		if(halfMap == null || !MapValidation.validateMap(halfMap)) {
			throw new InvalidParameterException("Error: incorrect HalfMap in getNewPlayerHalfMap");
		}
		Set<PlayerHalfMapNode> nodes = new HashSet<PlayerHalfMapNode>();
		for (Entry<Coordinates, MapNode> entry : halfMap.getFields().entrySet()) {
			int x = entry.getKey().getX();
			int y = entry.getKey().getY();
			boolean fortPresent = entry.getValue().isOwnFort();
			ETerrain terrain = ETerrain.valueOf(entry.getValue().getTerrain().toString());
			PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, fortPresent, terrain);
			nodes.add(node);
		}
		return new PlayerHalfMap(playerId, nodes);
	}
	
	public void saveMap() {
		boolean stop = false;
		GameState gamestate = null;
		try {
			do {
				gamestate = network.requestGameState();
				EPlayerGameState playerstate = getClientGameState(gamestate, playerId);
				if(playerstate.equals(EPlayerGameState.MustAct) && gamestate.getMap().getMapNodes().size() > 80) {
					logger.info("Received map and saving it");
					Control.data = new Data(new ClientFullMap(gamestate.getMap()));
					this.view = new CLI(data);
	
					this.setMovement(gamestate);		
					stop = true;
				}
				if(playerstate.equals(EPlayerGameState.Won)) {
					logger.info("Won game");
					terminateGame = true;
				}
				if(playerstate.equals(EPlayerGameState.Lost)) {
					logger.info("Lost game");
					terminateGame = true;
				}
			} while (!terminateGame && !stop);
		} catch(ResponseEnvelopeException e) {
	        logger.error("Error with network communication: " + e.getMessage());
		} catch (InvalidParameterException e) {
	        logger.error("Invalid Parameter: " + e.getMessage());
		} catch (Exception e) {
	        logger.error("Unexpected error: " + e.getMessage());
	    }
		
	}
	
	// set MovementControl for controlling the movements sent by Control
	public MovementControl setMovement(GameState gamestate) {
		if(gamestate.getMap().isEmpty()) 
			throw new InvalidParameterException("Map in gamestate is empty in setMovement()");
		try {
			this.movement = new MovementControl(gamestate, playerId);
		} catch (MovementException e) {
			logger.error("Error initializing a new movement instance: " + e.getMessage());
		} catch (InvalidMapException e) {
			logger.error("Error with map given: " + e.getMessage());
		}
		return this.movement;
	}
	
	public void play() throws ResponseEnvelopeException {
		logger.info("Start play()");
		
		GameState gamestate = null;
		try {
			while(!terminateGame) {
				
				gamestate = network.requestGameState();
				EPlayerGameState playerstate = getClientGameState(gamestate, playerId);
	
				if(playerstate.equals(EPlayerGameState.MustAct)) {
					logger.info("MustAct now");
					this.playerMove(gamestate);
				}
				if(playerstate.equals(EPlayerGameState.Won)) {
					data.changeGameState(EGameState.Won);
					logger.info("Game won");
					terminateGame = true;
				}
				if(playerstate.equals(EPlayerGameState.Lost)) {
					data.changeGameState(EGameState.Lost);
					logger.info("Game lost");
					terminateGame = true;
				}
			}
		} catch(ResponseEnvelopeException e) {
	        logger.error("Error with network communication: " + e.getMessage());
		} catch (InvalidParameterException e) {
	        logger.error("Invalid Parameter: " + e.getMessage());
		} catch (Exception e) {
	        logger.error("Unexpected error: " + e.getMessage());
	    }
	}

	public void playerMove(GameState gamestate) {
		PlayerMove playerMove = null; 
		try{
			playerMove = getMove(this, playerId, gamestate);
			network.sendPlayerMove(playerMove);
		} catch(MovementException e) {
	        logger.error("Error with movement control: " + e.getMessage());
		} catch (ResponseEnvelopeException e) {
	        logger.error("Error with network communication: " + e.getMessage());
		} catch (InvalidMapException e) {
	        logger.error("Error with map info: " + e.getMessage());
		}
	}
	
	public static PlayerMove getMove(Control con, String playerId, GameState gamestate) throws MovementException, InvalidMapException {
		EMove move = con.movement.getNextMove(gamestate, data);	
		return PlayerMove.of(playerId, move);	
	}
}
