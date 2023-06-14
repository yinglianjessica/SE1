package client.control;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import client.map.ClientFullMap;
import client.map.Coordinates;
import client.map.MapNode;
import client.map.Terrain;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class DataConverter {
	
	public static boolean collectedTreasure(Collection<PlayerState> players, String playerId){
		boolean collectedTreasure = false;
		for(Iterator<PlayerState> iterator = players.iterator(); iterator.hasNext();) {
			 PlayerState eachPlayer = (PlayerState) iterator.next();
			 if(eachPlayer.getUniquePlayerID() == playerId) {
				 if(eachPlayer.hasCollectedTreasure()) {
					collectedTreasure = true;
				 }
			 }
		}
		return collectedTreasure;
	}
	
	public static MapNode getPlayerPosition(GameState gamestate) {
		MapNode playerPosition = null;
		for (Iterator<FullMapNode> iterator = gamestate.getMap().getMapNodes().iterator(); iterator.hasNext();) {
			 FullMapNode node = (FullMapNode) iterator.next();
			 if(node.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition || node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition ) {
				 playerPosition = new MapNode(node);
				 break;
			 }
		}
		return playerPosition;
	}
	
	public static MapNode getEnemyPosition(GameState gamestate) {
		MapNode enemyPosition = null;
		for (Iterator<FullMapNode> iterator = gamestate.getMap().getMapNodes().iterator(); iterator.hasNext();) {
			 FullMapNode node = (FullMapNode) iterator.next();
			 if(node.getPlayerPositionState() == EPlayerPositionState.EnemyPlayerPosition || node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition ) {
				 enemyPosition = new MapNode(node);
				 break;
			 }
		}
		return enemyPosition;
	}
	public static MapNode convertMapNode(FullMapNode node){
			MapNode newNode = new MapNode(new Coordinates(node.getX(), node.getY()), convertTerrain(node.getTerrain()));
			if(node.getFortState() == EFortState.EnemyFortPresent) {
				newNode.setEnemyFort();
			}
			if(node.getFortState() == EFortState.MyFortPresent) {
				newNode.setOwnFort();
			}
			if(node.getTreasureState() == ETreasureState.MyTreasureIsPresent) {
				newNode.setTreasurePresent();
			}	
			return newNode;
	}
	
	public static HashMap<Coordinates, MapNode> convertMapNodeCollection(Collection<FullMapNode> fullMapNodes){
		HashMap<Coordinates, MapNode> ret = new HashMap<>();
		for (Iterator<FullMapNode> iterator = fullMapNodes.iterator(); iterator.hasNext();) {
			FullMapNode node = (FullMapNode) iterator.next();
			MapNode newNode = convertMapNode(node);
			ret.put(newNode.getCoordinates(), newNode);
		}
		return ret;
	}

	public static Terrain convertTerrain(ETerrain terrain) {
		Terrain ret = null;
		if(terrain.toString() == Terrain.Grass.toString())
			ret = Terrain.Grass;
		else if(terrain.toString().equals(Terrain.Water.toString()))
			ret = Terrain.Water;
		else if(terrain.toString().equals(Terrain.Mountain.toString()))
			ret = Terrain.Mountain;
		else {
			throw new IllegalArgumentException("Invalid terrain.");
		}
		return ret;
	}
	
}
