package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import model.GameData;
import service.GameService;
import io.javalin.http.Context;

import java.util.Map;

public class Join {
    private final GameService gamesService;

    public Join(GameService gamesService) {
        this.gamesService = gamesService;
    }

    public void handle(Context ctx) throws DataAccessException, ResponseException {
        var gson = new Gson();
        ctx.contentType("application/json");

        // Verify authentication
        String authToken;
        authToken = ctx.header("Authorization");
        // Check if unauthorized
        if (gamesService.getAuth(authToken) == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        // Verify input
        String color;
        int gameID;
        Map<String, Object> input = gson.fromJson(ctx.body(), Map.class);
        color = (String)input.get("playerColor");
        Double doubleid = (Double)input.get("gameID");
        if (doubleid == null || color == null || (!color.equals("WHITE") && !color.equals("BLACK"))) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
        }
        gameID = doubleid.intValue();

        // Check that nobody has already joined the game as the desired color
        GameData gametoJoin = gamesService.getGame(gameID);
        if ((color.equals("WHITE") && gametoJoin.getWhiteUsername() != null) ||
            (color.equals("BLACK") && gametoJoin.getBlackUsername() != null)) {
            throw new ResponseException(ResponseException.Code.Forbidden, "Error: already taken");
        }

        // Join game
        gamesService.joinGame(gameID, gamesService.getUsername(authToken), color);
//        gametoJoin.join(gamesService.getUsername(authToken), color);
        ctx.result("{}");
    }
}