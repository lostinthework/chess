package dataaccess;

import model.AuthData;
import model.GameData;
import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements InterfaceGameDAO {
    // Store games
    private List<GameData> games = new ArrayList<>();

    public List<GameData> getGames() {
        return games;
    }

    public GameData getGamebyID(int gameID) {
        // Iterate through the list until finding matching data
        for (GameData game : games) {
            if (game.getGameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public GameData getGamebyName(String gameName) {
        // Iterate through the list until finding matching data
        for (GameData game : games) {
            if (game.getName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    public void addGame(GameData gameData) {
        games.add(gameData);
    }

    public void joinGame(String username, String color, int gameID) {
        // Iterate through the list until finding matching data
        for (GameData game : games) {
            if (game.getGameID() == gameID) {
                game.join(username, color);
            }
        }
    }

    public void deleteGameData() {
        games = new ArrayList<>();
    }
}