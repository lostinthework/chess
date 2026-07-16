package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.GameData;
import service.gameService;
import io.javalin.http.Context;

import java.util.Map;

public class create {
    private final gameService gamesService;

    public create(gameService gamesService) {
        this.gamesService = gamesService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var gson = new Gson();
        ctx.contentType("application/json");
        String authToken;
        authToken = ctx.header("Authorization");

        // Verify authentication
        try {
            if (gamesService.getAuth(authToken) == null) {
                throw new DataAccessException("Error: unauthorized");
            }
        }
        catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(new ErrorHandler("Error: unauthorized")));
            return;
        }

        // Check for valid input
        GameData game;
        try {
            game = gson.fromJson(ctx.body(), GameData.class);
            if (game.getName() == null) {
                throw new DataAccessException("Error: bad request");
            }
        }
        catch (DataAccessException | JsonSyntaxException e) {
            ctx.status(400);
            ctx.result(gson.toJson(new ErrorHandler("Error: bad request")));
            return;
        }

        // Create game
        var gameID = gamesService.createGame(game.getName());

        // return gameID
        ctx.result(gson.toJson(Map.of("gameID", gameID)));
    }
}
