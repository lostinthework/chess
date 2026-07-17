package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import io.javalin.http.Context;
import java.util.Map;

public class List {
    private final GameService gamesService;

    public List(GameService gamesService) {
        this.gamesService = gamesService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var gson = new Gson();
        ctx.contentType("application/json");

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

        // get all games
        var list = gamesService.getGames();

        // return games
        var results = list.stream().map(g -> new ListResult(g.getGameID(), g.getWhiteUsername(), g.getBlackUsername(), g.getName())).toList();
        ctx.result(gson.toJson(Map.of("games", results)));
    }
}