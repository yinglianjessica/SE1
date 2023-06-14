package client.movement;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.control.Control;
import client.exceptions.InvalidMapException;
import client.exceptions.MovementException;
import client.map.ClientFullMap;
import client.map.Coordinates;
import client.map.MapNode;
import client.map.Terrain;
import messagesbase.messagesfromclient.EMove;

public class PathFinder {
	static Logger logger = LoggerFactory.getLogger(Control.class);

	
	
	public static EMove getValidDirection(MapNode node, ClientFullMap mapInfo) throws MovementException {
		if(node == null || mapInfo == null)
			throw new MovementException("node or map info invalid in getValidDirection");
		HashMap<Coordinates, MapNode> waterFields = mapInfo.getWaterFieldsNodes();
        int randomIndex = 5;
		if (mapInfo.getLeft(node) != null && !waterFields.containsValue(mapInfo.getLeft(node))) {
			randomIndex = 1;
	    }
		if (mapInfo.getUp(node) != null && !waterFields.containsValue(mapInfo.getUp(node))) {
	    	randomIndex = 0;
	    }
		if (mapInfo.getDown(node) != null && !waterFields.containsValue(mapInfo.getDown(node))) {
	    	randomIndex = 3;
	    }
		if (mapInfo.getRight(node) != null && !waterFields.containsValue(mapInfo.getRight(node))) {
	    	randomIndex = 2;
	    }
        return EMove.values()[randomIndex];
	}
	
	public static Queue<MapNode> getDijkstraNodes(MapNode start, MapNode goal, ClientFullMap mapInfo) throws MovementException {
		// get MapNode path from start to goal
		if(start == null || goal == null || mapInfo == null)
			throw new MovementException("nodes or map info invalid in getDijkstraNodes");
		Map<MapNode, Integer> cost = new HashMap<>();
		Map<MapNode, Queue<MapNode>> pathTo = new HashMap<>();
		Set<MapNode> visited = new HashSet<>();
		Set<MapNode> frontier = new HashSet<>();
		cost.put(start, 0);
		pathTo.put(start, new LinkedList<>());
		int limit = 0;
		while (!visited.contains(goal) || limit == 2000) {
			limit++;
			Set<MapNode> neighbours = mapInfo.getNeighbors(start);
			neighbours.removeAll(visited);
			if(start != null)
				visited.add(start);
			if(neighbours != null && !neighbours.isEmpty()) {
				for (MapNode p : neighbours) {
					if ((p.getTerrain() != Terrain.Water && cost.get(p) == null) || (p.getTerrain() != Terrain.Water && cost.get(p) >= cost.get(start) + start.getTerrain().cost() + p.getTerrain().cost())) {
						cost.put(p, cost.get(start) + start.getTerrain().cost() + p.getTerrain().cost());
						Queue<MapNode> temp = new LinkedList<>(pathTo.get(start));				
						temp.add(p);
						pathTo.put(p, temp);
					}
					frontier.add(p);
				}
			}
			if(frontier.stream().min((MapNode lhs, MapNode rhs) -> {
				return cost.get(lhs) - cost.get(rhs);
			}).isPresent()) {
				start = frontier.stream().min((MapNode lhs, MapNode rhs) -> {
					return cost.get(lhs) - cost.get(rhs);
				}).get();
			}
			else {
				return null;
			}
			frontier.remove(start);
		}
		return pathTo.get(goal);
	}
	
	
	
	// used in nextmove
	public static EMove getDijkstraMove(MapNode start, MapNode goal, ClientFullMap mapInfo) throws InvalidMapException, MovementException {
		if(start == null || goal == null || mapInfo == null)
			throw new MovementException("nodes or map info invalid in getDijkstraNodes");
		EMove move = null;;
	    if(start.getCoordinates() == goal.getCoordinates())
	    	return null;
	    
	    MapNode startOfLoop = start;
	    startOfLoop.calculateDistance(goal);
	    MapNode ret = start;
		ret.setDistance(ret.calculateDistance(goal));    
		HashMap<Coordinates, MapNode> newWalkingNodes = new HashMap<>();
		newWalkingNodes.putAll(mapInfo.getWalkingFields()); 
		
		MapNode prev = ret;
    	boolean first = true;
        if (move != null && ret.getCoordinates().getX() == goal.getCoordinates().getX() && ret.getCoordinates().getY() == goal.getCoordinates().getY() ) {
            return move;
        }
        else {
        	// get first direction of the dijkstra nodes
        	Queue<MapNode> help = getDijkstraNodes(start, goal, mapInfo);
        	if(help != null) {
	        	for (MapNode mapNode : help) {	
	    			if(mapNode.getTerrain() == Terrain.Water) {
	    				newWalkingNodes.remove(mapNode.getCoordinates());
	    			}
	    			else {
	    				if(first && ret.calculateDistance(mapNode) == 1 && mapNode.getTerrain() != Terrain.Water) {
	    					move = EMove.values()[ret.getDir(mapNode)];
	    					logger.trace(move.toString());
		    				logger.trace("1 loop");
		    				logger.trace("Info: Calculated move from " + start.getXCoordinates() + ", " + start.getYCoordinates() + " to " + goal.getXCoordinates() + ", " + goal.getYCoordinates() + ": "+ move.toString());

	    					first = false;
    					}
	    				ret = mapNode;
	    				ret.setDistance(goal.calculateDistance(mapNode));
	    			}
	    		}
	        }

        	if (move != null || ret.getCoordinates().getX() == goal.getCoordinates().getX() && ret.getCoordinates().getY() == goal.getCoordinates().getY() ) {
	            return move;
	        }
	        
        	if(first = true) {
        		// if no dijkstra path found, move further away from goal
 	        	for (Map.Entry<Coordinates, MapNode> entry : newWalkingNodes.entrySet()) {
 	    			MapNode mapNode = entry.getValue();
 	    			if(mapNode.getTerrain() == Terrain.Water) {
 	    				newWalkingNodes.remove(entry.getKey());
 	    			}
 	    			else {
 	    				if(Math.abs(goal.calculateDistance(mapNode)) >= ret.getDistance() && ret.calculateDistance(mapNode) == 1 && mapNode.getTerrain() != Terrain.Water) {
 		    				//if <
 		    				if(mapNode.getTerrain() == Terrain.Water) {
 		    					newWalkingNodes.remove(entry.getKey());
 			    			}
 		    				else {
 			    				if(prev.getCoordinates() != mapNode.getCoordinates()) {
 			    					if(first) {
 				    					move = EMove.values()[ret.getDir(mapNode)];
 				    					first = false;
				    					logger.trace(move.toString());
				    					logger.trace("2 loop");
					    				logger.trace("Info: Calculated move from " + start.getXCoordinates() + ", " + start.getYCoordinates() + " to " + goal.getXCoordinates() + ", " + goal.getYCoordinates() + ": "+ move.toString());
 			    					}
 			    					prev = ret;
 				    				ret = mapNode;
 				    				ret.setDistance(goal.calculateDistance(mapNode));
 			    				}
 		    				}
 		    			}
 	    			}
 	    		}
 	        }
 		        
         	if (move != null || ret.getCoordinates().getX() == goal.getCoordinates().getX() && ret.getCoordinates().getY() == goal.getCoordinates().getY() ) {
 	            return move;
 	        }
	    }
		logger.debug("Dijkstra failed get valid direction instead: " + start.getXCoordinates() + ", " + start.getYCoordinates() + " to " + goal.getXCoordinates() + ", " + goal.getYCoordinates() + ": ");
        return getValidDirection(start, mapInfo);
	}	
}