package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.gameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class list {
    private final gameService gamesService;

    public list(gameService gamesService) {
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

        // get all games
        var list = gamesService.getGames();

        // return games
        var results = list.stream().map(g -> new ListResult(g.getGameID(), g.getWhiteUsername(), g.getBlackUsername(), g.getName())).toList();
        return new Gson().toJson(Map.of("games", results));
    }
}