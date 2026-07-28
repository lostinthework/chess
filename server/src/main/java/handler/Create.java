package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import model.GameData;
import org.eclipse.jetty.server.Response;
import service.GameService;
import io.javalin.http.Context;

import java.util.Map;

public class Create {
    private final GameService gamesService;

    public Create(GameService gamesService) {
        this.gamesService = gamesService;
    }

    public void handle(Context ctx) throws DataAccessException, ResponseException {
        var gson = new Gson();
        ctx.contentType("application/json");
        String authToken;
        authToken = ctx.header("Authorization");

        // Verify authentication
        if (gamesService.getAuth(authToken) == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        // Check for valid input
        GameData game;
        try {
            game = gson.fromJson(ctx.body(), GameData.class);
            if (game.getName() == null) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
            }
        }
        catch (NullPointerException | JsonSyntaxException e) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
        }

        // Create game
        var gameID = gamesService.createGame(game.getName());

        // return gameID
        ctx.result(gson.toJson(Map.of("gameID", gameID)));
    }
}
