package client.control;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import client.map.ClientFullMap;
import client.map.MapNode;

public class Data {
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

	private static ClientFullMap mapInfo;
	private static MapNode playerPosition = null;
	private static MapNode enemyPosition = null;
	private static boolean collectedTreasure = false;
	private static EGameState gamestate = EGameState.MustAct;
	
	public Data(ClientFullMap mapInfo) throws IllegalArgumentException {
        if (mapInfo == null) {
            throw new IllegalArgumentException("mapInfo cannot be null");
        }
		Data.mapInfo = mapInfo;
	}
	
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
    
    public void setPlayerPosition(MapNode newPlayerPosition) {
        MapNode oldPlayerPosition = Data.playerPosition;
        Data.playerPosition = newPlayerPosition;
        support.firePropertyChange("player", oldPlayerPosition, playerPosition);
    }
    
    public void setEnemyPosition(MapNode newEnemyPosition) {
        MapNode oldEnemyPosition = Data.playerPosition;
        Data.enemyPosition = newEnemyPosition;
        support.firePropertyChange("enemy", oldEnemyPosition, enemyPosition);
    }
    
    public void setTreasureCollected(boolean newCollectedTreasure) {
    	Data.collectedTreasure = newCollectedTreasure;
        support.firePropertyChange("treasure", false, collectedTreasure);
    }
    
    public void changeGameState(EGameState newGamestate) {
    	EGameState oldGamestate = Data.gamestate;
        Data.gamestate = newGamestate;
        support.firePropertyChange("gamestate", oldGamestate, newGamestate);
    }
    
    public MapNode getEnemyPosition() {
		return enemyPosition;
	}
    
    public EGameState getGamestate() {
		return gamestate;
	}
    
    public static boolean isCollectedTreasure() {
		return collectedTreasure;
	}
    
    public static ClientFullMap getMapInfo() {
		return mapInfo;
	}
    
    public MapNode getPlayerPosition() {
		return playerPosition;
	}
}
