package client.movement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

import client.control.Data;
import client.control.DataConverter;
import client.exceptions.InvalidMapException;
import client.exceptions.MovementException;
import client.map.ClientFullMap;
import client.map.Coordinates;
import client.map.MapNode;
import client.ui.CLI;
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

class MovementControlTest {
	private static MovementControl movement;
	private static GameState gamestate = createDataForGamestate();
	private static String playerId;
	private static Data data;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		playerId = "itsjessica";
		movement = new MovementControl(gamestate, playerId);
		ClientFullMap map = new ClientFullMap(gamestate.getMap());
		data = new Data(map);
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
	
	@Test
	public void getNextMoveTest_OnlyValidDirectionDown_assertEqualsTrue() throws Exception {
		assertEquals(EMove.Down, movement.getNextMove(gamestate, data));	
	}
  
	@Test
	public void directionToStringTest_EMoveToString_assertEqualsTrue() throws Exception {
		assertEquals(Direction.Down.toString(), EMove.Down.toString());
		assertEquals(Direction.Up.toString(), EMove.Up.toString());
		assertEquals(Direction.Right.toString(), EMove.Right.toString());
		assertEquals(Direction.Left.toString(), EMove.Left.toString());
	}
	

	@MethodSource("createDataForGamestate")
	public static GameState createDataForGamestate() {
		String gameStateId = "game";
		HashSet<PlayerState> players = new HashSet<>();
		UniquePlayerIdentifier id = new UniquePlayerIdentifier("player");
		UniquePlayerIdentifier id2 = new UniquePlayerIdentifier("enemy");
		PlayerState playerstate = new PlayerState("P", "Layer", "playeracc", EPlayerGameState.MustAct, id, true);
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

}

