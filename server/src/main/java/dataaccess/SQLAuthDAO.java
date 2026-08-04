package dataaccess;

import model.AuthData;
import model.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLAuthDAO implements InterfaceAuthDAO {
    public SQLAuthDAO() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auths (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
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
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
    }

    public void addAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setString(1, authData.getauthToken());
            ps.setString(2, authData.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to add AuthData", e);
        }
    }

    public void deleteAuth(AuthData authData) throws DataAccessException {
        var statement = "DELETE FROM auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setString(1, authData.getauthToken());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to delete AuthData", e);
        }
    }

    public void deleteAuthData() throws DataAccessException {
        var statement = "TRUNCATE auths";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to delete AuthData", e);
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        String username = null;
        var statement = "SELECT * FROM auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setString(1, authToken);
            var rs = ps.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to get AuthData", e);
//            return null;
        }
        return new AuthData(username, authToken);
    }

}