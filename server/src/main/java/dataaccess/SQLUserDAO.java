package dataaccess;

import model.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;


public class SQLUserDAO implements InterfaceUserDAO {

    public SQLUserDAO() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                    if (false) {}
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError,
                                        String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
    }

    public void addUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            String hashedPassword = BCrypt.hashpw(userData.getPassword(), BCrypt.gensalt());
            ps.setString(1, userData.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, userData.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to add User", e);
        }
    }

    public UserData getUser(String username, String password) throws DataAccessException {
        String email = null;
        String hashedPassword = null;
        var statement = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
                hashedPassword = rs.getString("password");
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to get User", e);
        }
        if (BCrypt.checkpw(password, hashedPassword)) {
            return new UserData(username, password, email);
        }
        return null;
    }

    public UserData checkUser(String username) throws DataAccessException {
        String email = null;
        String password = null;
        var statement = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) {
                password = rs.getString("password");
                email = rs.getString("email");
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to check user", e);
        }
        return new UserData(username, password, email);
    }

    public void deleteUserData() throws DataAccessException {
        var statement = "TRUNCATE users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Delete user data", e);
        }
    }
}