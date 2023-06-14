package server.rules;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.control.GameControl;
import server.exceptions.InvalidPlayerException;

public class ValidPlayerIDRule implements IRule {

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkValidPlayerToGame(gameId, playerHalfMap.getUniquePlayerID());
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		checkValidPlayerToGame(gameId, playerId.getUniquePlayerID());
	}
	
	private void checkValidPlayerToGame(String gameId, String playerId) {
		if(!GameControl.getGameById(gameId).playerBelongsToGame(playerId)){
			throw new InvalidPlayerException("Invalid player");
		}
	}
}
