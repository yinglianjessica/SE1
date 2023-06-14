package server.rules;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.control.GameControl;
import server.exceptions.InvalidMapException;

public class PlayerHasNotSendMapBeforeRule implements IRule {

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkPlayerHasNotSendMapBeforeRule(gameId, playerHalfMap);
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		// TODO Auto-generated method stub
	}
	
	private void checkPlayerHasNotSendMapBeforeRule(String gameId, PlayerHalfMap playerHalfMap) {
		if(GameControl.getGameById(gameId).playerBelongsToGame(playerHalfMap.getUniquePlayerID())) {
			if(GameControl.getGameById(gameId).getInformationByPlayerId(playerHalfMap.getUniquePlayerID()).isSentMap()){
				throw new InvalidMapException("Player tried to send another map");
			}
		}
	}
}
