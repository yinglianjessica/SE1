package server.data;

import java.util.Collection;
import java.util.HashSet;

import server.data.map.HalfMapNode;
import server.enums.PlayerGameState;

public class PlayerInformation {
    private String studentFirstName;
    private String studentLastName;
    private String studentUAccount;
    private PlayerGameState state;
    private String playerid;
    private boolean collectedTreasure;
    private Collection<HalfMapNode> mapNodes = new HashSet<>();
    private boolean sentMap = false;

    public PlayerInformation(String studentFirstName, String studentLastName, String studentUAccount, 
    		PlayerGameState state, String playerid, boolean collectedTreasure) {
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.studentUAccount = studentUAccount;
        this.state = state;
        this.playerid = playerid;
        this.collectedTreasure = collectedTreasure;
    }

	public void addMapNodes(Collection<HalfMapNode> mapNodes) {
    	if(!sentMap) {
	    	this.mapNodes = mapNodes;
	    	sentMap = true;
    	}
    }

    public boolean isSentMap() {
		return sentMap;
	}
    
    public String getStudentFirstName() {
        return studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public String getStudentUAccount() {
        return studentUAccount;
    }

    public String getPlayerid() {
        return playerid;
    }
    
    public PlayerGameState getState() {
		return state;
	}
    
    public Collection<HalfMapNode> getMapNodes() {
		return mapNodes;
	}
    
    public boolean isCollectedTreasure() {
		return collectedTreasure;
	}
    
    public void setCollectedTreasureTrue() {
		this.collectedTreasure = true;
	}
    
    public void setState(PlayerGameState state) {
		this.state = state;
	}
}
