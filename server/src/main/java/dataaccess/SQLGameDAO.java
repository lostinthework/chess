package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

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
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public List<GameData> getGames() {
        List<GameData> games = new ArrayList<>();
        var statement = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            var rs = ps.executeQuery();
            while (true) {
                if (rs.next()) {
                    games.add(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), new Gson().fromJson(rs.getString("game"), ChessGame.class)));
                } else {
                    return games;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public GameData getGamebyID(int gameID) {
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
        } catch (Exception e) {
        }
        return new GameData(gameID, whiteUsername, blackUsername, gameName, new Gson().fromJson(game, ChessGame.class));
    }

    public void addGame(GameData gameData) {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setInt(1, gameData.getGameID());
            ps.setString(2, gameData.getWhiteUsername());
            ps.setString(3, gameData.getBlackUsername());
            ps.setString(4, gameData.getName());
            ps.setString(5, new Gson().toJson(gameData.getGame()));
            int rows = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void deleteGameData() {
        var statement = "TRUNCATE games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void joinGame(int gameID, String username, String color) {
        System.out.println("Debugging!");
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
            int rows = ps.executeUpdate();
            System.out.println(rows);
        } catch (Exception e) {
        }
    }

}