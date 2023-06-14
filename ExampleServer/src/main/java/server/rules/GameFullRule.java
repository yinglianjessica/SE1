package server.rules;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.control.GameControl;
import server.exceptions.GameFullException;

public class GameFullRule implements IRule {

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		checkGameNotFull(gameId);		
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkGameFull(gameId);
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		// TODO Auto-generated method stub
	}
	
	private void checkGameFull(String gameId) {
		if(!GameControl.isGameExists(gameId) || !GameControl.getGameById(gameId).playersFull()) {
			throw new GameFullException(gameId + " does not exist or is not full");
		}
	}
	
	private void checkGameNotFull(String gameId) {
		if(GameControl.isGameExists(gameId) && GameControl.getGameById(gameId).playersFull()) {
			throw new GameFullException(gameId + " is full and cannot register another player");
		}
	}
}
