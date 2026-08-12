package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.ResponseException;
import service.GameService;
import io.javalin.http.Context;
import java.util.Map;

public class List {
    private final GameService gamesService;

    public List(GameService gamesService) {
        this.gamesService = gamesService;
    }

    public void handle(Context ctx) throws DataAccessException, ResponseException {
        var gson = new Gson();
        ctx.contentType("application/json");

        String authToken;
        authToken = ctx.header("Authorization");
        if (gamesService.getAuth(authToken) == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        // get all games
        var list = gamesService.getGames();

        // return games
        ctx.result(gson.toJson(Map.of("games", list)));
    }
}