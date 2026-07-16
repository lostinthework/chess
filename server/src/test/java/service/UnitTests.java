package service;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

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
    @DisplayName("Get AuthData Negative")
    public void AuthNegative() {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.deleteAuth(auth);
        AuthData result = authyService.getAuth("authtoken");
        assertNull(result);
    }

    @Test
    @Order(5)
    @DisplayName("Get AuthData Positive")
    public void getUsernamePositive() {
        AuthData auth = new AuthData("username", "authtoken");
        authDAO.addAuth(auth);
        String result = authyService.getUsername("authtoken");
        assertNotNull(result);
        assertEquals("username", result);
    }

    @Test
    @Order(6)
    @DisplayName("Get AuthData Negative")
    public void getUsernameNegative() {
        String result = authyService.getUsername("authtoken");
        assertNull(result);
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

}
