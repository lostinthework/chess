package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.GameData;
import service.gameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class join {
    private final gameService gamesService;

    public join(gameService gamesService) {
        this.gamesService = gamesService;
    }

    public Object handle(Request req, Response res) throws DataAccessException {
        var gson = new Gson();
        res.type("application/json");

        // Verify authentication
        String authToken;
        authToken = req.headers("Authorization");
        try {
            if (gamesService.getAuth(authToken) == null) {
                throw new DataAccessException("Error: unauthorized");
            }
        }
        catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorHandler("Error: unauthorized"));
        }

        // Verify input
        String color;
        int gameID;
        try {
            Map<String, Object> input = new Gson().fromJson(req.body(), Map.class);
            color = (String)input.get("playerColor");
            Double doubleid = (Double)input.get("gameID");
            if (doubleid == null || color == null || (!color.equals("WHITE") && !color.equals("BLACK"))) {
                throw new DataAccessException("Error: bad request");
            }
            gameID = doubleid.intValue();
        }
        catch (DataAccessException | JsonSyntaxException | ClassCastException e) {
            res.status(400);
            return gson.toJson(new ErrorHandler("Error: bad request"));
        }

        // Check that nobody has already joined the game as the desired color
        GameData gametoJoin = gamesService.getGame(gameID);
        try {
            if ((color.equals("WHITE") && gametoJoin.getWhiteUsername() != null) || (color.equals("BLACK") && gametoJoin.getBlackUsername() != null)) {
                throw new DataAccessException("Error: already taken");
            }
        }
        catch (DataAccessException e) {
            res.status(403);
            return gson.toJson(new ErrorHandler("Error: already taken"));
        }

        // Join game
        gametoJoin.join(gamesService.getUsername(authToken), color);
        return "{}";
    }
}