package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.GameData;
import service.gameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class create {
    private final gameService gamesService;

    public create(gameService gamesService) {
        this.gamesService = gamesService;
    }

    public Object handle(Request req, Response res) throws DataAccessException {
        var gson = new Gson();
        res.type("application/json");
        String authToken;
        authToken = req.headers("Authorization");

        // Verify authentication
        try {
            if (gamesService.getAuth(authToken) == null) {
                throw new DataAccessException("Error: unauthorized");
            }
        }
        catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorHandler("Error: unauthorized"));
        }

        // Check for valid input
        GameData game;
        try {
            game = gson.fromJson(req.body(), GameData.class);
            if (game.getName() == null) {
                throw new DataAccessException("Error: bad request");
            }
        }
        catch (DataAccessException | JsonSyntaxException e) {
            res.status(400);
            return gson.toJson(new ErrorHandler("Error: bad request"));
        }

        // Create game
        var gameID = gamesService.createGame(game.getName());

        // return gameID
        return new Gson().toJson(Map.of("gameID", gameID));
    }
}
