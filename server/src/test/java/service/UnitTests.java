package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UnitTests {
    static final authService authyService = new authService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());

    @BeforeEach
    public void setuptests() {
        InterfaceUserDAO userDAO = new MemoryUserDAO();
        InterfaceAuthDAO authDAO = new MemoryAuthDAO();
        InterfaceGameDAO gameDAO = new MemoryGameDAO();
        authService authyservice = new authService(userDAO, authDAO, gameDAO);
        gameService gamesService = new gameService(userDAO, authDAO, gameDAO);
        gameService useryService = new gameService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void getAuth_() {

    }

}
