package server.rules;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidMapException;

public class TerrainCountRule implements IRule {
	private static final int MIN_GRASS_COUNT = 24;
	private static final int MIN_WATER_COUNT = 7;
	private static final int MIN_MOUNTAIN_COUNT = 5;

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkTerrainCount(playerHalfMap);
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		// TODO Auto-generated method stub
	}

	private void checkTerrainCount(PlayerHalfMap playerHalfMap) {
		int grassCount = 0;
		int waterCount = 0;
		int mountainCount = 0;
		for(PlayerHalfMapNode node : playerHalfMap.getMapNodes()) {
			if(node.getTerrain() == ETerrain.Grass) {
				grassCount++;
			}
			else if(node.getTerrain() == ETerrain.Water) {
				waterCount++;
			}
			else if(node.getTerrain() == ETerrain.Mountain) {
				mountainCount++;
			}
		}
		
		if(grassCount < MIN_GRASS_COUNT || mountainCount < MIN_MOUNTAIN_COUNT || waterCount < MIN_WATER_COUNT) {
			throw new InvalidMapException("Terrain count is not correct");
		}
	}
}