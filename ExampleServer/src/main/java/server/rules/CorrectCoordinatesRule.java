package server.rules;

import java.util.Collection;
import java.util.Iterator;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidMapException;

public class CorrectCoordinatesRule implements IRule {
	private static final int MIN_X_Y = 0;
	private static final int MAX_X = 9;
	private static final int MAX_Y = 4;

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkCorrectCoordinates(playerHalfMap.getMapNodes());
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		// TODO Auto-generated method stub
	}
	
	private void checkCorrectCoordinates(Collection<PlayerHalfMapNode> mapNodes) {
		for (Iterator<PlayerHalfMapNode> iterator = mapNodes.iterator(); iterator.hasNext();) {
			PlayerHalfMapNode mapNode = (PlayerHalfMapNode) iterator.next();
			if(mapNode.getX() < MIN_X_Y || mapNode.getX() > MAX_X || mapNode.getY() < MIN_X_Y || mapNode.getY() > MAX_Y ) {
				throw new InvalidMapException("Map node coordinates are invalid");
			}
		}
	}
}
