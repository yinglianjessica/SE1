package client.map;
import java.util.HashMap;

import client.exceptions.InvalidMapException;

public class HalfMap {
	private HashMap<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();

	public HalfMap(HashMap<Coordinates, MapNode> fields) throws InvalidMapException {
		if(fields == null || fields.size()!=50)
			throw new InvalidMapException("Parameters in HalfMap Constructor is invalid");
		this.fields = fields;
	}
	
	public HashMap<Coordinates, MapNode> getFields() {
		return this.fields;
	}
}
