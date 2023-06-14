package client.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.control.Control;
import client.control.Data;
import client.control.EGameState;
import client.map.MapNode;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.FullMapNode;

public class CLI implements PropertyChangeListener {
	Logger logger = LoggerFactory.getLogger(Control.class);

	private MapNode playerPosition;
	private MapNode enemyPosition;
	private boolean collectedTreasure = false;
	private EGameState gamestate = EGameState.MustAct;

    public CLI(Data data) {
        data.addPropertyChangeListener(this);
    }

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		try {
            switch (event.getPropertyName()) {
                case "player":
                    this.playerPosition = (MapNode) event.getNewValue();
                    break;
                case "enemy":
                    this.enemyPosition = (MapNode) event.getNewValue();
                    break;
                case "treasure":
                    this.collectedTreasure = (boolean) event.getNewValue();
                    break;
                case "gamestate":
                    this.gamestate = (EGameState) event.getNewValue();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid property name: " + event.getPropertyName());
            }
            showGame();
        } catch (Exception e) {
        	logger.error("Error occurred while handling property change event: " + e.getMessage());
        }
	}
	
	public void showGame() {
		try {
			String output = "\nGAME INFORMATION\n\n";
			output += "MAP\n\n";
			int y = 0;
			for (FullMapNode node : Data.getMapInfo().getOrderedForOutput()) {
				if(y < node.getY()) {
					y++;
					output += "\n";
				}
				if(playerPosition != null && playerPosition.getXCoordinates() == node.getX() && playerPosition.getYCoordinates() == node.getY()) {
					output += "\u001B[32mP\u001B[0m";
				}
				else if(enemyPosition != null && enemyPosition.getXCoordinates() == node.getX() && enemyPosition.getYCoordinates() == node.getY()) {
					output += "\u001B[31mE\u001B[0m";
				}
				else if(node.getFortState() == EFortState.EnemyFortPresent || node.getFortState() == EFortState.MyFortPresent) {
					output += "F";
				}
				else {
					if(node.getTerrain() == ETerrain.Grass)
						output += "G";
					if(node.getTerrain() == ETerrain.Water)
						output += "W";
					if(node.getTerrain() == ETerrain.Mountain)
						output += "M";
				}
			}
			output += "\n" + (collectedTreasure? "\u001B[32mTREASURE COLLECTED\n\u001B[0m" : "\u001B[31mTREASURE NOT COLLECTED YET\n\u001B[0m");
			if(gamestate.toString().equals("Lost")) 
				output += "\n\u001B[31mGAME OVER YOU LOST\u001B[0m";
			if(gamestate.toString().equals("Won")) 
				output += "\n\u001B[32mGAME OVER YOU WON\u001B[0m";
			System.out.println(output);
	    } catch (Exception e) {
	    	logger.error("Error occurred while handling property change event: " + e.getMessage());
	    }
	}

}
