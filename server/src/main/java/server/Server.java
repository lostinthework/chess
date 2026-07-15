package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import spark.*;

import java.util.Map;

public class Server {

    private final InterfaceUserDAO userDAO = new MemoryUserDAO();
    private final InterfaceAuthDAO authDAO = new MemoryAuthDAO();
    private final InterfaceGameDAO gameDAO = new MemoryGameDAO();

    userService useryService = new userService(userDAO, authDAO, gameDAO);
    gameService gamesService = new gameService(userDAO, authDAO, gameDAO);


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // Registration
        Spark.post("/user", (req, res) -> new handler.register(useryService).handle(req, res));

        // Login
        Spark.post("/session", (req, res) -> new handler.login(useryService).handle(req, res));

        // Logout
        Spark.delete("/session", (req, res) -> new handler.logout(useryService).handle(req, res));

        // List games
        Spark.get("/game", (req, res) -> new handler.list(gamesService).handle(req, res));

        // Create game
        Spark.post("/game", (req, res) -> new handler.create(gamesService).handle(req, res));

        // Join game
        Spark.put("/game", (req, res) -> new handler.join(gamesService).handle(req, res));

        // Clear application
        Spark.delete("/db", (req, res) -> new handler.clear(useryService, gamesService).handle(req, res));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static <T> T getBody(Request request, Class<T> classy) {
        var body = new Gson().fromJson(request.body(), classy);
        if (body == null) {
            // bad request
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}
