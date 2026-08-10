package dataaccess;

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

    public void addGame(GameData gameData) {
        games.add(gameData);
    }

    public void joinGame(int gameID, String username, String color) {
        GameData game = getGamebyID(gameID);
        game.join(username, color);
    }

    public void deleteGameData() {
        games = new ArrayList<>();
    }
    public void deleteGame(int gameID) {}

    public void updateGame(GameData newGame) {
//        GameData game = getGamebyID(newGame.getGameID());
//        game = new GameData(newGame.getGameID(), newGame.getWhiteUsername(), newGame.getBlackUsername(), newGame.getName(), newGame.getGame());
    }
}