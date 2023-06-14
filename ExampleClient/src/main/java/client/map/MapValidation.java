package client.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MapValidation {

	public static boolean validateMap(HalfMap halfMap) {
		boolean valid = true;
		HashMap<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();
		fields = halfMap.getFields();
		int grassCount = 0, mountainCount = 0, waterCount = 0, fortCount = 0, numberOfFields = 0;
		Iterator<Entry<Coordinates, MapNode>> iterator = fields.entrySet().iterator();
		while (iterator.hasNext()) {
		    HashMap.Entry<Coordinates, MapNode> entry = iterator.next();
		    if(entry.getValue().getTerrain() == Terrain.Grass)
		    	grassCount++;
		    if(entry.getValue().getTerrain() == Terrain.Mountain)
		    	mountainCount++;
		    if(entry.getValue().getTerrain() == Terrain.Water)
		    	waterCount++;		    
		    if(entry.getValue().isOwnFort())
			    	fortCount++;
		    numberOfFields++;
		}
		if(grassCount < 24 || mountainCount < 5 || waterCount < 7 || fortCount != 1 || numberOfFields != 50) {
			valid = false;
		}
		return valid;
	}
}
