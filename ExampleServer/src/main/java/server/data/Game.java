package server.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.control.GameControl;
import server.data.map.HalfMapNode;
import server.enums.PlayerGameState;
import server.exceptions.ElementNotFoundException;

public class Game {
	private static final int GAME_ID_LENGTH = 5;
	private static final int PLAYER_LIMIT = 2;
	private static final int INDEX_0 = 0;
	private static final int INDEX_1 = 1;
	private String gameId;
	private boolean square;
	private boolean firstPlayerMap;	
	private String gameStateId;
	private List<PlayerInformation> players = new ArrayList<>(PLAYER_LIMIT);
	private Random random = new Random();
	private static final Logger logger = LoggerFactory.getLogger(Game.class);

	// CONSTRUCTOR
	public Game() {
		this.gameId = createNewGameId();
		this.square = random.nextBoolean();
		this.firstPlayerMap = random.nextBoolean();
		this.gameStateId = "";
		
		logger.info(String.format("New game was created with game ID: %s", gameId));
	}
	
	// GETTERS
	public String getGameId() {
		return gameId;
	}	

	public boolean isSquare() {
		return square;
	}
	
	public boolean isFirstPlayerMap() {
		return firstPlayerMap;
	}
	
	public String getGameStateId() {
		return gameStateId;
	}
	
	// SETTERS
	public void setGameStateId(String gameStateId) {
		this.gameStateId = gameStateId;
	}
	
	// NEW GAME
	private String createNewGameId() {
		String gameId = createRandomGameId();
		while(GameControl.isGameExists(gameId)) {
			gameId = createRandomGameId();
		}
		return gameId;
	}	
	
	private String createRandomGameId() {
		String gameIdSymbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder gameIdBuilder = new StringBuilder();
		while (gameIdBuilder.length() < GAME_ID_LENGTH) {
			int index = random.nextInt(gameIdSymbols.length());
			char randomChar = gameIdSymbols.charAt(index);
			gameIdBuilder.append(randomChar);
		}
		String gameId = gameIdBuilder.toString();
		return gameId;
	}
	
	// PLAYERS
	public void addPlayer(PlayerInformation playerId) {
		players.add(playerId);
		logger.info(String.format("Game: %s New player was added: %s", gameId, playerId));
		if(players.size() == PLAYER_LIMIT) {
			chooseStartingPlayer();
	    }
	}

	public boolean playersFull() {
		return players.size() >= PLAYER_LIMIT;
	}
		
	public boolean playerBelongsToGame(String playerId){
		for(PlayerInformation player : players) {
			if(player.getPlayerid().equals(playerId)) {
				return true;
			}
		}
		return false;
	}
	
	// MAPS
	public void addPlayerHalfMap(String playerId, Collection<HalfMapNode> halfMap) {
		if(playersFull()) {
			boolean playerSentMap = getInformationByPlayerId(playerId).isSentMap();
			boolean enemySentMap = getEnemyInformationByPlayerId(playerId).isSentMap();
			
			if(!(playerSentMap && enemySentMap)) {
				this.gameStateId = "";
				getInformationByPlayerId(playerId).addMapNodes(halfMap);
				logger.info(String.format("Game: %s HalfMap was added by player: %s", gameId, playerId));
			}
		}
	}

	// PLAYERSTATES
	private void chooseStartingPlayer() {
		Random random = new Random();
		String player1 = players.get(INDEX_0).getPlayerid();
		String player2 = players.get(INDEX_1).getPlayerid();
		
		if(random.nextBoolean()) {
			endPlayersTurn(player1);
			logger.info(String.format("Game: %s Starting player is: %s", gameId, player2));
		}
        else{
			endPlayersTurn(player2);
			logger.info(String.format("Game: %s Starting player is: %s", gameId, player1));
        }
	}
	
	public void endPlayersTurn(String playerId) {
		getInformationByPlayerId(playerId).setState(PlayerGameState.MustWait);
		getEnemyInformationByPlayerId(playerId).setState(PlayerGameState.MustAct);
	}

	public void setLoser(String loser) {
		getInformationByPlayerId(loser).setState(PlayerGameState.Lost);
		getEnemyInformationByPlayerId(loser).setState(PlayerGameState.Won);
	}		
	
	// PLAYERINFORMATIONS
	public PlayerInformation getInformationByPlayerId(String playerId) {
		for(PlayerInformation playerInfo : players) {
			if(playerInfo.getPlayerid().equals(playerId)) {
				return playerInfo;
			}
		}
	    throw new ElementNotFoundException("No player found");
	}	

	public PlayerInformation getEnemyInformationByPlayerId(String playerId) {
		for(PlayerInformation playerInfo : players) {
			if(!playerInfo.getPlayerid().equals(playerId)) {
				return playerInfo;
			}
		}
	    throw new ElementNotFoundException("No enemy found");
	}
}
