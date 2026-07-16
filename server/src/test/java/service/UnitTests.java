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

    private authService authyService;
    private gameService gamesService;
    private userService useryService;

    @BeforeEach
    public void setuptests() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        authyService = new authService(userDAO, authDAO, gameDAO);
        gamesService = new gameService(userDAO, authDAO, gameDAO);
        useryService = new userService(userDAO, authDAO, gameDAO);

    }

    @Test
    @Order(1)
    @DisplayName("Get AuthData Positive")
    public void getAuthPositive() {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.addAuth(auth);
        AuthData result = authyService.getAuth("authtoken");
        assertNotNull(result);
        assertEquals("username", result.getUsername());
    }

    @Test
    @Order(2)
    @DisplayName("Get AuthData Negative")
    public void getAuthNegative() {
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(3)
    @DisplayName("Delete AuthData Positive")
    public void deleteAuthPositive() {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.addAuth(auth);
        authDAO.deleteAuth(auth);
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(4)
    @DisplayName("Delete AuthData Negative")
    public void AuthNegative() {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.deleteAuth(auth);
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(5)
    @DisplayName("Get Username Positive")
    public void getUsernamePositive() {
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
    public void clearPositive() {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.addAuth(auth);
        authyService.clear();
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(8)
    @DisplayName("Get AuthData Negative")
    public void clearNegative() {
        AuthData auth = new AuthData("username", "authtoken");
        authyService.clear();
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(9)
    @DisplayName("Get Game Positive")
    public void getGamePositive() {
        GameData game = new GameData(1, "white", "black", "name", new ChessGame());
        gameDAO.addGame(game);
        GameData result = gamesService.getGame(1);
        assertNotNull(result);
        assertEquals(game, result);
    }

    @Test
    @Order(10)
    @DisplayName("Get Game Negative")
    public void getGameNegative() {
        GameData result = gamesService.getGame(1);
        assertNull(result);
    }

    @Test
    @Order(11)
    @DisplayName("List Games Positive")
    public void listGamesPositive() {
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
    public void listGamesNegative() {
        List<GameData> result = gamesService.getGames();
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    @Order(13)
    @DisplayName("Create Game Positive")
    public void createGamePositive() {
        int result = gamesService.createGame("name");
        assertNotNull(result);
    }

    @Test
    @Order(14)
    @DisplayName("Create Game Negative")
    public void createGameNegative() {
        int result1 = gamesService.createGame("name");
        int result2 = gamesService.createGame("name");
        assertNotNull(result2);
    }

    @Test
    @Order(15)
    @DisplayName("Register Positive")
    public void registerPositive() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        RegisterResult result = useryService.register(user);
        assertNotNull(result);
    }

    @Test
    @Order(16)
    @DisplayName("Register Negative")
    public void registerNegative() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        useryService.register(user);
        assertThrows(DataAccessException.class, () -> {useryService.register(user);});
    }

    @Test
    @Order(17)
    @DisplayName("Login Positive")
    public void loginPositive() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        useryService.register(user);
        LoginResult result = useryService.login("username", "password");
        assertNotNull(result);
    }

    @Test
    @Order(18)
    @DisplayName("Login Negative")
    public void loginNegative() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        useryService.register(user);
        assertThrows(DataAccessException.class, () -> {useryService.login("username", "passwords");});
    }

    @Test
    @Order(19)
    @DisplayName("Logout Positive")
    public void logoutPositive() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        RegisterResult result = useryService.register(user);
        useryService.logout(result.getAuthToken());
        assertNotNull(result.getAuthToken());
    }

    @Test
    @Order(20)
    @DisplayName("Login Negative")
    public void logoutNegative() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        RegisterResult result = useryService.register(user);
        assertThrows(DataAccessException.class, () -> {useryService.logout(null);});
    }


}
