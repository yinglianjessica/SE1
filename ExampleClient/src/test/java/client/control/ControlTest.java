package client.control;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;

import client.exceptions.InvalidMapException;
import client.exceptions.ResponseEnvelopeException;
import client.map.HalfMap;
import client.map.MapGenerator;
import client.movement.Direction;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

class ControlTest {
	private static Control control;
	private static Network networkMock;
	private static HalfMap halfmap;
	private static GameState gamestate;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		gamestate = createDataForGamestate();
        networkMock = mock(Network.class);
        when(networkMock.requestGameState()).thenReturn(gamestate);
        when(networkMock.playerRegistration()).thenReturn("player");
        control = new Control("http://example.com", "mockGameId");
        control.setNetwork(networkMock);	        
        halfmap = new HalfMap(MapGenerator.generateFields());
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
	public void playTest_callRequestGameState_atLeastOnce(){
        control.play();
        verify(networkMock, atLeastOnce()).requestGameState();
    }

	@Test
	public void playerRegistrationTest_callPlayerRegistration_oneTime() {
        control.playerRegistration();
        verify(networkMock, times(1)).playerRegistration();
    }

	@Test
	public void saveMapTest_callRequestGameState_atLeastOnce() {
        control.saveMap();
        verify(networkMock, atLeastOnce()).requestGameState();
    }

	@Test
	public void sendMapTest_callRequestGameState_atLeastOnce() throws InvalidMapException {
        control.sendMap();
        verify(networkMock, atLeastOnce()).requestGameState();
    }
	
	@Test
	public void getClientGameStateTest_Lost_True() {
        assertEquals(EPlayerGameState.Lost.toString(), control.getClientGameState(gamestate, "player").toString());
    }

	@Test
	public void getClientGameStateTest_MustAct_False() {
        assertNotEquals(EPlayerGameState.MustWait.toString(), control.getClientGameState(gamestate, "player").toString());
	}

	
	@Test
	public void getNewPlayerHalfMapTest_netWorkMethodsCalled() {
        assertEquals(control.getNewPlayerHalfMap("player", halfmap).getUniquePlayerID(), "player");
    }

	@MethodSource("createDataForGamestate")
	public static GameState createDataForGamestate() {
		String gameStateId = "game";
		HashSet<PlayerState> players = new HashSet<>();
		UniquePlayerIdentifier id = new UniquePlayerIdentifier("player");
		UniquePlayerIdentifier id2 = new UniquePlayerIdentifier("enemy");
		PlayerState playerstate = new PlayerState("P", "Layer", "playeracc", EPlayerGameState.Lost, id, true);
		PlayerState playerstate2 = new PlayerState("E", "Nemy", "enemyacc", EPlayerGameState.Won, id2, true);
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
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.MyPlayerPosition,
							ETreasureState.MyTreasureIsPresent, EFortState.NoOrUnknownFortState, x, y));
				}
				else if(x == 8 && y == 8) {
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState, EFortState.EnemyFortPresent, x, y));
				}
				else if(x ==1 && y == 1) {
					nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.EnemyPlayerPosition,
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
