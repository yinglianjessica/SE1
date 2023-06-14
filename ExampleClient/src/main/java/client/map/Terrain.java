package client.map;

public enum Terrain {
	Grass(2), Mountain(3), Water(10000);
	private final int cost;

	private Terrain(int cost) {
		this.cost = cost;
	}

	public int cost() {
		return cost;
	}
}
