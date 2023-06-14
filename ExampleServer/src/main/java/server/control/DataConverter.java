package server.control;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import server.data.Gamestate;
import server.data.PlayerInformation;
import server.data.map.HalfMapNode;
import server.data.map.MapNode;
import server.enums.FortState;
import server.enums.PlayerGameState;
import server.enums.PlayerPositionState;
import server.enums.Terrain;
import server.enums.TreasureState;
import server.exceptions.InvalidObjectFromMessagebase;

public class DataConverter {
	private static final int PLAYER_LIMIT = 2;
	private static final int FULLMAP_COUNT = 100;


	// UNIQUE GAME ID
	public static UniqueGameIdentifier convertToUniqueGameIdentifier(String gameId) {
		return new UniqueGameIdentifier(gameId);
	}

	// PLAYERHALFMAP CONVERTERS
	public static Collection<HalfMapNode> convertToHalfMap(PlayerHalfMap playerHalfMap) {
		Collection<HalfMapNode> mapNodes= new HashSet<>();
		
		try {
			for(PlayerHalfMapNode halfMapNode : playerHalfMap.getMapNodes()) {
				mapNodes.add(new HalfMapNode(convertToTerrain(halfMapNode.getTerrain()), halfMapNode.isFortPresent(), halfMapNode.getX(), halfMapNode.getY()));
			}
		} catch (Exception e) {
            throw new InvalidObjectFromMessagebase("No matching playerHalfMap");
		}
		
		return mapNodes;
	}
	
	public static PlayerHalfMap convertToPlayerHalfMap(PlayerInformation playerInfo) {
		String playerId = playerInfo.getPlayerid();
		Collection<HalfMapNode> halfMap = playerInfo.getMapNodes();
		Collection<PlayerHalfMapNode> mapNodes= new HashSet<>();
		
		try {
			for(HalfMapNode halfMapNode : halfMap) {
				mapNodes.add(new PlayerHalfMapNode(halfMapNode.getX(), halfMapNode.getY(), halfMapNode.isFortPresent(), convertToETerrain(halfMapNode.getTerrain()) ));
			}
		} catch (Exception e) {
            throw new InvalidObjectFromMessagebase("No matching playerHalfMap");
		}
		
		return new PlayerHalfMap(playerId, mapNodes);
	}	
	
	// GAMESTATE CONVERTER
	public static GameState convertToGameState(Gamestate gamestate) {
		GameState newGameState = new GameState();
		Set<PlayerState> players = new HashSet<PlayerState>();
		PlayerState playerstate = convertToPlayerState(gamestate.getPlayer(), false);
		players.add(playerstate);
		
		if(gamestate.getPlayers().size() == PLAYER_LIMIT) {
			PlayerState enemystate = convertToPlayerState(gamestate.getEnemy(), true);
			players.add(enemystate);
		}
		
		if(gamestate.getFullMap().isEmpty() || gamestate.getFullMap().size() != FULLMAP_COUNT) {
			try{
				newGameState = new GameState(players, gamestate.getGameStateId());
				return newGameState;
			} catch (Exception e) {
		        throw new InvalidObjectFromMessagebase("No matching PlayerState");
			}
		}
		
		FullMap fullmap = convertToFullMap(gamestate.getFullMap());
		
		try{
			newGameState = new GameState(fullmap, players, gamestate.getGameStateId());
		} catch (Exception e) {
	        throw new InvalidObjectFromMessagebase("No matching PlayerState");
		}
		
		return newGameState;
	}
	
	// PLAYERSTATE CONVERTER
	public static PlayerState convertToPlayerState(PlayerInformation playerInfo, boolean enemy) {
		PlayerState playerstate = new PlayerState();
		if(!enemy) {
			try{
				playerstate = new PlayerState(playerInfo.getStudentFirstName(), playerInfo.getStudentLastName(), playerInfo.getStudentUAccount(), 
				convertToEPlayerGameState(playerInfo.getState()), UniquePlayerIdentifier.of(playerInfo.getPlayerid()), playerInfo.isCollectedTreasure());
			} catch (Exception e) {
		        throw new InvalidObjectFromMessagebase("No matching PlayerState");
			}
		}
		else {
			try{
				playerstate = new PlayerState(playerInfo.getStudentFirstName(), playerInfo.getStudentLastName(), playerInfo.getStudentUAccount(), 
					convertToEPlayerGameState(playerInfo.getState()), UniquePlayerIdentifier.of(UUID.randomUUID().toString()), playerInfo.isCollectedTreasure());
			} catch (Exception e) {
		        throw new InvalidObjectFromMessagebase("No matching PlayerState");
			}
		}
		return playerstate;
	}
	
	// FULLMAP CONVERTER
	public static FullMap convertToFullMap(Collection<MapNode> mapNodes) {
		Set<FullMapNode> fullMapNodes = new HashSet<>();
		try {
			for (Iterator<MapNode> iterator = mapNodes.iterator(); iterator.hasNext();) {
				MapNode mapNode = (MapNode) iterator.next();
				fullMapNodes.add(new FullMapNode(convertToETerrain(mapNode.getTerrain()), 
						convertToEPlayerPositionState(mapNode.getPlayerPositionState()), 
						convertToETreasureState(mapNode.getTreasureState()), 
						convertToEFortState(mapNode.getFortstate()), 
						mapNode.getX(), mapNode.getY()));
			}
		} catch (Exception e) {
	        throw new InvalidObjectFromMessagebase("No matching FullMap");
		}

		FullMap map = new FullMap(fullMapNodes);
		return map;
	}
	
	// ENUM CONVERTERS
	public static EPlayerGameState convertToEPlayerGameState(PlayerGameState playerGameState)  {        
        switch (playerGameState) {
            case Won:
                return EPlayerGameState.Won;
            case Lost:
                return EPlayerGameState.Lost;
            case MustAct:                
            	return EPlayerGameState.MustAct;
            default:
                return EPlayerGameState.MustWait;
        }
	}

	public static Terrain convertToTerrain(ETerrain eTerrain) {
        switch (eTerrain) {
            case Water:
            	return Terrain.Water;
            case Mountain:
            	return Terrain.Mountain;
            case Grass:
            	return Terrain.Grass;
            default:
                throw new InvalidObjectFromMessagebase("No matching ETerrain value");
        }
	}
	
	public static ETerrain convertToETerrain(Terrain Terrain) {
        switch (Terrain) {
            case Water:
            	return ETerrain.Water;
            case Mountain:
            	return ETerrain.Mountain;
            default:
            	return ETerrain.Grass;
        }
	}
	
	public static EPlayerPositionState convertToEPlayerPositionState(PlayerPositionState playerpositionstate) {
        switch (playerpositionstate) {
            case BothPlayerPosition:
            	return EPlayerPositionState.BothPlayerPosition;
            case EnemyPlayerPosition:
            	return EPlayerPositionState.EnemyPlayerPosition;
            case MyPlayerPosition:
            	return EPlayerPositionState.MyPlayerPosition;
            default:
            	return EPlayerPositionState.NoPlayerPresent;
        }
	}
	
	public static ETreasureState convertToETreasureState(TreasureState treasurestate) {
        switch (treasurestate) {
            case MyTreasureIsPresent:
            	return ETreasureState.MyTreasureIsPresent;
            default:
            	return ETreasureState.NoOrUnknownTreasureState;
        }
	}
	
	public static EFortState convertToEFortState(FortState fortstate) {
        switch (fortstate) {
            case EnemyFortPresent:
            	return EFortState.EnemyFortPresent;
            case MyFortPresent:
            	return EFortState.MyFortPresent;
            default:
            	return EFortState.NoOrUnknownFortState;
        }
	}
}
