package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.GameData;
import service.gameService;
import io.javalin.http.Context;

import java.util.Map;

public class join {
    private final gameService gamesService;

    public join(gameService gamesService) {
        this.gamesService = gamesService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var gson = new Gson();
        ctx.contentType("application/json");

        // Verify authentication
        String authToken;
        authToken = ctx.header("Authorization");
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

        // Verify input
        String color;
        int gameID;
        try {
            Map<String, Object> input = gson.fromJson(ctx.body(), Map.class);
            color = (String)input.get("playerColor");
            Double doubleid = (Double)input.get("gameID");
            if (doubleid == null || color == null || (!color.equals("WHITE") && !color.equals("BLACK"))) {
                throw new DataAccessException("Error: bad request");
            }
            gameID = doubleid.intValue();
        }
        catch (DataAccessException | JsonSyntaxException | ClassCastException e) {
            ctx.status(400);
            ctx.result(gson.toJson(new ErrorHandler("Error: bad request")));
            return;
        }

        // Check that nobody has already joined the game as the desired color
        GameData gametoJoin = gamesService.getGame(gameID);
        try {
            if ((color.equals("WHITE") && gametoJoin.getWhiteUsername() != null) || (color.equals("BLACK") && gametoJoin.getBlackUsername() != null)) {
                throw new DataAccessException("Error: already taken");
            }
        }
        catch (DataAccessException e) {
            ctx.status(403);
            ctx.result(gson.toJson(new ErrorHandler("Error: already taken")));
            return;
        }

        // Join game
        gametoJoin.join(gamesService.getUsername(authToken), color);
        ctx.result("{}");
    }
}