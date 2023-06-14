package client.movement;

import java.util.Iterator;
import java.util.LinkedHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.control.Data;
import client.control.DataConverter;
import client.exceptions.InvalidMapException;
import client.exceptions.MovementException;
import client.map.ClientFullMap;
import client.map.MapNode;
import client.map.Coordinates;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class MovementControl {
	Logger logger = LoggerFactory.getLogger(MovementControl.class);
    
	private ClientFullMap mapInfo; //(x, y, terrain) maxX, maxY, my half, other half, ownFort
	private LinkedHashSet<EMove> currentMoves = new LinkedHashSet<EMove>();
	private boolean treasureCollected = false;
	private boolean goalReached = true;
	private String playerId;
	MapNode nextGoalNode = null;
	private EMove nextMove;
	
	public MovementControl(GameState gamestate, String playerId) throws MovementException, InvalidMapException {
		if(gamestate == null || playerId == null || gamestate.getMap().isEmpty()) {
			throw new MovementException("gamestate or playerId in MovementControl is invalid");
		}
		this.playerId = playerId;
		this.mapInfo = new ClientFullMap(gamestate.getMap());
	}

	public void updateVariables(GameState gamestate) {
		// update variables and set condition parameter for getNextMove

		MapNode playerposition = DataConverter.getPlayerPosition(gamestate);
		try {
			mapInfo.updateVariables(gamestate);
		} catch (InvalidMapException e) {
			logger.error("map invalid in gamestate in updatevariables");
		}
		
		for (Iterator<PlayerState> iterator = gamestate.getPlayers().iterator(); iterator.hasNext();) {
			 PlayerState eachPlayer = (PlayerState) iterator.next();
			 if(eachPlayer.getUniquePlayerID() == playerId) {
				 if(eachPlayer.hasCollectedTreasure()) {
					this.goalReached = true;
					this.treasureCollected = true;
				 }
			 }
		}
		
		if(this.nextGoalNode != null) {
			if(playerposition.getCoordinates() == this.nextGoalNode.getCoordinates()) {
				this.goalReached = true;
			}	
		}

		if(this.currentMoves == null || this.currentMoves.isEmpty()) 
			this.goalReached = true;
	}
	
	public EMove getNextMove(GameState gamestate, Data data) throws InvalidMapException, MovementException {
		updateVariables(gamestate);
		for (Iterator<PlayerState> iterator = gamestate.getPlayers().iterator(); iterator.hasNext();) {
			 PlayerState eachPlayer = (PlayerState) iterator.next();
			 if(eachPlayer.getUniquePlayerID().equals(playerId)) {
				 this.treasureCollected = eachPlayer.hasCollectedTreasure();
			 }
		}
		boolean treasureCollected = this.treasureCollected;
		MapNode playerposition = DataConverter.getPlayerPosition(gamestate);
		MapNode enemyPosition = DataConverter.getEnemyPosition(gamestate);
		data.setPlayerPosition(playerposition);
		data.setEnemyPosition(enemyPosition);
		data.setTreasureCollected(treasureCollected);
		
		logger.debug("Info of t: " + mapInfo.getTreasureFields().size() + " f: " + mapInfo.getFortFields().size() + " w: " + mapInfo.getWalkingFields().size());
		
		// get nextGoalNode depending on goal being treasure or fort
		if((!treasureCollected && this.currentMoves == null) || (!treasureCollected && this.nextMove == null) || (!treasureCollected && isGoalReached())) {
			this.nextGoalNode = GoalFinder.getNextGoalNode(playerposition, mapInfo.getTreasureFields());
			for (Iterator<FullMapNode> iterator = gamestate.getMap().getMapNodes().iterator(); iterator.hasNext();) {
				FullMapNode node = (FullMapNode) iterator.next();
				if(node.getTreasureState() == ETreasureState.MyTreasureIsPresent) {
					this.nextGoalNode = mapInfo.getMapNodeFromCo(new Coordinates(node.getX(), node.getY()));
					this.currentMoves.clear();
				}
			}
		}
		else if((treasureCollected && this.currentMoves == null) || (treasureCollected && this.nextMove == null) || (treasureCollected && isGoalReached())) {
			this.nextGoalNode = GoalFinder.getNextGoalNode(playerposition, mapInfo.getFortFields());
			for (Iterator<FullMapNode> iterator = gamestate.getMap().getMapNodes().iterator(); iterator.hasNext();) {
				FullMapNode node = (FullMapNode) iterator.next();
				if(node.getFortState() == EFortState.EnemyFortPresent) {
					this.nextGoalNode = mapInfo.getMapNodeFromCo(new Coordinates(node.getX(), node.getY()));
					this.currentMoves.clear();
				}
			}
		}	
		this.nextMove = PathFinder.getDijkstraMove(playerposition, nextGoalNode, mapInfo);
		this.goalReached = false;
		logger.info("Info: Calculated move from " + playerposition.getXCoordinates() + ", " + playerposition.getYCoordinates() + " to " + nextGoalNode.getXCoordinates() + ", " + nextGoalNode.getYCoordinates() + ": "+ nextMove.toString());
		return this.nextMove;
	}

	public boolean isGoalReached() {
		return goalReached;
	}
}
