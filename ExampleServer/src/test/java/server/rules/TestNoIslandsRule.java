package server.rules;

import static org.junit.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.MethodSource;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.exceptions.InvalidMapException;

public class TestNoIslandsRule {


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
	public void playerHalfMap_hasIsland_ThrowsInvalidMapException() {
	    assertThrows(InvalidMapException.class, () -> {
			PlayerHalfMap islandMap = createIslandPlayerHalfMap();
			NoIslandsRule noIsland = new NoIslandsRule();
			noIsland.checkNoIslands(islandMap);
	    });
	}

	

	@MethodSource("createIslandPlayerHalfMap")
	private static PlayerHalfMap createIslandPlayerHalfMap() {
		Set<PlayerHalfMapNode> nodes = new HashSet<PlayerHalfMapNode>();
		int yLimit = 5;
		int xLimit = 10;

		for (int y = 0; y < yLimit; y++) {
			for (int x = 0; x < xLimit; x++) {
				if(x==1 && y==1) {
					PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, true, ETerrain.Grass);
					nodes.add(node);
				}
				else if(x!=1 && y==2){
					PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, false, ETerrain.Water);
					nodes.add(node);
				}
				else if(x==2 && y==3){
					PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, false, ETerrain.Water);
					nodes.add(node);
				}
				else if(x==5 && y==3){
					PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, false, ETerrain.Water);
					nodes.add(node);
				}
				else if(x==2 && y==4){
					PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, false, ETerrain.Water);
					nodes.add(node);
				}
				else if(x==3 && y==4){
					PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, false, ETerrain.Water);
					nodes.add(node);
				}
				else if(x==4 && y==4){
					PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, false, ETerrain.Water);
					nodes.add(node);
				}
				else if(x==5 && y==4){
					PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, false, ETerrain.Water);
					nodes.add(node);
				}
				else {
					PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, false, ETerrain.Grass);
					nodes.add(node);
				}
				
			}			
		}

		return new PlayerHalfMap("playerId", nodes);

	}

}


