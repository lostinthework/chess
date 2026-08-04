package client;

import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.AuthData;
import model.GameData;
import model.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeEach
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }


    @Test
    public void registerPos() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);

        Assertions.assertNotNull(auth);
    }

    @Test
    public void registerNeg() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);

        Assertions.assertThrows(ResponseException.class,
                () -> facade.register(username, password, email));
    }

    @Test
    public void loginPos() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        auth = facade.login(username, password);
        Assertions.assertNotNull(auth);
    }

    @Test
    public void loginNeg() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        Assertions.assertThrows(ResponseException.class,
                () -> facade.login(username, ""));
    }

    @Test
    public void logoutPos() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        facade.logout(auth.getauthToken());
        Assertions.assertDoesNotThrow(
                () -> facade.login(username, password));
    }

    @Test
    public void logoutNeg() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        facade.logout(auth.getauthToken());
        Assertions.assertThrows(ResponseException.class,
                () -> facade.login(username, password));
    }

    @Test
    public void listPos() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        facade.createGame(auth.getauthToken(), "game");
        List<GameData> games = facade.listGames(auth.getauthToken());
        Assertions.assertNotNull(games);
    }

    @Test
    public void listNeg() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        List<GameData> games = facade.listGames(auth.getauthToken());
        Assertions.assertNotNull(games);
    }

    @Test
    public void createPos() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        Assertions.assertDoesNotThrow(() -> facade.createGame(auth.getauthToken(), "game"));
    }

    @Test
    public void createNeg() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame("", "game"));
    }

    @Test
    public void joinPos() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        int gameID = facade.createGame(auth.getauthToken(), "game");
        facade.listGames(auth.getauthToken());
        Assertions.assertDoesNotThrow(() -> facade.joinGame(auth.getauthToken(), gameID, "white"));
    }

    @Test
    public void joinNeg() throws ResponseException {
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        AuthData auth = facade.register(username, password, email);
        int gameID = facade.createGame(auth.getauthToken(), "game");
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(auth.getauthToken(), gameID, "white"));
    }

}
