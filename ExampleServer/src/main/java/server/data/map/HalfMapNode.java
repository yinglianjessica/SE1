package server.data.map;

import server.enums.Terrain;

public class HalfMapNode {
	private Terrain terrain;
	private boolean fortPresent;
	private int xCoordinate;
	private int yCoordinate;	
	
	// HalfMapNode
	public HalfMapNode(Terrain terrain, boolean fortPresent, int xCoordinate, int yCoordinate) {
		this.terrain = terrain;
        this.fortPresent = fortPresent;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;	
    }	
	
	public boolean isFortPresent() {
		return fortPresent;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}

	public int getX() {
		return xCoordinate;
	}
	
	public int getY() {
		return yCoordinate;
	}
}
