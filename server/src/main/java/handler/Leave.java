package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameData;
import model.LeaveRequest;
import model.ResponseException;
import service.GameService;

public class Leave {
    private final GameService gamesService;

    public Leave(GameService gamesService) {
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
        LeaveRequest input = gson.fromJson(ctx.body(), LeaveRequest.class);
        Double doubleid = input.getGameID();
        if (doubleid == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
        }
        gameID = doubleid.intValue();

        // Verify that it's their turn
        String username = gamesService.getUsername(authToken);
        GameData game = gamesService.getGame(gameID);
        if (username.equals(game.getWhiteUsername())) {
            game.setWhiteUsername(null);
        }
        else {
            game.setBlackUsername(null);
        }

        gamesService.updateGame(game);

        ctx.result("{}");
    }
}
