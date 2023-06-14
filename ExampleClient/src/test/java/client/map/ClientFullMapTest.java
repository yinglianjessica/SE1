package client.map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;

import client.control.Control;
import client.control.Data;
import client.control.DataConverter;
import client.control.EGameState;
import client.control.Network;
import client.exceptions.InvalidMapException;
import client.exceptions.MovementException;
import client.exceptions.ResponseEnvelopeException;
import client.movement.Direction;
import client.movement.MovementControl;
import client.movement.PathFinder;
import client.ui.CLI;
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

class ClientFullMapTest {
	private static GameState gamestate;
	private static String playerId;
	private static ClientFullMap map;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		gamestate = createDataForGamestate();
		playerId = "player";
		map = new ClientFullMap(gamestate.getMap());
	}	


	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	// data test
	@Test
	void removeCurrentPosFromUnknownFortTest_CurrentPos_SizeChanges() throws InvalidMapException {
		// removing node that is in fortfields
		int fortsizeBefore = map.getFortFields().size();
		map.removeCurrentPosFromUnknownFort(new Coordinates(0,5));
		int fortsizeAfter = map.getFortFields().size();
		assertNotEquals(fortsizeBefore, fortsizeAfter);

		// removing the same node wont change the size
		map.removeCurrentPosFromUnknownFort(new Coordinates(0,5));
		assertEquals(fortsizeAfter, map.getFortFields().size());
		
		//removing a node not in fortfield wont change the size either
		map.removeCurrentPosFromUnknownFort(new Coordinates(0,3));
		assertEquals(fortsizeAfter, map.getFortFields().size());
	}

	@Test
	void removeCurrentPosFromUnknownTreasureTest_CurrentPos_SizeChanges() throws InvalidMapException {
		int treasuresizeBefore = map.getTreasureFields().size();
		map.removeCurrentPosFromUnknownTreasure(new Coordinates(0,3));
		int treasuresizeAfter = map.getTreasureFields().size();
		assertNotEquals(treasuresizeBefore, treasuresizeAfter);

		map.removeCurrentPosFromUnknownTreasure(new Coordinates(0,3));
		assertEquals(treasuresizeAfter, map.getTreasureFields().size());
		
		map.removeCurrentPosFromUnknownTreasure(new Coordinates(0,5));
		assertEquals(treasuresizeAfter, map.getTreasureFields().size());
	}
	
	@Test
	void updateFortNodesTest_MountainPos_SizeChanges() throws InvalidMapException {
		MapNode mountain = new MapNode(new Coordinates(3,6), Terrain.Mountain);
		int fortsizeBefore = map.getFortFields().size();
		map.updateFortNodes(gamestate, mountain);
		int fortsizeAfter = map.getFortFields().size();
		
		assertNotEquals(fortsizeBefore, fortsizeAfter);
		
		map.updateFortNodes(gamestate, mountain);		
		assertEquals(map.getFortFields().size(), fortsizeAfter);

	}
	
	@Test
	void updateTreasureNodesTest_MountainPos_SizeChanges() throws InvalidMapException {
		MapNode mountain = new MapNode(new Coordinates(3,3), Terrain.Mountain);
		int treasuresizeBefore = map.getTreasureFields().size();
		map.updateTreasurenodes(gamestate, mountain);
		int treasuresizeAfter = map.getTreasureFields().size();
		
		assertNotEquals(treasuresizeBefore, treasuresizeAfter);
		
		map.updateTreasurenodes(gamestate, mountain);		
		assertEquals(map.getTreasureFields().size(), treasuresizeAfter);

	}
	
	// negative
	@Test
	void clientFullMapConstructor_FullMapWithoutFort_throwsInvalidMapException() {
		// exception thrown if fort is missing
		assertThrows(InvalidMapException.class, () -> {
			ClientFullMap clientfullmap = new ClientFullMap(createDataForFullMap());
	        throw new InvalidMapException();
	    });
	}


	@MethodSource("createDataForGamestate")
	public static GameState createDataForGamestate() {
		String gameStateId = "game";
		HashSet<PlayerState> players = new HashSet<>();
		UniquePlayerIdentifier id = new UniquePlayerIdentifier("player");
		UniquePlayerIdentifier id2 = new UniquePlayerIdentifier("enemy");
		PlayerState playerstate = new PlayerState("P", "Layer", "playeracc", EPlayerGameState.Lost, id, true);
		PlayerState playerstate2 = new PlayerState("E", "Nemy", "enemyacc", EPlayerGameState.MustWait, id2, true);
		players.add(playerstate);
		players.add(playerstate2);
		FullMap fullmap = new FullMap();
		HashSet<FullMapNode> nodes = new HashSet<>();
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				if(x == 3 && y == 3) {
					nodes.add(new FullMapNode(ETerrain.Mountain, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				}
				else if(x == 0 && y == 0) {
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.BothPlayerPosition,
							ETreasureState.MyTreasureIsPresent, EFortState.NoOrUnknownFortState, x, y));
				}
				else if(x == 8 && y == 8) {
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.EnemyFortPresent, x, y));
				}
				else if(x ==1 && y == 1) {
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.MyFortPresent, x, y));
				}
				else if(y%2 == 1)
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
						ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				else if(x%2 == 1)
					nodes.add(new FullMapNode(ETerrain.Mountain, EPlayerPositionState.NoPlayerPresent,
						ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				else
					nodes.add(new FullMapNode(ETerrain.Water, EPlayerPositionState.NoPlayerPresent,
						ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
			}
		}
		fullmap = new FullMap(nodes);		
		GameState gamestate = new GameState(fullmap, players, gameStateId);
		return gamestate;
	}


	@MethodSource("createDataForFullMap")
	public static FullMap createDataForFullMap() {
		FullMap fullmap = new FullMap();
		HashSet<FullMapNode> nodes = new HashSet<>();
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				if(x == 3 && y == 3) {
					nodes.add(new FullMapNode(ETerrain.Mountain, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				}
				else if(x == 0 && y == 0) {
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.BothPlayerPosition,
							ETreasureState.MyTreasureIsPresent, EFortState.NoOrUnknownFortState, x, y));
				}
				else if(x == 8 && y == 8) {
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.EnemyFortPresent, x, y));
				}
				else if(x ==1 && y == 1) {
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				}
				else if(y%2 == 1)
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
						ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				else if(x%2 == 1)
					nodes.add(new FullMapNode(ETerrain.Mountain, EPlayerPositionState.NoPlayerPresent,
						ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				else
					nodes.add(new FullMapNode(ETerrain.Water, EPlayerPositionState.NoPlayerPresent,
						ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
			}
		}
		fullmap = new FullMap(nodes);		

		return fullmap;
	}

	

}
