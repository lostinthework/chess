package server;

import com.google.gson.Gson;
import dataaccess.*;
import handler.ErrorHandler;
import model.ResponseException;
import service.*;
import io.javalin.Javalin;

public class Server {
    private Javalin app;

    private InterfaceUserDAO userDAO;
    private InterfaceAuthDAO authDAO;
    private InterfaceGameDAO gameDAO;

    public int run(int desiredPort) {
        try {
            userDAO = new SQLUserDAO();
            gameDAO = new SQLGameDAO();
            authDAO = new SQLAuthDAO();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't initialize database", e);
        }


        UserService useryService = new UserService(userDAO, authDAO, gameDAO);
        GameService gamesService = new GameService(userDAO, authDAO, gameDAO);

        app = Javalin.create(config -> {config.staticFiles.add("web");}).start(desiredPort);

        // Register your endpoints and handle exceptions here.

        // Registration
        app.post("/user", ctx -> new handler.Register(useryService).handle(ctx));

        // Login
        app.post("/session", ctx -> new handler.Login(useryService).handle(ctx));

        // Logout
        app.delete("/session", ctx -> new handler.Logout(useryService).handle(ctx));

        // List games
        app.get("/game", ctx -> new handler.List(gamesService).handle(ctx));

        // Create game
        app.post("/game", ctx -> new handler.Create(gamesService).handle(ctx));

        // Join game
        app.put("/game", ctx -> new handler.Join(gamesService).handle(ctx));

        // Clear application
        app.delete("/db", ctx -> new handler.Clear(useryService, gamesService).handle(ctx));

        app.exception(ResponseException.class, (e, ctx) -> {
            int status = switch (e.code) {
                case BadRequest -> 400;
                case Unauthorized -> 401;
                case Forbidden -> 403;
                case ServerError -> 500;
            };

            ctx.status(status);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(new ErrorHandler(e.getMessage())));
        });

        app.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(new handler.ErrorHandler(e.getMessage())));
        });

        //This line initializes the server and can be removed once you have a functioning endpoint
        return app.port();
    }

    public void stop() {
        app.stop();
    }

}