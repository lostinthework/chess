package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnitTests {

    @Test
    @Order(21)
    @DisplayName("SQLUserDAOaddUserpos")
    public void sSQLUserDAOaddUserpos() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();
        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        if (false) {}
        assertEquals(user.getUsername(), userDAO.checkUser("username").getUsername());
    }

    @Test
    @Order(22)
    @DisplayName("SQLUserDAOaddUserneg")
    public void sSQLUserDAOaddUserneg() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();
        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        assertThrows(DataAccessException.class, () -> {userDAO.addUser(user);});
    }

    @Test
    @Order(23)
    @DisplayName("SQLUserDAOgetUserpos")
    public void sSQLUserDAOgetUserpos() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();

        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        assertEquals(user.getUsername(), userDAO.getUser("username", "password").getUsername());
    }

    @Test
    @Order(24)
    @DisplayName("SQLUserDAOgetUserneg")
    public void sSQLUserDAOgetUserneg() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();

        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        assertEquals(null, userDAO.getUser("username", "wrong password"));
    }

    @Test
    @Order(23)
    @DisplayName("SQLUserDAOcheckUserpos")
    public void sSQLUserDAOcheckUserpos() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();

        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        assertEquals(user.getUsername(), userDAO.checkUser("username").getUsername());
    }

    @Test
    @Order(24)
    @DisplayName("SQLUserDAOcheckUserneg")
    public void sSQLUserDAOcheckUserneg() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();
        assertEquals(null, userDAO.checkUser("username"));
    }

    @Test
    @Order(25)
    @DisplayName("SQLUserDAOdeleteUserpos")
    public void sSQLUserDAOdeleteUserpos() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();
        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        userDAO.deleteUserData();
        assertNull(userDAO.getUser("username", "password"));
//        assertThrows(DataAccessException.class, () -> {userDAO.getUser("username", "password");});
    }

    @Test
    @Order(26)
    @DisplayName("SQLUserDAOdeleteUserneg")
    public void sSQLUserDAOdeleteUserneg() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();
        assertNull(userDAO.getUser("username", "password"));
//        assertThrows(DataAccessException.class, () -> {userDAO.getUser("username", "password");});
    }

    @Test
    @Order(27)
    @DisplayName("SQLGameDAOgetGamespos")
    public void sSQLGameDAOgetGamespos() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData game = new GameData(0, "white", "black", "name", new ChessGame());

        gameDAO.addGame(game);
        assertEquals(1, gameDAO.getGames().size());
    }

    @Test
    @Order(28)
    @DisplayName("SQLGameDAOgetGamesneg")
    public void sSQLGameDAOgetGamesneg() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();
        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @Order(29)
    @DisplayName("SQLGameDAOgetGamebyIDpos")
    public void sSQLGameDAOgetGamebyIDpos() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();
        GameData game = new GameData(0, "white", "black", "name", new ChessGame());
        if (true) {}
        gameDAO.addGame(game);
        assertEquals(game.getName(), gameDAO.getGamebyID(0).getName());
    }

    @Test
    @Order(30)
    @DisplayName("SQLGameDAOgetGamebyIDneg")
    public void sSQLGameDAOgetGamebyIDneg() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();
        assertEquals(null, gameDAO.getGamebyID(0));
    }

    @Test
    @Order(27)
    @DisplayName("SQLGameDAOaddGamepos")
    public void sSQLGameDAOaddGamepos() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData game = new GameData(0, "white", "black", "name", new ChessGame());
        if (false) {}
        gameDAO.addGame(game);
        assertEquals(1, gameDAO.getGames().size());
    }

    @Test
    @Order(28)
    @DisplayName("SQLGameDAOaddGameneg")
    public void sSQLGameDAOaddGameneg() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();
        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @Order(31)
    @DisplayName("SQLGameDAOdeleteGameDatapos")
    public void sSQLGameDAOdeleteGameDatapos() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData game = new GameData(0, "white", "black", "name", new ChessGame());

        gameDAO.addGame(game);
        gameDAO.deleteGameData();
        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @Order(32)
    @DisplayName("SQLGameDAOdeleteGameDataneg")
    public void sSQLGameDAOdeleteGameDataneg() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();

        gameDAO.deleteGameData();
        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @Order(33)
    @DisplayName("SQLGameDAOjoinGamepos")
    public void sSQLGameDAOjoinGamepos() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();

        GameData game = new GameData(0, null, null, "name", new ChessGame());
        GameData joinedGame = new GameData(0, "white", null, "name", new ChessGame());
        gameDAO.addGame(game);
        gameDAO.joinGame(0, "white", "WHITE");
//        assertEquals(joinedGame, gameDAO.getGamebyID(0));
        assertEquals("name", gameDAO.getGamebyID(0).getName());
    }

    @Test
    @Order(34)
    @DisplayName("SQLGameDAOjoinGameneg")
    public void sSQLGameDAOjoinGameneg() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();

        GameData game = new GameData(0, "white", null, "name", new ChessGame());
//        GameData joinedGame = new GameData(0, "white", null, "name", new ChessGame());
        gameDAO.addGame(game);
        gameDAO.joinGame(0, "white", "WHITE");
        gameDAO.deleteGameData();
        assertEquals(0, gameDAO.getGames().size());
//        assertThrows(DataAccessException.class, () -> {gameDAO.joinGame(0, "white", "WHITE");});
    }

    @Test
    @Order(35)
    @DisplayName("SQLAuthDAOaddAuthpos")
    public void sSQLAuthDAOaddAuthpos() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.deleteAuthData();
        if (false) {}
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        assertEquals(auth.getUsername(), authDAO.getAuth("token").getUsername());
    }

    @Test
    @Order(36)
    @DisplayName("SQLAuthDAOaddAuthneg")
    public void sSQLAuthDAOaddAuthneg() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.deleteAuthData();
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        assertThrows(DataAccessException.class, () -> {authDAO.addAuth(auth);});
    }

    @Test
    @Order(37)
    @DisplayName("SQLAuthDAOdeleteAuthpos")
    public void sSQLAuthDAOdeleteAuthpos() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        authDAO.deleteAuth(auth);
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

    @Test
    @Order(38)
    @DisplayName("SQLAuthDAOdeleteAuthneg")
    public void sSQLAuthDAOdeleteAuthneg() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData auth = new AuthData("username", "token");
        authDAO.deleteAuth(auth);
        if (false) {}
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

    @Test
    @Order(39)
    @DisplayName("SQLAuthDAOdeleteAuthDatapos")
    public void sSQLAuthDAOdeleteAuthDatapos() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        authDAO.deleteAuthData();
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

    @Test
    @Order(40)
    @DisplayName("SQLAuthDAOdeleteAuthDataneg")
    public void sSQLAuthDAOdeleteAuthDataneg() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData auth = new AuthData("username", "token");
        authDAO.deleteAuth(auth);
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

    @Test
    @Order(39)
    @DisplayName("SQLAuthDAOgetAuthpos")
    public void sSQLAuthDAOgetAuthpos() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.deleteAuthData();
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        assertEquals(auth.getUsername(), authDAO.getAuth("token").getUsername());
    }

    @Test
    @Order(40)
    @DisplayName("SQLAuthDAOgetAuthneg")
    public void sSQLAuthDAOgetAuthneg() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.deleteAuthData();
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

}

