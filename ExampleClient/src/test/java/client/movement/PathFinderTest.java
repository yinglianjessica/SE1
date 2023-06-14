package client.movement;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import client.control.DataConverter;
import client.exceptions.InvalidMapException;
import client.exceptions.MovementException;
import client.map.ClientFullMap;
import client.map.Coordinates;
import client.map.MapNode;
import client.map.Terrain;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

class PathFinderTest {

	private static ClientFullMap map;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		map = new ClientFullMap(createDataForFullMap());
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
	
	@ParameterizedTest
	@CsvSource({
	    "0, 5, Right",
	    "3, 9, Up",
	    "9, 5, Left",
	    "3, 0, Down",
	    "0, 7, Right",
	    "5, 0, Down"
	})
    void getValidDirectionTest_assertEqualsTrue(int x, int y,  String move) throws MovementException, InvalidMapException {
		MapNode start = map.getMapNodeFromCo(new Coordinates(x,y));		
        EMove actualMove = PathFinder.getValidDirection(start, map);
        assertEquals(move, actualMove.toString());
    }

	@Test
	void getDijkstraMoveTest_Down_assertEquals() throws InvalidMapException, MovementException {
		MapNode start = map.getMapNodeFromCo(new Coordinates(1,1));
		MapNode goal = map.getMapNodeFromCo(new Coordinates(1,2));
		assertEquals(EMove.Down, PathFinder.getDijkstraMove(start, goal, map));
		assertEquals(EMove.Down.toString(), Direction.Down.toString());

		MapNode start2 = map.getMapNodeFromCo(new Coordinates(1,1));
		MapNode goal2 = map.getMapNodeFromCo(new Coordinates(8,8));
		assertNotEquals(EMove.Down, PathFinder.getDijkstraMove(start2, goal2, map));
	}
	

	@MethodSource("createDataForFullMap")
	public static FullMap createDataForFullMap() {
		FullMap fullmap = new FullMap();
		HashSet<FullMapNode> nodes = new HashSet<>();
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				if(x == 3 && y == 3) 
					nodes.add(new FullMapNode(ETerrain.Mountain, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				else if(x == 4 && y == 9) 
					nodes.add(new FullMapNode(ETerrain.Water, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				else if(x == 2 && y == 9) 
					nodes.add(new FullMapNode(ETerrain.Water, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				else if(x == 9 && y == 6) 
					nodes.add(new FullMapNode(ETerrain.Water, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				else if(x == 9 && y == 4)
					nodes.add(new FullMapNode(ETerrain.Water, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
				else if(x == 0 && y == 0) 
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.BothPlayerPosition,
							ETreasureState.MyTreasureIsPresent, EFortState.NoOrUnknownFortState, x, y));
				else if(x == 8 && y == 8) 
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.EnemyFortPresent, x, y));
				else if(x ==1 && y == 1) 
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.MyFortPresent, x, y));
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
