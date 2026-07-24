package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//public class SQLGameDAO implements InterfaceGameDAO {
    public SQLUserDAO() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS  games (
          'gameID' int NOT NULL,
          `whiteUsername` varchar(256) NOT NULL,
          `blackUsername` varchar(256) NOT NULL,
          `gameName` varchar(256) NOT NULL,
          PRIMARY KEY (`gameID`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

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

    public void deleteGameData() {
        games = new ArrayList<>();
    }
}