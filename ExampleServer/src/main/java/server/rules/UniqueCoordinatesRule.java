package server.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidMapException;

public class UniqueCoordinatesRule implements IRule {

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkUniqueCoordinates(playerHalfMap.getMapNodes());
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		// TODO Auto-generated method stub
	}
	
	private void checkUniqueCoordinates(Collection<PlayerHalfMapNode> mapNodes) {
		Set<String> nodeCoordinates = new HashSet<>();
		for (Iterator<PlayerHalfMapNode> iterator = mapNodes.iterator(); iterator.hasNext();) {
			PlayerHalfMapNode mapNode = (PlayerHalfMapNode) iterator.next();
			String coordinates = mapNode.getX() + "," + mapNode.getY();
		    if (nodeCoordinates.contains(coordinates)) {
		        throw new InvalidMapException("Duplicate map node coordinates found");
		    }
		    nodeCoordinates.add(coordinates);
		}
	}
}