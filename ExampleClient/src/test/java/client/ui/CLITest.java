
package client.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

import client.control.Data;
import client.control.EGameState;
import client.map.ClientFullMap;
import client.map.Coordinates;
import client.map.MapNode;
import client.map.Terrain;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class CLITest {

    private CLI cli;
    private Data data;
    private GameState gamestate;
    private MapNode node;

    @Before
    public void setUpBeforeClass() {
    	node = new MapNode(new Coordinates(1,1), Terrain.Grass);
    	data = mock(Data.class);
    	when(data.getEnemyPosition()).thenReturn(node);
    	when(data.getPlayerPosition()).thenReturn(node);
        cli = new CLI(data);
    }
    
    @Test
    public void testPropertyChange_player() {
    	data.changeGameState(EGameState.MustAct);
		data.setEnemyPosition(node);
		data.setPlayerPosition(node);
		data.setTreasureCollected(true);
		data.getPlayerPosition();
		data.getGamestate();
		data.getEnemyPosition();
		data.changeGameState(EGameState.Won);
		data.changeGameState(EGameState.Lost);
		data.changeGameState(EGameState.MustAct);
		
    }
    


}
	
	
