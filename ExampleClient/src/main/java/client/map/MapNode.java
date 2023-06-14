package client.map;

import client.control.DataConverter;
import client.exceptions.InvalidMapException;
import messagesbase.messagesfromserver.FullMapNode;

public class MapNode {
	private Terrain terrain;
	private Coordinates coordinates;
	private boolean ownFort = false;
	private boolean enemyFort = false;
	private boolean treasurePresent = false;
	private boolean noTreasurePresent = false;
	private int distance;
	
	public MapNode(Coordinates coordinates, Terrain terrain) {
		this.coordinates = coordinates;
		this.terrain = terrain;
		this.ownFort = false;
		this.enemyFort = false;
		this.treasurePresent = false;
		this.noTreasurePresent = false;
	}
	public MapNode(FullMapNode node) {
		this.terrain = DataConverter.convertTerrain(node.getTerrain());
		this.coordinates = DataConverter.convertMapNode(node).getCoordinates();
		this.ownFort = DataConverter.convertMapNode(node).isOwnFort();
		this.enemyFort = DataConverter.convertMapNode(node).isEnemyFort();
		this.treasurePresent = DataConverter.convertMapNode(node).isTreasurePresent();
		this.noTreasurePresent = DataConverter.convertMapNode(node).isNoTreasurePresent();
	}
	
	public void setOwnFort() {
		this.ownFort = true;
	}	
	
	public void setEnemyFort() {
		this.enemyFort = true;
	}	
	
	public void setTreasurePresent() {
		this.treasurePresent = true;
	}	
	public boolean isEnemyFort() {
		return enemyFort;
	}

	public boolean isNoTreasurePresent() {
		return noTreasurePresent;
	}
	
	public boolean isOwnFort() {
		return ownFort;
	}
	
	public boolean isTreasurePresent() {
		return treasurePresent;
	}
	
	
	public int getXCoordinates() {
		return this.coordinates.getX();
	}	
	
	public int getYCoordinates() {
		return this.coordinates.getY();
	}	
	
	public Terrain getTerrain() {
		return this.terrain;
	}
	
	public void changeTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
	
	public Coordinates getCoordinates() {
		return coordinates;
	}

	public int getDistance() {
		return distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public int calculateDistance(MapNode goalNode) throws InvalidMapException {
		if(goalNode == null) 
			throw new InvalidMapException("node in calculateDistance is invalid");
		return (Math.abs((this.getXCoordinates()-goalNode.getXCoordinates())) + Math.abs((this.getYCoordinates()-goalNode.getYCoordinates())));
	}
	
	public int getDir(MapNode node) throws InvalidMapException {
		int ret = 5;
		if(node.getXCoordinates() < this.getXCoordinates() && node.getTerrain() != Terrain.Water) {
			return 1;
		}
		else if(node.getXCoordinates() > this.getXCoordinates() && node.getTerrain() != Terrain.Water) {
			return 2;
		}
		else if(node.getYCoordinates() < this.getYCoordinates() && node.getTerrain() != Terrain.Water) {
			return 0;
		}
		else if(node.getYCoordinates() > this.getYCoordinates() && node.getTerrain() != Terrain.Water) {
			return 3;
		}
		if(ret == 5) 
			throw new InvalidMapException("node in getDir is invalid");
		return ret;
	}	
}
