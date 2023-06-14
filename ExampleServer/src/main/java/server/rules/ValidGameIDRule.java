package server.rules;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.control.GameControl;
import server.exceptions.InvalidGameIDException;

public class ValidGameIDRule implements IRule {

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		checkValidGameID(gameId);
	}
	
	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkValidGameID(gameId);		
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		checkValidGameID(gameId);		
	}
	
	private void checkValidGameID(String gameId){
		if(!GameControl.isGameExists(gameId)) {
			throw new InvalidGameIDException(gameId + " does not exist");
		}
	}
}
