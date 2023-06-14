package server.rules;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidMapException;

public class OneFortIsPresentRule implements IRule {
	private static final int DESIRED_FORT_COUNT = 1;

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkOneFortIsPresent(playerHalfMap);
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		// TODO Auto-generated method stub
	}

	private void checkOneFortIsPresent(PlayerHalfMap playerHalfMap) {
		int fortCount = 0;
		for(PlayerHalfMapNode node : playerHalfMap.getMapNodes()) {
			if(node.isFortPresent()) {
				fortCount++;
			}
		}
		if(fortCount != DESIRED_FORT_COUNT) {
			throw new InvalidMapException(String.format("Fort count is not %d", DESIRED_FORT_COUNT));
		}
	}
}
