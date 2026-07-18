package dataaccess;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLUserDAO implements InterfaceUserDAO {

    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE TABLE IF NOT EXISTS users ()";
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }
}
