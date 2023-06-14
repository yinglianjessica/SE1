package server.data.map;

import server.enums.FortState;
import server.enums.PlayerPositionState;
import server.enums.Terrain;
import server.enums.TreasureState;

public class MapNode {
	private Terrain terrain;
	private PlayerPositionState playerPositionState;
	private TreasureState treasureState;
	private FortState fortstate;
	private int xCoordinate;
	private int yCoordinate;

	// FullMapNode
	public MapNode(Terrain terrain, PlayerPositionState playerPositionState, TreasureState treasureState, 
			FortState fortstate, int xCoordinate, int yCoordinate) {
		this.terrain = terrain;
        this.playerPositionState = playerPositionState;
        this.fortstate = fortstate;
        this.treasureState = treasureState;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;	
    }		

	public FortState getFortstate() {
		return fortstate;
	}
	
	public PlayerPositionState getPlayerPositionState() {
		return playerPositionState;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public TreasureState getTreasureState() {
		return treasureState;
	}
	
	public int getX() {
		return xCoordinate;
	}
	
	public int getY() {
		return yCoordinate;
	}
}