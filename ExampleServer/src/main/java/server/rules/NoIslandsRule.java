package server.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidMapException;

public class NoIslandsRule implements IRule {
	private static final int DIRECTION = 1;
	private static final int MIN_X_Y = 0;
	private static final int MAX_X = 9;
	private static final int MAX_Y = 4;
	private static final int HALFMAP_SIZE = 50;

	@Override
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap) {
		checkNoIslands(playerHalfMap);
	}

	@Override
	public void validateState(String gameId, UniquePlayerIdentifier playerId) {
		// TODO Auto-generated method stub
	}
	
	public void checkNoIslands(PlayerHalfMap map) {
	    Set<PlayerHalfMapNode> visited = new HashSet<>();
	    boolean islandFound = false;
		for (Iterator<PlayerHalfMapNode> iterator = map.getMapNodes().iterator(); iterator.hasNext();) {
			PlayerHalfMapNode mapNode = (PlayerHalfMapNode) iterator.next();

	        if (!visited.contains(mapNode) && mapNode.getTerrain() != ETerrain.Water) {
	            if (dfs(map.getMapNodes(), visited, mapNode.getX(), mapNode.getY())) {
	                islandFound = true;
	                break;
	            }
		        break;
	        }
	    }
	    if(islandFound) {
			throw new InvalidMapException("Islands present");
	    }
	    if(visited.size() != HALFMAP_SIZE) {
			throw new InvalidMapException("Islands present");
	    }
	}

	private boolean dfs(Collection<PlayerHalfMapNode> map, Collection<PlayerHalfMapNode> visited, int x, int y) {
	    PlayerHalfMapNode mapNode = new PlayerHalfMapNode();
		for (Iterator<PlayerHalfMapNode> iterator = map.iterator(); iterator.hasNext();) {
			PlayerHalfMapNode playerHalfMapNode = (PlayerHalfMapNode) iterator.next();
			if(playerHalfMapNode.getX() == x && playerHalfMapNode.getY() == y ) {	
				mapNode = playerHalfMapNode;
			}
		}
		
		if (map.isEmpty() || visited.contains(mapNode)) {
	        return false; // Already visited or water node, not an island
	    }
		
        visited.add(mapNode);
		
		if (mapNode.getTerrain() == ETerrain.Water) {
			return false;
		}
		if(visited.size() == HALFMAP_SIZE) {
			return false;
		}

	    // Recursively explore adjacent nodes
	    boolean island = true;
	    if (x < MAX_X) {
	    	island &= dfs(map, visited, x + DIRECTION, y); // Down
	    }
	    if(x > MIN_X_Y) {
	    	island &= dfs(map, visited, x - DIRECTION, y); // Up
	    }
	    if(y < MAX_Y) {
	    	island &= dfs(map, visited, x, y + DIRECTION); // Right
	    }
	    if(y > MIN_X_Y) {
	    	island &= dfs(map, visited, x, y - DIRECTION); // Left
	    }
	    return island;
	}
}
