package server.rules;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidMapException;

public class CorrectMapNodeCountRule implements IRule {
	private static final int HALFMAP_SIZE = 50;

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkCorrectMapNodeCount(playerHalfMap);
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		// TODO Auto-generated method stub
	}

	private void checkCorrectMapNodeCount(PlayerHalfMap playerHalfMap) {
		if(playerHalfMap.getMapNodes().size() != HALFMAP_SIZE) {
			throw new InvalidMapException(String.format("MapNode count is not %d", HALFMAP_SIZE));
		}
	}
}