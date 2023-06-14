package server.control;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import server.data.Game;
import server.data.Gamestate;
import server.data.PlayerInformation;
import server.data.map.HalfMapNode;
import server.data.map.MapNode;
import server.enums.FortState;
import server.enums.PlayerPositionState;
import server.enums.Terrain;
import server.enums.TreasureState;

public class GameStateControl {
	private static final int OFFSET_X_VALUE = 10;
	private static final int OFFSET_Y_VALUE = 5;
	
	public static Gamestate makeGameState(String oldGameStateId, Game game, String playerId) {
		String gameStateId = UUID.randomUUID().toString();
		if(oldGameStateId != "") {
			gameStateId = oldGameStateId;
		}
		game.setGameStateId(gameStateId);

		Collection<PlayerInformation> playerInfos = new HashSet<PlayerInformation>();		
		PlayerInformation playerInfo = game.getInformationByPlayerId(playerId);
		playerInfos.add(playerInfo);
		
		if(!game.playersFull()) {
			return new Gamestate(playerInfos, gameStateId, playerInfo.getPlayerid());
		}

		PlayerInformation enemyInfo = game.getEnemyInformationByPlayerId(playerId);
		playerInfos.add(enemyInfo);
		
		if(!enemyInfo.isSentMap() || !playerInfo.isSentMap()) {
			return new Gamestate(playerInfos, gameStateId, playerInfo.getPlayerid());
		}
		
		Collection<MapNode> map = makeMap(game, playerInfo, enemyInfo);
		return new Gamestate(map, playerInfos, gameStateId, playerInfo.getPlayerid());		
	}	

	
	private static Collection<MapNode> makeMap(Game game, PlayerInformation playerInfo, PlayerInformation enemyInfo){
		Collection<MapNode> mapNodes= new HashSet<>();
		
		// set condition variables
		boolean playerMapFirst = false; // is true if firstplayermap true
		Collection<HalfMapNode> firstMap;
		Collection<HalfMapNode> secondMap;
		int offsetX = 0;
		int offsetY = 0;
		
		if(game.isSquare()) {
			offsetY = OFFSET_Y_VALUE;
		}
		else {
			offsetX = OFFSET_X_VALUE;
		}
		
		if(game.isFirstPlayerMap()) {
			playerMapFirst = true;
		}
		
		// assign first and second map to player and enemy
		if(playerMapFirst) {
			 firstMap = playerInfo.getMapNodes();
			 secondMap = enemyInfo.getMapNodes();
		}
		else {
			 firstMap = enemyInfo.getMapNodes();
			 secondMap = playerInfo.getMapNodes();
		}

		// first map
		boolean randomEnemyPositionSet = false;
		for(HalfMapNode halfMapNode : firstMap) {
			if(halfMapNode.isFortPresent() && playerMapFirst) {
				mapNodes.add(new MapNode(halfMapNode.getTerrain(), PlayerPositionState.MyPlayerPosition, 
						TreasureState.NoOrUnknownTreasureState, FortState.MyFortPresent, halfMapNode.getX(), halfMapNode.getY()));
			}
			else {
				if(!randomEnemyPositionSet && !playerMapFirst && halfMapNode.getTerrain() == Terrain.Grass) {
					randomEnemyPositionSet = true;
					mapNodes.add(new MapNode(halfMapNode.getTerrain(), PlayerPositionState.EnemyPlayerPosition, 
							TreasureState.NoOrUnknownTreasureState, FortState.NoOrUnknownFortState, halfMapNode.getX(), halfMapNode.getY()));
				}
				else{
					mapNodes.add(new MapNode(halfMapNode.getTerrain(), PlayerPositionState.NoPlayerPresent, 
							TreasureState.NoOrUnknownTreasureState, FortState.NoOrUnknownFortState, halfMapNode.getX(), halfMapNode.getY()));
				}
			}
		}
		// second map
		for(HalfMapNode halfMapNode : secondMap) {
			if(halfMapNode.isFortPresent() && !playerMapFirst) {
				mapNodes.add(new MapNode(halfMapNode.getTerrain(), PlayerPositionState.MyPlayerPosition, 
						TreasureState.NoOrUnknownTreasureState, FortState.MyFortPresent, halfMapNode.getX()+offsetX, halfMapNode.getY()+offsetY));
			}
			else {
				if(!randomEnemyPositionSet && playerMapFirst && halfMapNode.getTerrain() == Terrain.Grass) {
					randomEnemyPositionSet = true;
					mapNodes.add(new MapNode(halfMapNode.getTerrain(), PlayerPositionState.EnemyPlayerPosition, 
							TreasureState.NoOrUnknownTreasureState, FortState.NoOrUnknownFortState, halfMapNode.getX()+offsetX, halfMapNode.getY()+offsetY));
				}
				else{
					mapNodes.add(new MapNode(halfMapNode.getTerrain(), PlayerPositionState.NoPlayerPresent, 
							TreasureState.NoOrUnknownTreasureState, FortState.NoOrUnknownFortState, halfMapNode.getX()+offsetX, halfMapNode.getY()+offsetY));
				}
			}
		}
		return mapNodes;
	}
}