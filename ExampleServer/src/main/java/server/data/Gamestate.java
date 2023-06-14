package server.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import server.data.map.MapNode;
import server.exceptions.ElementNotFoundException;

public class Gamestate {
    private Collection<MapNode> fullMap = new HashSet<MapNode>();
    private Set<PlayerInformation> players;
    private String gameStateId;
    private String playerId;

    public Gamestate(Collection<PlayerInformation> players, String gameStateId, String playerId) {
        this.players = new HashSet<>(players);
        this.gameStateId = gameStateId;
        this.playerId = playerId;
    }

    public Gamestate(Collection<MapNode> fullMap, Collection<PlayerInformation> players, String gameStateId, String playerId) {
        this.fullMap = fullMap;
        this.players = new HashSet<>(players);
        this.gameStateId = gameStateId;
        this.playerId = playerId;

    }
    
    public Collection<MapNode> getFullMap() {
		return fullMap;
	}
    
    public String getGameStateId() {
		return gameStateId;
	}
    
    public Set<PlayerInformation> getPlayers() {
		return players;
	}
	    
    private String getPlayerId() {
		return playerId;
	}

    public PlayerInformation getPlayer() {
    	for (Iterator<PlayerInformation> iterator = getPlayers().iterator(); iterator.hasNext();) {
			PlayerInformation playerInformation = (PlayerInformation) iterator.next();
			if(playerInformation.getPlayerid() == getPlayerId()) {
				return playerInformation;
			}
		}
	    throw new ElementNotFoundException("No player found");
    }
    public PlayerInformation getEnemy() {
    	for (Iterator<PlayerInformation> iterator = getPlayers().iterator(); iterator.hasNext();) {
			PlayerInformation playerInformation = (PlayerInformation) iterator.next();
			if(playerInformation.getPlayerid() != getPlayerId()) {
				return playerInformation;
			}
		}
	    throw new ElementNotFoundException("No enemy found");
    }
}