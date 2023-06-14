package client.movement;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import client.exceptions.InvalidMapException;
import client.map.Coordinates;
import client.map.MapNode;

public class GoalFinder {
	
	public static class CoordinatesComparator implements Comparator<Coordinates> {
	    @Override
	    public int compare(Coordinates c1, Coordinates c2) {
	        int yCompare = Integer.compare(c1.getY(), c2.getY());
	        if (yCompare != 0) {
	            return yCompare;
	        }
	        return Integer.compare(c1.getX(), c2.getX());
	    }
	}

	public static MapNode getNextGoalNode(MapNode playerPosition, HashMap<Coordinates, MapNode> unknownNodes) throws InvalidMapException {
		if(playerPosition == null || unknownNodes == null)
			throw new IllegalArgumentException("Parameter in getNextGoalNode is invalid");
		MapNode ret = playerPosition;
		ret.setDistance(200);
		TreeMap<Coordinates, MapNode> possibleGoalNodes = new TreeMap<>(new CoordinatesComparator());
		possibleGoalNodes.putAll(unknownNodes);
		// get the grass field that is close to the current position without producing reoccuring ones
		for (Map.Entry<Coordinates, MapNode> entry : possibleGoalNodes.entrySet()) {
			MapNode mapNode = entry.getValue(); // <= statt <
			if(Math.abs(playerPosition.calculateDistance(mapNode)) +1 < ret.getDistance() && playerPosition.calculateDistance(mapNode) != 0) {
				ret = mapNode;
				ret.setDistance(playerPosition.calculateDistance(mapNode));
			}
		}		
		
		return ret;
	}
}
