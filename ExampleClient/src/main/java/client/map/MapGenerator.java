package client.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

public class MapGenerator {
	private static int xLimit = 10;
	private static int yLimit = 5;
	private static int grassLimit = 24;
	private static int mountainLimit = 5;
	private static int waterLimit = 7;

	public static HashMap<Coordinates, MapNode> generateFields(){
		HashMap<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();
		Random random = new Random();
		
		boolean terrainPlaced = false;
		boolean randomBoolean = random.nextBoolean();
		
		for (int y = 0; y < yLimit; y++) {
			int waterlimit = 0;
			int nextMountain = 0;
			for (int x = 0; x < xLimit; x++) {
				Coordinates coordinates = new Coordinates(x, y);
	        	MapNode mapNode;
				Terrain[] values = Terrain.values();
				Terrain randomTerrain = values[random.nextInt(values.length)];

				// mountains should have enough distance between each other in the designated columns and rows
				if(y%2 == 1 || x%7 == 1) {
					if(x%7 == 1 || nextMountain == 2) {
						randomTerrain = Terrain.Mountain;
					}
					else if(x == 2 || nextMountain == 0) {
						randomTerrain = values[random.nextInt(1)];
					}
					else if(nextMountain == 1) {
						randomTerrain = values[random.nextInt(2)];
					}
				}
				// for the other fields its random
				else{
			        randomTerrain = values[random.nextInt(values.length)];
			        // waterlimit shouldnt be exceeded
			        if(waterlimit == 4 && randomTerrain == Terrain.Water) {
			        	do {
					        randomTerrain = values[random.nextInt(values.length)];
				        } while (randomTerrain == Terrain.Water);
			        }
			        if((randomTerrain == Terrain.Water) && (y < 4 && y > 0 && x%9 == 0)) {
			        	do {
					        randomTerrain = values[random.nextInt(values.length)];
				        } while (randomTerrain == Terrain.Water);
			        }
		        }
				
				if(randomTerrain == Terrain.Water)
					waterlimit++;
				
				if(randomTerrain == Terrain.Mountain)
					nextMountain = 0;
				else {
					nextMountain++;
				}
				
	        	mapNode = new MapNode(coordinates, randomTerrain);
				
	        	// playing fort
				if(!terrainPlaced && mapNode.getTerrain() == Terrain.Grass) {
					randomBoolean = random.nextBoolean();
					if (randomBoolean) {
						mapNode.setOwnFort();
						terrainPlaced = true;
					}
				}
	 
		        fields.put(coordinates, mapNode);
			}			
		}
		
		// distributing terrains to match the limit 
		int grassCount = 0;
		int mountainCount = 0;
		int waterCount = 0;
		
		Iterator<Entry<Coordinates, MapNode>> iterator = fields.entrySet().iterator();
		while (iterator.hasNext()) {
		    HashMap.Entry<Coordinates, MapNode> entry = iterator.next();
		    if(entry.getValue().getTerrain() == Terrain.Grass)
		    	grassCount++;
		    if(entry.getValue().getTerrain() == Terrain.Mountain)
		    	mountainCount++;
		    if(entry.getValue().getTerrain() == Terrain.Water)
		    	waterCount++;
		}
		
		while(grassCount < grassLimit) {
			int grassToAdd = grassLimit - grassCount;
			iterator = fields.entrySet().iterator();			
			while(grassToAdd > 0) {
				if(grassToAdd < mountainCount - mountainLimit) {
					if(iterator.hasNext()) {
						HashMap.Entry<Coordinates, MapNode> entry = iterator.next();
						if(entry.getValue().getTerrain() == Terrain.Mountain) {
							entry.getValue().changeTerrain(Terrain.Grass);
							grassCount++;
							mountainCount--;
						}
					}
				}
				else{
					if(iterator.hasNext()) {
						HashMap.Entry<Coordinates, MapNode> entry = iterator.next();
						if(entry.getValue().getTerrain() == Terrain.Water) {
							entry.getValue().changeTerrain(Terrain.Grass);
							grassCount++;
							waterCount--;
						}
					}
				}
				grassToAdd = grassLimit - grassCount;
			}
		}
		
		while(mountainCount < mountainLimit) {
			int mountainToAdd = mountainLimit - mountainCount;
			iterator = fields.entrySet().iterator();			
			while(mountainToAdd > 0) {
				if(mountainToAdd < grassCount - grassLimit) {
					if(iterator.hasNext()) {
						HashMap.Entry<Coordinates, MapNode> entry = iterator.next();
						if(entry.getValue().getTerrain() == Terrain.Grass && !entry.getValue().isOwnFort()) {
							entry.getValue().changeTerrain(Terrain.Mountain);
							mountainCount++;
							grassCount--;
						}
					}
				}
				else {
					if(iterator.hasNext()) {
						HashMap.Entry<Coordinates, MapNode> entry = iterator.next();
						if(entry.getValue().getTerrain() == Terrain.Water) {
							entry.getValue().changeTerrain(Terrain.Mountain);
							mountainCount++;
							waterCount--;
						}
					}
				}
				mountainToAdd = mountainLimit - mountainCount;
			}
		}

		while(waterCount < waterLimit) {
			int waterToAdd = waterLimit - waterCount;
			iterator = fields.entrySet().iterator();			
			while(waterToAdd > 0) {
				if(waterToAdd < mountainCount - mountainLimit) {
					if(iterator.hasNext()) {
						HashMap.Entry<Coordinates, MapNode> entry = iterator.next();
						if(entry.getKey().getX()%9 != 0 && entry.getKey().getY()%4 == 2){
							if(entry.getValue().getTerrain() == Terrain.Mountain) {
								entry.getValue().changeTerrain(Terrain.Water);
								waterCount++;
								mountainCount--;
							}
						}
					}
				}
				else {
					if(iterator.hasNext()) {
						HashMap.Entry<Coordinates, MapNode> entry = iterator.next();
						if(entry.getKey().getX()%9 != 0 && entry.getKey().getY()%4 == 2){
							if(entry.getValue().getTerrain() == Terrain.Grass  && !entry.getValue().isOwnFort()) {
								entry.getValue().changeTerrain(Terrain.Water);
								waterCount++;
								grassCount--;
							}
						}
					}
				}
				waterToAdd =  waterLimit - waterCount;
			}
		}	
		return fields;
	}
}
