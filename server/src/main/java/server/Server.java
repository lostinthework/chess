package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class Server {
    private Javalin app;

    private final InterfaceUserDAO userDAO = new MemoryUserDAO();
    private final InterfaceAuthDAO authDAO = new MemoryAuthDAO();
    private final InterfaceGameDAO gameDAO = new MemoryGameDAO();

    userService useryService = new userService(userDAO, authDAO, gameDAO);
    gameService gamesService = new gameService(userDAO, authDAO, gameDAO);


    public int run(int desiredPort) {
        app = Javalin.create(config -> {config.staticFiles.add("web");}).start(desiredPort);

        // Register your endpoints and handle exceptions here.

        // Registration
        app.post("/user", ctx -> new handler.register(useryService).handle(ctx));

        // Login
        app.post("/session", ctx -> new handler.login(useryService).handle(ctx));

        // Logout
        app.delete("/session", ctx -> new handler.logout(useryService).handle(ctx));

        // List games
        app.get("/game", ctx -> new handler.list(gamesService).handle(ctx));

        // Create game
        app.post("/game", ctx -> new handler.create(gamesService).handle(ctx));

        // Join game
        app.put("/game", ctx -> new handler.join(gamesService).handle(ctx));

        // Clear application
        app.delete("/db", ctx -> new handler.clear(useryService, gamesService).handle(ctx));

        //This line initializes the server and can be removed once you have a functioning endpoint
        return app.port();
    }

    public void stop() {
        app.stop();
    }

}