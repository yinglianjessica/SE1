package client.map;

public class Coordinates {
	private int x;
	private int y;
	
	public Coordinates(int x, int y) {
		if(x < 0 || x > 19 || y < 0 || y > 9)
			throw new IllegalArgumentException("Parameters in Coordinates are invalid");
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Coordinates getCoordinates() {
		return this;
	}
}

