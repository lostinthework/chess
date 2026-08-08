package model;

import chess.ChessMove;

public class MoveRequest {
    private final double gameID;
    private final ChessMove move;

    public MoveRequest (int gameID, ChessMove move) {
        this.gameID = gameID;
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

    public double getGameID() {
        return gameID;
    }
}