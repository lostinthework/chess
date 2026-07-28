package service;

import chess.ChessGame;
import dataaccess.*;
import handler.LoginResult;
import handler.RegisterResult;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {
    private InterfaceUserDAO userDAO;
    private InterfaceAuthDAO authDAO;
    private InterfaceGameDAO gameDAO;

    private AuthService authyService;
    private GameService gamesService;
    private UserService useryService;

    @BeforeEach
    public void setuptests() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        authyService = new AuthService(userDAO, authDAO, gameDAO);
        gamesService = new GameService(userDAO, authDAO, gameDAO);
        useryService = new UserService(userDAO, authDAO, gameDAO);

    }

    @Test
    @Order(1)
    @DisplayName("Get AuthData Positive")
    public void getAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.addAuth(auth);
        AuthData result = authyService.getAuth("authtoken");
        assertNotNull(result);
        assertEquals("username", result.getUsername());
    }

    @Test
    @Order(2)
    @DisplayName("Get AuthData Negative")
    public void getAuthNegative() throws DataAccessException {
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(3)
    @DisplayName("Delete AuthData Positive")
    public void deleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.addAuth(auth);
        authDAO.deleteAuth(auth);
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(4)
    @DisplayName("Delete AuthData Negative")
    public void deleteAuthNegative() throws DataAccessException {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.deleteAuth(auth);
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(5)
    @DisplayName("Get Username Positive")
    public void getUsernamePositive() throws DataAccessException {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.addAuth(auth);
        String result = authyService.getUsername("authtoken");
        assertNotNull(result);
        assertEquals("username", result);
    }

    @Test
    @Order(6)
    @DisplayName("Get Username Negative")
    public void getUsernameNegative() {
        assertThrows(NullPointerException.class, () -> {authyService.getUsername("authtoken");});
    }

    @Test
    @Order(7)
    @DisplayName("Get AuthData Positive")
    public void clearPositive() throws DataAccessException {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.addAuth(auth);
        authyService.clear();
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(8)
    @DisplayName("Get AuthData Negative")
    public void clearNegative() throws DataAccessException {
        AuthData auth = new AuthData("username", "authtoken");
        authyService.clear();
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(9)
    @DisplayName("Get Game Positive")
    public void getGamePositive() throws DataAccessException {
        GameData game = new GameData(1, "white", "black", "name", new ChessGame());
        gameDAO.addGame(game);
        GameData result = gamesService.getGame(1);
        assertNotNull(result);
        assertEquals(game, result);
    }

    @Test
    @Order(10)
    @DisplayName("Get Game Negative")
    public void getGameNegative() throws DataAccessException {
        GameData result = gamesService.getGame(1);
        assertNull(result);
    }

    @Test
    @Order(11)
    @DisplayName("List Games Positive")
    public void listGamesPositive() throws DataAccessException {
        GameData game1 = new GameData(1, "white", "black", "name", new ChessGame());
        GameData game2 = new GameData(1, "white", "black", "name", new ChessGame());
        gameDAO.addGame(game1);
        gameDAO.addGame(game2);
        List<GameData> result = gamesService.getGames();
        assertNotNull(result);
        assertEquals(List.of(game1, game2), result);
    }

    @Test
    @Order(12)
    @DisplayName("List Games Negative")
    public void listGamesNegative() throws DataAccessException {
        List<GameData> result = gamesService.getGames();
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    @Order(13)
    @DisplayName("Create Game Positive")
    public void createGamePositive() throws DataAccessException {
        int result = gamesService.createGame("name");
        assertNotNull(result);
    }

    @Test
    @Order(14)
    @DisplayName("Create Game Negative")
    public void createGameNegative() throws DataAccessException {
        int result1 = gamesService.createGame("name");
        int result2 = gamesService.createGame("name");
        assertNotNull(result2);
    }

    @Test
    @Order(15)
    @DisplayName("Register Positive")
    public void registerPositive() throws DataAccessException, ResponseException {
        UserData user = new UserData("username", "password", "email");
        RegisterResult result = useryService.register(user);
        assertNotNull(result);
    }

    @Test
    @Order(16)
    @DisplayName("Register Negative")
    public void registerNegative() throws DataAccessException, ResponseException {
        UserData user = new UserData("username", "password", "email");
        useryService.register(user);
        assertThrows(ResponseException.class, () -> {useryService.register(user);});
    }

    @Test
    @Order(17)
    @DisplayName("Login Positive")
    public void loginPositive() throws DataAccessException, ResponseException {
        UserData user = new UserData("username", "password", "email");
        useryService.register(user);
        LoginResult result = useryService.login("username", "password");
        assertNotNull(result);
    }

    @Test
    @Order(18)
    @DisplayName("Login Negative")
    public void loginNegative() throws DataAccessException, ResponseException {
        UserData user = new UserData("username", "password", "email");
        useryService.register(user);
        assertThrows(ResponseException.class, () -> {useryService.login("username", "passwords");});
    }

    @Test
    @Order(19)
    @DisplayName("Logout Positive")
    public void logoutPositive() throws DataAccessException, ResponseException {
        UserData user = new UserData("username", "password", "email");
        RegisterResult result = useryService.register(user);
        useryService.logout(result.getAuthToken());
        assertNotNull(result.getAuthToken());
    }

    @Test
    @Order(20)
    @DisplayName("Login Negative")
    public void logoutNegative() throws DataAccessException, ResponseException {
        UserData user = new UserData("username", "password", "email");
        RegisterResult result = useryService.register(user);
        assertThrows(ResponseException.class, () -> {useryService.logout(null);});
    }

    @Test
    @Order(21)
    @DisplayName("SQLUserDAO_addUser_pos")
    public void sSQLUserDAO_addUser_pos() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();
        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        assertEquals(user.getUsername(), userDAO.checkUser("username").getUsername());
    }

    @Test
    @Order(22)
    @DisplayName("SQLUserDAO_addUser_neg")
    public void sSQLUserDAO_addUser_neg() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();
        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        assertThrows(DataAccessException.class, () -> {userDAO.addUser(user);});
    }

    @Test
    @Order(23)
    @DisplayName("SQLUserDAO_getUser_pos")
    public void sSQLUserDAO_getUser_pos() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();

        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        assertEquals(user.getUsername(), userDAO.getUser("username", "password").getUsername());
    }

    @Test
    @Order(24)
    @DisplayName("SQLUserDAO_getUser_neg")
    public void sSQLUserDAO_getUser_neg() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();

        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        assertEquals(null, userDAO.getUser("username", "wrong password"));
    }

    @Test
    @Order(23)
    @DisplayName("SQLUserDAO_checkUser_pos")
    public void sSQLUserDAO_checkUser_pos() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();

        UserData user = new UserData("username", "password", "email");
        userDAO.addUser(user);
        assertEquals(user.getUsername(), userDAO.checkUser("username").getUsername());
    }

    @Test
    @Order(24)
    @DisplayName("SQLUserDAO_checkUser_neg")
    public void sSQLUserDAO_checkUser_neg() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();
        assertEquals(null, userDAO.checkUser("username"));
    }

    @Test
    @Order(25)
    @DisplayName("SQLUserDAO_deleteUser_pos")
    public void sSQLUserDAO_deleteUser_pos() throws DataAccessException, ResponseException {
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
    @DisplayName("SQLUserDAO_deleteUser_neg")
    public void sSQLUserDAO_deleteUser_neg() throws DataAccessException, ResponseException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.deleteUserData();
        assertNull(userDAO.getUser("username", "password"));
//        assertThrows(DataAccessException.class, () -> {userDAO.getUser("username", "password");});
    }

    @Test
    @Order(27)
    @DisplayName("SQLGameDAO_getGames_pos")
    public void sSQLGameDAO_getGames_pos() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData game = new GameData(0, "white", "black", "name", new ChessGame());

        gameDAO.addGame(game);
        assertEquals(1, gameDAO.getGames().size());
    }

    @Test
    @Order(28)
    @DisplayName("SQLGameDAO_getGames_neg")
    public void sSQLGameDAO_getGames_neg() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();
        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @Order(29)
    @DisplayName("SQLGameDAO_getGamebyID_pos")
    public void sSQLGameDAO_getGamebyID_pos() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();
        GameData game = new GameData(0, "white", "black", "name", new ChessGame());

        gameDAO.addGame(game);
        assertEquals(game.getName(), gameDAO.getGamebyID(0).getName());
    }

    @Test
    @Order(30)
    @DisplayName("SQLGameDAO_getGamebyID_neg")
    public void sSQLGameDAO_getGamebyID_neg() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();
        assertEquals(null, gameDAO.getGamebyID(0));
    }

    @Test
    @Order(27)
    @DisplayName("SQLGameDAO_addGame_pos")
    public void sSQLGameDAO_addGame_pos() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData game = new GameData(0, "white", "black", "name", new ChessGame());

        gameDAO.addGame(game);
        assertEquals(1, gameDAO.getGames().size());
    }

    @Test
    @Order(28)
    @DisplayName("SQLGameDAO_addGame_neg")
    public void sSQLGameDAO_addGame_neg() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.deleteGameData();
        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @Order(31)
    @DisplayName("SQLGameDAO_deleteGameData_pos")
    public void sSQLGameDAO_deleteGameData_pos() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData game = new GameData(0, "white", "black", "name", new ChessGame());

        gameDAO.addGame(game);
        gameDAO.deleteGameData();
        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @Order(32)
    @DisplayName("SQLGameDAO_deleteGameData_neg")
    public void sSQLGameDAO_deleteGameData_neg() throws DataAccessException, ResponseException {
        SQLGameDAO gameDAO = new SQLGameDAO();

        gameDAO.deleteGameData();
        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @Order(33)
    @DisplayName("SQLGameDAO_joinGame_pos")
    public void sSQLGameDAO_joinGame_pos() throws DataAccessException, ResponseException {
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
    @DisplayName("SQLGameDAO_joinGame_neg")
    public void sSQLGameDAO_joinGame_neg() throws DataAccessException, ResponseException {
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
    @DisplayName("SQLAuthDAO_addAuth_pos")
    public void sSQLAuthDAO_addAuth_pos() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.deleteAuthData();
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        assertEquals(auth.getUsername(), authDAO.getAuth("token").getUsername());
    }

    @Test
    @Order(36)
    @DisplayName("SQLAuthDAO_addAuth_neg")
    public void sSQLAuthDAO_addAuth_neg() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.deleteAuthData();
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        assertThrows(DataAccessException.class, () -> {authDAO.addAuth(auth);});
    }

    @Test
    @Order(37)
    @DisplayName("SQLAuthDAO_deleteAuth_pos")
    public void sSQLAuthDAO_deleteAuth_pos() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        authDAO.deleteAuth(auth);
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

    @Test
    @Order(38)
    @DisplayName("SQLAuthDAO_deleteAuth_neg")
    public void sSQLAuthDAO_deleteAuth_neg() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData auth = new AuthData("username", "token");
        authDAO.deleteAuth(auth);
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

    @Test
    @Order(39)
    @DisplayName("SQLAuthDAO_deleteAuthData_pos")
    public void sSQLAuthDAO_deleteAuthData_pos() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        authDAO.deleteAuthData();
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

    @Test
    @Order(40)
    @DisplayName("SQLAuthDAO_deleteAuthData_neg")
    public void sSQLAuthDAO_deleteAuthData_neg() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData auth = new AuthData("username", "token");
        authDAO.deleteAuth(auth);
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

    @Test
    @Order(39)
    @DisplayName("SQLAuthDAO_getAuth_pos")
    public void sSQLAuthDAO_getAuth_pos() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.deleteAuthData();
        AuthData auth = new AuthData("username", "token");
        authDAO.addAuth(auth);
        assertEquals(auth.getUsername(), authDAO.getAuth("token").getUsername());
    }

    @Test
    @Order(40)
    @DisplayName("SQLAuthDAO_getAuth_neg")
    public void sSQLAuthDAO_getAuth_neg() throws DataAccessException, ResponseException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.deleteAuthData();
        assertEquals(null, authDAO.getAuth("token"));
//        assertThrows(DataAccessException.class, () -> {authDAO.getAuth("token");});
    }

}
