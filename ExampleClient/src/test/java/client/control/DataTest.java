package client.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import client.map.ClientFullMap;
import client.map.ClientFullMap.CoordinatesComparator;
import client.ui.CLI;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMapNode;

class DataTest {
	private static ClientFullMap mapInfo;
	private static Data data;
	private static CLI cli;
	private static TreeSet<FullMapNode> nodeSet;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		TreeSet<FullMapNode> nodeSet = new TreeSet<FullMapNode>(new CoordinatesComparator());
		nodeSet.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.BothPlayerPosition,
			                    ETreasureState.MyTreasureIsPresent, EFortState.NoOrUnknownFortState, 1, 2));

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

	// tests with mock
	@Test
	void changeGameStateTest_Lost_True() {
		mapInfo = mock(ClientFullMap.class);
		when(mapInfo.getOrderedForOutput()).thenReturn(nodeSet);
		data = new Data(mapInfo);
		cli = new CLI(data);
		data.changeGameState(EGameState.Lost);
		assertEquals(EGameState.Lost, data.getGamestate());
	}
	
	@Test
	void changeGameStateTest_Won_True() {
		mapInfo = mock(ClientFullMap.class);
		when(mapInfo.getOrderedForOutput()).thenReturn(nodeSet);
		data = new Data(mapInfo);
		cli = new CLI(data);
		data.changeGameState(EGameState.Won);
		assertEquals(EGameState.Won, data.getGamestate());
	}
	
	@Test
	void changeGameStateTest_MustAct_True() {
		mapInfo = mock(ClientFullMap.class);
		when(mapInfo.getOrderedForOutput()).thenReturn(nodeSet);
		data = new Data(mapInfo);
		cli = new CLI(data);
		data.changeGameState(EGameState.MustAct);
		assertEquals(EGameState.MustAct, data.getGamestate());
	}
	
	@Test
	void changeGameStateTest_MustWait_True() {
		mapInfo = mock(ClientFullMap.class);
		when(mapInfo.getOrderedForOutput()).thenReturn(nodeSet);
		data = new Data(mapInfo);
		cli = new CLI(data);
		data.changeGameState(EGameState.MustWait);
		assertEquals(EGameState.MustWait, data.getGamestate());
	}

	
	@Test
	void setTreasureCollectedTest_True_True() {
		mapInfo = mock(ClientFullMap.class);
		when(mapInfo.getOrderedForOutput()).thenReturn(nodeSet);
		data = new Data(mapInfo);
		cli = new CLI(data);
		assertEquals(false, data.isCollectedTreasure());
		data.setTreasureCollected(true);
		assertEquals(true, data.isCollectedTreasure());
	}



}
