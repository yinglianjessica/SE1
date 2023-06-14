package client.map;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import client.control.DataConverter;
import client.exceptions.InvalidMapException;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;

public class ClientFullMap {	
	Logger logger = LoggerFactory.getLogger(ClientFullMap.class);

	private int maxX;
	private int maxY;
	private MapNode myFort;
	private boolean left;
	private boolean up;
	private HashMap<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();
	private HashMap<Coordinates, MapNode> myHalf = new HashMap<Coordinates, MapNode>();
	private HashMap<Coordinates, MapNode> enemyHalf = new HashMap<Coordinates, MapNode>();
	private HashSet<Coordinates> waterFields = new HashSet<Coordinates>();
	private HashMap<Coordinates, MapNode> treasureFields = new HashMap<Coordinates, MapNode>();
	private HashMap<Coordinates, MapNode> walkingFields = new HashMap<Coordinates, MapNode>();
	private HashMap<Coordinates, MapNode> fortFields = new HashMap<Coordinates, MapNode>();
	private HashMap<Coordinates, MapNode> waterFieldsNodes = new HashMap<Coordinates, MapNode>();
	private TreeSet<FullMapNode> orderedForOutput;
	
	public HashMap<Coordinates, MapNode> getWalkingFields() {
		return walkingFields;
	}

	public Set<MapNode> getNeighbors(MapNode node) {
		Set<MapNode> ret = new HashSet<MapNode>();
		if(node != null) {
			if(this.getLeft(node) != null) {
				ret.add(this.getLeft(node));
			}
			if(this.getRight(node) != null) {
				ret.add(this.getRight(node));
			}
			if(this.getDown(node) != null) {
				ret.add(this.getDown(node));
			}
			if(this.getUp(node) != null) {
				ret.add(this.getUp(node));
			}
		}
		return ret;
	}
	
	public MapNode getLeft(MapNode node) {
		MapNode ret = null;	
		try {
			if(node != null && node.getXCoordinates()-1 >= 0) {
				//get mapnode from co needs throw TODO
				MapNode leftNode = getMapNodeFromCo(new Coordinates(node.getXCoordinates()-1, node.getYCoordinates()));
				if(leftNode.getTerrain() != Terrain.Water) {
					ret = leftNode;
				}
			}
		} catch(InvalidMapException e) {
			logger.error("Map is faulty: " + e);
		}
		return ret;
	}
	
	public MapNode getRight(MapNode node) {
		MapNode ret = null;
		try {
			if(node != null && node.getXCoordinates()+1 < maxX) {
				MapNode rightNode = getMapNodeFromCo(new Coordinates(node.getXCoordinates()+1, node.getYCoordinates()));
				if(rightNode.getTerrain() != Terrain.Water) {
					ret = rightNode;
				}
			}
		} catch(InvalidMapException e) {
			logger.error("Map is faulty: " + e);
		}
		return ret;
	}
	
	public MapNode getUp(MapNode node) {
		MapNode ret = null;
		try {
			if(node != null && node.getYCoordinates()-1 >= 0) {
				MapNode upNode = getMapNodeFromCo(new Coordinates(node.getXCoordinates(), node.getYCoordinates()-1));
				if(upNode.getTerrain() != Terrain.Water) {
					ret = upNode;
				}
			}
		} catch(InvalidMapException e) {
			logger.error("Map is faulty: " + e);
		}
		return ret;
	}
	
	public MapNode getDown(MapNode node) {
		MapNode ret = null;
		try{
			if(node != null && node.getYCoordinates()+1 < maxY) {
				MapNode downNode = getMapNodeFromCo(new Coordinates(node.getXCoordinates(), node.getYCoordinates()+1));
				if(downNode.getTerrain() != Terrain.Water) {
					ret = downNode;
				}
			}
		} catch(InvalidMapException e) {
			logger.error("Map is faulty: " + e);
		}
		return ret;
	}
	
	public HashMap<Coordinates, MapNode> getWaterFieldsNodes() {
		return this.waterFieldsNodes;
	}
	
	public static class CoordinatesComparator implements Comparator<FullMapNode> {
	    @Override
	    public int compare(FullMapNode c1, FullMapNode c2) {
	        int yCompare = Integer.compare(c1.getY(), c2.getY());
	        if (yCompare != 0) {
	            return yCompare;
	        }
	        return Integer.compare(c1.getX(), c2.getX());
	    }
	}

	public TreeSet<FullMapNode> getOrderedForOutput() {
		return orderedForOutput;
	}
	
	public ClientFullMap(FullMap map) throws InvalidMapException {
		if(map == null) 
			throw new IllegalArgumentException("map cannot be null");
		if(map.getMapNodes().size() != 100) 
            throw new IllegalArgumentException("map size is not correct");
		boolean fortPresent = false;
		this.maxX = 9;
		this.maxY = 4;
		this.orderedForOutput = new TreeSet<FullMapNode>(new CoordinatesComparator());
		// adding Nodes to fields, orderedForOutput, waterFieldsNodes, walkingFields
		for (Iterator<FullMapNode> iterator = map.getMapNodes().iterator(); iterator.hasNext();) {
			FullMapNode node = (FullMapNode) iterator.next();
			this.orderedForOutput.add(node);
			Coordinates coordinates = new Coordinates(node.getX(), node.getY());
			MapNode newNode = new MapNode(coordinates, DataConverter.convertTerrain(node.getTerrain()));
			if(node.getFortState() == EFortState.MyFortPresent) { // make fortstate class
				newNode.setOwnFort();
				this.myFort = newNode;
				fortPresent = true;
			}
			if(node.getTerrain() == ETerrain.Water) {
				this.waterFields.add(coordinates);
				this.waterFieldsNodes.put(coordinates, newNode);
			}
			if(node.getTerrain() != ETerrain.Water) {
				this.walkingFields.put(coordinates, newNode);
			}
			this.fields.put(new Coordinates(node.getX(), node.getY()), DataConverter.convertMapNode(node));
			if(this.maxX < node.getX())
				this.maxX = 19;
			if(this.maxY < node.getY())
				this.maxY = 9;
		}
		if(fortPresent == false)
			throw new InvalidMapException("Fort is not present");
		// determining shape of the fullmap
		this.left = false;
		this.up = false;
		if(myFort != null) {
			if(this.maxX == 19) {
				if(myFort.getXCoordinates() <= 9) {
					left = true;
				}
			}
			else {
				if(myFort.getYCoordinates() <= 4) {
					up = true;
				}
			}
		}
		
		// determining myHalf, enemyHalf, walkingfields, possible treasurefields and fortfields
		this.myHalf.putAll(fields);
		this.enemyHalf.putAll(fields);
		if(left || up) {
			for (Entry<Coordinates, MapNode> entry : fields.entrySet()) {
				if(entry.getValue().getTerrain() == Terrain.Water)
					this.walkingFields.remove(entry.getKey());
				if(entry.getKey().getX() > 9 || entry.getKey().getY() > 4) 
					this.myHalf.remove(entry.getKey());
				else 
					this.enemyHalf.remove(entry.getKey());
			}
		}
		
		else {
			for (Entry<Coordinates, MapNode> entry : fields.entrySet()) {
				if(entry.getValue().getTerrain() == Terrain.Water) 
					this.walkingFields.remove(entry.getKey());
				if(entry.getKey().getX() > 9 || entry.getKey().getY() > 4) 
					this.enemyHalf.remove(entry.getKey());
				else 
					this.myHalf.remove(entry.getKey());
			}
		}
		this.treasureFields.putAll(myHalf);
		for (Entry<Coordinates, MapNode> entry : myHalf.entrySet()) {
			if(entry.getValue().isOwnFort() || entry.getValue().getTerrain() == Terrain.Water || entry.getValue().getTerrain() == Terrain.Mountain) { //ändern ALLES
				this.treasureFields.remove(entry.getKey());
			}
		}
		this.fortFields.putAll(enemyHalf);
		for (Entry<Coordinates, MapNode> entry : enemyHalf.entrySet()) {
			if(entry.getValue().getTerrain() == Terrain.Water || entry.getValue().getTerrain() == Terrain.Mountain) { //ändern ALLES
				this.fortFields.remove(entry.getKey());
			}
		}
	}
	
	public HashMap<Coordinates, MapNode> getTreasureFields() {
		return treasureFields;
	}
	
	public HashMap<Coordinates, MapNode> getFortFields() {
		return fortFields;
	}
	
	public HashMap<Coordinates, MapNode> getFields() {
		return this.fields;
	}
	
	
	public MapNode getMapNodeFromCo(Coordinates co) throws InvalidMapException {
		MapNode ret = null;
		if(co == null || co.getX() < 0 || co.getY() < 0)
			throw new InvalidMapException("Coordinates in getMapNodeFromCo is invalid");
		for (Entry<Coordinates, MapNode> entry : fields.entrySet()) {
			if(entry.getKey().getX() == co.getX() && entry.getKey().getY() == co.getY()) {
				ret = entry.getValue();
				break;
			}
		}
		return ret;
	}
	
	public void updateVariables(GameState gamestate) throws InvalidMapException {
		if(gamestate == null) 
			throw new InvalidMapException("gamestate in updatevariables is null");
		try{
			MapNode playerposition = getPlayerPosition(gamestate);
			removeCurrentPosFromUnknownTreasure(playerposition.getCoordinates());
			removeCurrentPosFromUnknownFort(playerposition.getCoordinates());
			updateTreasurenodes(gamestate, playerposition);
			updateFortNodes(gamestate, playerposition);
		} catch(InvalidMapException e) {
			logger.error("Map is faulty: " + e);
		}
	}
	
	public void removeCurrentPosFromUnknownTreasure(Coordinates co) throws InvalidMapException {
		if(co == null || co.getX() < 0 || co.getY() < 0)
			throw new InvalidMapException("Coordinates in getMapNodeFromCo is invalid");
		HashMap<Coordinates, MapNode> newMap = new HashMap<>();
		for (Map.Entry<Coordinates, MapNode> entry : treasureFields.entrySet()) {
			MapNode mapNode = entry.getValue();
			if(!(entry.getKey().getX() == co.getX() && entry.getKey().getY() == co.getY() )) 
				newMap.put(new Coordinates(entry.getKey().getX(), entry.getKey().getY()), mapNode);
		}
		this.treasureFields.clear();
		this.treasureFields = newMap;
	}
	
	public void updateTreasurenodes(GameState gamestate, MapNode playerposition) throws InvalidMapException {
		if(gamestate == null || playerposition == null)
			throw new InvalidMapException("gamestate or playerposition in updateTreasurenodes is invalid");
		// checks for treasure at the grass field or the 9 fields surroundings of the mountain field
		if(playerposition.getTerrain() == Terrain.Mountain) {
			for (Iterator<FullMapNode> iterator = gamestate.getMap().getMapNodes().iterator(); iterator.hasNext();) {
				FullMapNode node = (FullMapNode) iterator.next();
				if(node.getTerrain() == ETerrain.Grass) {
					if(playerposition.getXCoordinates() == node.getX()+1 && (playerposition.getYCoordinates() == node.getY()+1|| playerposition.getYCoordinates() == node.getY()|| playerposition.getYCoordinates() == node.getY()-1)
							|| playerposition.getXCoordinates() == node.getX()-1 && (playerposition.getYCoordinates() == node.getY()+1|| playerposition.getYCoordinates() == node.getY()|| playerposition.getYCoordinates() == node.getY()-1)
							|| playerposition.getXCoordinates() == node.getX() && (playerposition.getYCoordinates() == node.getY()+1|| playerposition.getYCoordinates() == node.getY()|| playerposition.getYCoordinates() == node.getY()-1)
							) {
						if(node.getTreasureState() == ETreasureState.NoOrUnknownTreasureState) {

							logger.trace("treasure before removing: " + treasureFields.size());
							this.treasureFields.remove(new Coordinates(node.getX(), node.getY()));
							removeCurrentPosFromUnknownTreasure(new Coordinates(node.getX(), node.getY()));
							logger.trace("treasure after removing: " + treasureFields.size());
						}
					}
				}
			}
		}
	}
	
	public void removeCurrentPosFromUnknownFort(Coordinates co) throws InvalidMapException {
		if(co == null || co.getX() < 0 || co.getY() < 0)
			throw new InvalidMapException("Coordinates in removeCurrentPosFromUnknownFort is invalid");
		HashMap<Coordinates, MapNode> newMap = new HashMap<>();
		for (Map.Entry<Coordinates, MapNode> entry : fortFields.entrySet()) {
			MapNode mapNode = entry.getValue();
			if(entry.getKey().getX() != co.getX() && entry.getKey().getY() != co.getY() 
					|| entry.getKey().getX() == co.getX() && entry.getKey().getY() != co.getY()
					|| entry.getKey().getX() != co.getX() && entry.getKey().getY() == co.getY()) {
				newMap.put(new Coordinates(entry.getKey().getX(), entry.getKey().getY()), mapNode);
			}
		}
		this.fortFields.clear();
		this.fortFields = newMap;
	}
	
	public MapNode getPlayerPosition(GameState gamestate) throws InvalidMapException {
		if(gamestate == null)
			throw new InvalidMapException("gamestate in updateTreasurenodes is invalid");
		MapNode playerPosition = null;
		for (Iterator<FullMapNode> iterator = gamestate.getMap().getMapNodes().iterator(); iterator.hasNext();) {
			 FullMapNode node = (FullMapNode) iterator.next();
			 if(node.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition || node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition ) {
				 playerPosition = new MapNode(node);
				 break;
			 }
		}
		return playerPosition;
	}
	
	public void updateFortNodes(GameState gamestate, MapNode playerposition) throws InvalidMapException {
		if(gamestate == null || playerposition == null)
			throw new InvalidMapException("gamestate or playerposition in updateFortNodes is invalid");
		// checks for fort at the grass field or the 9 fields surroundings of the mountain field
		if(playerposition.getTerrain() == Terrain.Mountain) {
			for (Iterator<FullMapNode> iterator = gamestate.getMap().getMapNodes().iterator(); iterator.hasNext();) {
				FullMapNode node = (FullMapNode) iterator.next();
				if(node.getTerrain() == ETerrain.Grass) {
					if(playerposition.getXCoordinates() == node.getX()+1 && (playerposition.getYCoordinates() == node.getY()+1|| playerposition.getYCoordinates() == node.getY()|| playerposition.getYCoordinates() == node.getY()-1)
							|| playerposition.getXCoordinates() == node.getX()-1 && (playerposition.getYCoordinates() == node.getY()+1|| playerposition.getYCoordinates() == node.getY()|| playerposition.getYCoordinates() == node.getY()-1)
							|| playerposition.getXCoordinates() == node.getX() && (playerposition.getYCoordinates() == node.getY()+1|| playerposition.getYCoordinates() == node.getY()|| playerposition.getYCoordinates() == node.getY()-1)
							) {
						if(node.getFortState() == EFortState.NoOrUnknownFortState) {
							logger.trace("fortsize before removing: " + fortFields.size());
							this.fortFields.remove(new Coordinates(node.getX(), node.getY()));
							removeCurrentPosFromUnknownFort(new Coordinates(node.getX(), node.getY()));
							logger.trace("fortsize after removing: " + fortFields.size());

						}
					}
				}
			}
		}
	}
}