package server.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.data.Game;
import server.exceptions.ElementNotFoundException;

public class GameControl {
    private static final int MAX_GAMES_SIZE = 99;
    private static final int INDEX_GAME_TO_BE_REMOVED = 0;
	private static List<Game> games = new ArrayList<>(MAX_GAMES_SIZE);
    private static Set<String> gameIds = new HashSet<>(MAX_GAMES_SIZE);
    private static final Logger logger = LoggerFactory.getLogger(GameControl.class);
	
	public static void addGame(Game game) {
		if (games.size() >= MAX_GAMES_SIZE) {
           	Game removedGame = games.remove(INDEX_GAME_TO_BE_REMOVED);
           	gameIds.remove(removedGame.getGameId());
		}
		games.add(game);
	    gameIds.add(game.getGameId());
        logger.info(String.format("Added new game with game ID: %s Game count: %d", game.getGameId(), games.size()));
    }

    public static boolean isGameExists(String gameId) {
        return gameIds.contains(gameId);
    }

	public static Game getGameById(String gameId) {
		for (Game game : games) {
            if (game.getGameId().equals(gameId)) {
                return game;
            }
        }
	    throw new ElementNotFoundException("No game found");
	}
}