package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.*;


public class SQLUserDAO implements InterfaceUserDAO {

    public SQLUserDAO() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
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
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public void addUser(UserData userData) {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setString(1, userData.getUsername());
            ps.setString(2, userData.getPassword());
            ps.setString(3, userData.getEmail());
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public UserData getUser(String username, String password) {
        String email = null;
        var statement = "SELECT * FROM users WHERE username = ? and password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.setString(1, username);
            ps.setString(2, password);
            var rs = ps.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
            }
            else {
                return null;
            }
        } catch (Exception e) {
        }
        return new UserData(username, password, email);
    }

    public UserData checkUser(String username) {
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
        } catch (Exception e) {
        }
        return new UserData(username, password, email);
    }

    public void deleteUserData() {
        var statement = "TRUNCATE users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);) {
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
}
