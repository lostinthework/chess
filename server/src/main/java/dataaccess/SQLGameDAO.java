package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements InterfaceGameDAO {
    public SQLGameDAO() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS games (
          `gameID` int NOT NULL,
          `whiteUsername` varchar(256) DEFAULT NULL,
          `blackUsername` varchar(256) DEFAULT NULL,
          `gameName` varchar(256) NOT NULL,
          `game` LONGTEXT NOT NULL,
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
            if (true && false) {}
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError,
                                        String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
    }

    public List<GameData> getGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        var statement = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            var rs = ps.executeQuery();
            while (true) {
                if (rs.next()) {
                    games.add(new GameData(rs.getInt("gameID"),
                                           rs.getString("whiteUsername"),
                                           rs.getString("blackUsername"),
                                           rs.getString("gameName"),
                                           new Gson().fromJson(rs.getString("game"), ChessGame.class)));
                } else {
                    return games;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to get game", e);
//            return null;
        }
    }

    public GameData getGamebyID(int gameID) throws DataAccessException {
        String whiteUsername = null;
        String blackUsername = null;
        String gameName = null;
        String game = null;
        var statement = "SELECT * FROM games WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setInt(1, gameID);
            var rs = ps.executeQuery();
            if (rs.next()) {
                whiteUsername = rs.getString("whiteUsername");
                blackUsername = rs.getString("blackUsername");
                gameName = rs.getString("gameName");
                game = rs.getString("game");
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to get game", e);
        }
        return new GameData(gameID, whiteUsername, blackUsername, gameName, new Gson().fromJson(game, ChessGame.class));
    }

    public void addGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setInt(1, gameData.getGameID());
            ps.setString(2, gameData.getWhiteUsername());
            ps.setString(3, gameData.getBlackUsername());
            ps.setString(4, gameData.getName());
            ps.setString(5, new Gson().toJson(gameData.getGame()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteGameData() throws DataAccessException {
        var statement = "TRUNCATE games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to delete data", e);
        }
    }

    public void joinGame(int gameID, String username, String color) throws DataAccessException {
        String statement;
        if (color.equals("WHITE")) {
            statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?; ";
        }
        else {
            statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?; ";
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setString(1, username);
            ps.setInt(2, gameID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to join game", e);
        }
    }

    public void updateGame(GameData game) throws DataAccessException {
        String statement;
        int gameID = game.getGameID();
        statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?; ";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setString(1, game.getWhiteUsername());
            ps.setString(2, game.getBlackUsername());
            ps.setString(3, new Gson().toJson(game.getGame()));
            ps.setInt(4, gameID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to update game", e);
        }
    }
}