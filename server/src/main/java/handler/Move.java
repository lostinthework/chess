package handler;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.MoveRequest;
import model.ResponseException;
import model.GameData;
import service.GameService;
import io.javalin.http.Context;

import java.util.Map;

public class Move {
    private final GameService gamesService;

    public Move(GameService gamesService) {
        this.gamesService = gamesService;
    }

    public void handle(Context ctx) throws DataAccessException, ResponseException, InvalidMoveException {
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
        MoveRequest input = gson.fromJson(ctx.body(), MoveRequest.class);
        ChessMove move = input.getMove();
        Double doubleid = input.getGameID();
        if (doubleid == null || move == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
        }
        gameID = doubleid.intValue();

        // Verify that it's their turn
        String username = gamesService.getUsername(authToken);
        GameData game = gamesService.getGame(gameID);
        ChessGame.TeamColor playerColor;
        if (username.equals(game.getWhiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        }
        else {
            playerColor = ChessGame.TeamColor.BLACK;
        }
        if (game.getGame().getTeamTurn() != playerColor) {
            throw new ResponseException(ResponseException.Code.Forbidden, "It is not your turn.");
        }
        try {
            game.getGame().makeMove(move);
        }
        catch (InvalidMoveException e) {
            throw new ResponseException(ResponseException.Code.Forbidden, "Invalid move.");
        }

        gamesService.updateGame(game);

        ctx.result(gson.toJson(game));
    }
}