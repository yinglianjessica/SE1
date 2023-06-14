package client.map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.exceptions.InvalidMapException;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMapNode;

class MapNodeTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
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
	public void getDirTest_SameNode_ThrowsException() {
		MapNode newNode = new MapNode(new Coordinates(1,1), Terrain.Mountain);
	    assertThrows(InvalidMapException.class, () -> {
			newNode.getDir(newNode);
	        throw new InvalidMapException();
	    });
	}
	
	// negative test
	@Test
	public void calculateDistanceTest_Null_ThrowsException() {
		MapNode newNode = new MapNode(new Coordinates(1,1), Terrain.Mountain);
	    assertThrows(InvalidMapException.class, () -> {
			newNode.calculateDistance(null);
	        throw new InvalidMapException();
	    });
	}

}