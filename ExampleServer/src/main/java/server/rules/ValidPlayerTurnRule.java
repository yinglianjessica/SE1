package server.rules;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.control.GameControl;
import server.data.Game;
import server.enums.PlayerGameState;
import server.exceptions.InvalidTurnActionException;

public class ValidPlayerTurnRule implements IRule {

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkValidPlayerTurnRule(GameControl.getGameById(gameId), playerHalfMap.getUniquePlayerID());
	}
	
	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		// TODO Auto-generated method stub
	}
	
	private void checkValidPlayerTurnRule(Game game, String playerId) {
		if(!game.playerBelongsToGame(playerId)) {
			throw new InvalidTurnActionException(playerId + " not registered");
		}
		if(game.getInformationByPlayerId(playerId).isSentMap()) {
			throw new InvalidTurnActionException(playerId + " tried to send another map");
		}
		if(game.getInformationByPlayerId(playerId).getState() == PlayerGameState.MustWait) {
			throw new InvalidTurnActionException(playerId + " not at turn");
		}
	}
}