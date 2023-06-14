package server.control;

import java.util.Collection;
import java.util.UUID;

import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import server.data.Game;
import server.data.Gamestate;
import server.data.PlayerInformation;
import server.data.map.HalfMapNode;
import server.enums.PlayerGameState;

public class Control {

	public Control(){}
	
	// NEW GAME
	public UniqueGameIdentifier getNewGame() {
		Game newGame = new Game();
		GameControl.addGame(newGame);
		return DataConverter.convertToUniqueGameIdentifier(newGame.getGameId());
	}
	
	// REGISTER PLAYER
	public UniquePlayerIdentifier postRegisterPlayer(String gameID, PlayerRegistration playerRegistration) {
		Game game = GameControl.getGameById(gameID);
		UniquePlayerIdentifier newPlayerID = new UniquePlayerIdentifier(UUID.randomUUID().toString());
		PlayerInformation newPlayer = new PlayerInformation(playerRegistration.getStudentFirstName(), playerRegistration.getStudentLastName(), 
				playerRegistration.getStudentUAccount(), PlayerGameState.MustWait , newPlayerID.getUniquePlayerID(), false);
		game.addPlayer(newPlayer);
		return newPlayerID;
	}

	// RECEIVE HALFMAP
	public void postReceiveHalfMap(String gameID, PlayerHalfMap playerHalfMap) {
		Game game = GameControl.getGameById(gameID);
		Collection<HalfMapNode> mapNodes = DataConverter.convertToHalfMap(playerHalfMap);
		game.addPlayerHalfMap(playerHalfMap.getUniquePlayerID(), mapNodes);
		game.endPlayersTurn(playerHalfMap.getUniquePlayerID());
	}

	// RETURN GAMESTATE
	public GameState getReturnGameState(String gameID, String playerID) {
		Game game = GameControl.getGameById(gameID);
		Gamestate gamestate = GameStateControl.makeGameState(game.getGameStateId(), game, playerID);
		GameState newGameState = DataConverter.convertToGameState(gamestate);
		return newGameState;
	}

	// SET LOSER
	public void setLoser(String gameID, String playerId) {
		Game game = GameControl.getGameById(gameID);
		game.setLoser(playerId);
	}
}