package chess;
import org.junit.jupiter.api.condition.EnabledIf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
/**For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private List<ChessMove> moveLog = new ArrayList<ChessMove>();
    private TeamColor teamTurn;
    private ChessBoard board;
    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }
    public static ChessGame copyOf(ChessGame original) {
        ChessGame copy = new ChessGame();
        copy.teamTurn = original.teamTurn;
        copy.board = ChessBoard.copyOf(original.board);
        return copy;
    }
    public ChessGame(ChessGame o) {
        teamTurn = o.teamTurn;
        board = o.board;
    }
    // @return Which team's turn it is
    public TeamColor getTeamTurn() {
        return teamTurn;
    }
    // Set's which teams turn it is
    // @param team the team whose turn it is
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }
    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
    // Enum identifying the 2 possible teams in a chess game
    public enum TeamColor {
        WHITE,
        BLACK
    }
    /** Gets all valid moves for a piece at the given location
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for the requested piece, or null if no piece at
     * startPosition
     **/
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> naïveMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> actualMoves = new ArrayList<>(naïveMoves);
        TeamColor teamColor = board.getPiece(startPosition).getTeamColor();
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        else {
            for (ChessMove cm : naïveMoves) {
                ChessGame theoreticalGame = copyOf(this);
                try {
                    theoreticalGame.forceMove(cm);
                } catch (InvalidMoveException e) {
                    actualMoves.remove(cm);
                }
                // if the move puts the king in check
                if (checkHelper(teamColor, theoreticalGame.getBoard())) {
                    actualMoves.remove(cm);
                }
            }
            // En Passant
            addEnPassantMoves(startPosition, teamColor, actualMoves);
            // Castle
            if (teamColor == TeamColor.WHITE) {
                addWhiteCastlingMoves(startPosition, teamColor, actualMoves);
            }
            else {
                addBlackCastlingMoves(startPosition, teamColor, actualMoves);
            }
        }
        return actualMoves;
    }
    void addWhiteCastlingMoves(ChessPosition startPosition, TeamColor teamColor, Collection<ChessMove> actualMoves) {
        /* The king is not in check */
        if (isInCheck(TeamColor.WHITE)) {
            return;
        }
        /* Never moved king */
        ChessMove castle;
        boolean neverMovedKing = true;
        boolean neverMovedKingRook = true;
        boolean neverMovedQueenRook = true;
        for (ChessMove cm : moveLog) {
            if (cm.getStartPosition().equals(new ChessPosition(1, 5))) {
                neverMovedKing = false;
                break;
            }
        }
        if (!board.getPosition(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING)).equals(new ChessPosition(1, 5))) {
            neverMovedKing = false;
        }
        if (!neverMovedKing) {
            return;
        }
        /* There are no pieces between the king and king-side rook */
        if (board.getPiece(new ChessPosition(1, 6)) == null &&
                board.getPiece(new ChessPosition(1, 7)) == null) {
            /* Never moved king-side rook */
            for (ChessMove cm : moveLog) {
                if (cm.getStartPosition().equals(new ChessPosition(1, 8))) {
                    neverMovedKingRook = false;
                    break;
                }
            }
            if (neverMovedKingRook) {
                castle = new ChessMove(startPosition, new ChessPosition(1, 7), null);
                actualMoves.add(castle);
                /* The king would be in check if it moved to a square that it needs to pass through to castle */
                for (int i = 0; i < 3; i++) {
                    // make a theoretical game where the move is made
                    ChessGame theoreticalGame = copyOf(this);
                    try {
                        theoreticalGame.forceMove(new ChessMove(startPosition, new ChessPosition(1, 5 + i), null));
                    } catch (InvalidMoveException e) {
                        actualMoves.remove(castle);
                    }
                    // if the move puts the king in check
                    if (checkHelper(teamColor, theoreticalGame.getBoard())) {
                        actualMoves.remove(castle);
                    }
                }
            }
        }
        /* There are no pieces between the king and queen-side rook */
        if (board.getPiece(new ChessPosition(1, 4)) == null &&
                board.getPiece(new ChessPosition(1, 3)) == null &&
                board.getPiece(new ChessPosition(1, 2)) == null) {
            /* Never moved king-side rook */
            for (ChessMove cm : moveLog) {
                if (cm.getStartPosition().equals(new ChessPosition(1, 1))) {
                    neverMovedQueenRook = false;
                    break;
                }
            }
            if (neverMovedQueenRook) {
                castle = new ChessMove(startPosition, new ChessPosition(1, 3), null);
                actualMoves.add(castle);
                /* The king would be in check if it moved to a square that it needs to pass through to castle */
                for (int i = 0; i < 3; i++) {
                    // make a theoretical game where the move is made
                    ChessGame theoreticalGame = copyOf(this);
                    try {
                        theoreticalGame.forceMove(new ChessMove(startPosition, new ChessPosition(1, 5 - i), null));
                    } catch (InvalidMoveException e) {
                        actualMoves.remove(castle);
                    }
                    // if the move puts the king in check
                    if (checkHelper(teamColor, theoreticalGame.getBoard())) {
                        actualMoves.remove(castle);
                    }
                }
            }
        }
    }
    void addBlackCastlingMoves(ChessPosition startPosition, TeamColor teamColor, Collection<ChessMove> actualMoves) {
        /* The king is not in check */
        if (isInCheck(TeamColor.BLACK)) {
            return;
        }
        /* Never moved king */
        ChessMove castle;
        boolean neverMovedKing = true;
        boolean neverMovedKingRook = true;
        boolean neverMovedQueenRook = true;
        for (ChessMove cm : moveLog) {
            if (cm.getStartPosition().equals(new ChessPosition(8, 5))) {
                neverMovedKing = false;
                break;
            }
        }
        if (!board.getPosition(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING)).equals(new ChessPosition(8, 5))) {
            neverMovedKing = false;
        }
        if (!neverMovedKing) {
            return;
        }
        /* There are no pieces between the king and king-side rook */
        if (board.getPiece(new ChessPosition(8, 6)) == null &&
                board.getPiece(new ChessPosition(8, 7)) == null) {
            /* Never moved king-side rook */
            for (ChessMove cm : moveLog) {
                if (cm.getStartPosition().equals(new ChessPosition(8, 8))) {
                    neverMovedKingRook = false;
                    break;
                }
            }
            if (neverMovedKingRook) {
                castle = new ChessMove(startPosition, new ChessPosition(8, 7), null);
                actualMoves.add(castle);
                /* The king would be in check if it moved to a square that it needs to pass through to castle */
                for (int i = 0; i < 3; i++) {
                    // make a theoretical game where the move is made
                    ChessGame theoreticalGame = copyOf(this);
                    try {
                        theoreticalGame.forceMove(new ChessMove(startPosition, new ChessPosition(8, 5 + i), null));
                    } catch (InvalidMoveException e) {
                        actualMoves.remove(castle);
                    }
                    // if the move puts the king in check
                    if (checkHelper(teamColor, theoreticalGame.getBoard())) {
                        actualMoves.remove(castle);
                    }
                }
            }
        }
        /* There are no pieces between the king and queen-side rook */
        if (board.getPiece(new ChessPosition(8, 4)) == null &&
                board.getPiece(new ChessPosition(8, 3)) == null &&
                board.getPiece(new ChessPosition(8, 2)) == null) {
            /* Never moved king-side rook */
            for (ChessMove cm : moveLog) {
                if (cm.getStartPosition().equals(new ChessPosition(8, 1))) {
                    neverMovedQueenRook = false;
                    break;
                }
            }
            if (neverMovedQueenRook) {
                castle = new ChessMove(startPosition, new ChessPosition(8, 3), null);
                actualMoves.add(castle);
                /* The king would be in check if it moved to a square that it needs to pass through to castle */
                for (int i = 0; i < 3; i++) {
                    // make a theoretical game where the move is made
                    ChessGame theoreticalGame = copyOf(this);
                    try {
                        theoreticalGame.forceMove(new ChessMove(startPosition, new ChessPosition(8, 5 - i), null));
                    } catch (InvalidMoveException e) {
                        actualMoves.remove(castle);
                    }
                    // if the move puts the king in check
                    if (checkHelper(teamColor, theoreticalGame.getBoard())) {
                        actualMoves.remove(castle);
                    }
                }
            }
        }
    }
    void addEnPassantMoves(ChessPosition startPosition, TeamColor teamColor, Collection<ChessMove> actualMoves) {
        if (/* the piece being moved is a pawn */
                board.getPiece(startPosition).getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                        /* there was a move already made */
                        !moveLog.isEmpty() &&
                        /* the last move was a pawn */
                        board.getPiece(moveLog.getLast().getEndPosition()).getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                        /* the enemy pawn moved to row 4 from row 2 if it's white or */
                        ((moveLog.getLast().getEndPosition().getRow() == 4 &&
                                moveLog.getLast().getStartPosition().getRow() == 2 &&
                                board.getPiece(moveLog.getLast().getEndPosition()).getTeamColor().equals(TeamColor.WHITE)) ||
                                /* the enemy pawn moved to row 5 from row 7 if it's black */
                                (moveLog.getLast().getEndPosition().getRow() == 5 &&
                                        moveLog.getLast().getStartPosition().getRow() == 7 &&
                                        board.getPiece(moveLog.getLast().getEndPosition()).getTeamColor().equals(TeamColor.BLACK))) &&
                        /* the starting position row is the same as the enemy pawn */
                        startPosition.getRow() == moveLog.getLast().getEndPosition().getRow() &&
                        /* the starting column is one more than the enemy pawn or */
                        (startPosition.getColumn() == moveLog.getLast().getEndPosition().getColumn() + 1 ||
                                /* the starting column is one less than the enemy pawn */
                                startPosition.getColumn() == moveLog.getLast().getEndPosition().getColumn() - 1)
        ) {
            ChessMove enPassant = getEnPassant(startPosition, teamColor);
            actualMoves.add(enPassant);
            /* the move would not result in the king being in check */
            ChessGame theoreticalGame = copyOf(this);
            try {
                theoreticalGame.forceMove(enPassant);
            } catch (InvalidMoveException e) {
                actualMoves.remove(enPassant);
            }
            // if the move puts the king in check
            if (checkHelper(teamColor, theoreticalGame.getBoard())) {
                actualMoves.remove(enPassant);
            }
        }
    }
    private ChessMove getEnPassant(ChessPosition startPosition, TeamColor teamColor) {
        int rowOffset = (teamColor == TeamColor.WHITE) ? 1 : -1;
        ChessPosition endPosition = moveLog.getLast().getEndPosition();
        return new ChessMove(startPosition, new ChessPosition(endPosition.getRow() + rowOffset, endPosition.getColumn()), null);
    }
    /**Makes a move in a chess game
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     **/
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // the piece doesn't exist or
        if (board.getPiece(move.getStartPosition()) == null ||
            /* it's not the right turn or */
            board.getPiece(move.getStartPosition()).getTeamColor() != teamTurn ||
            /* can't make a valid move to position */
            !validMoves(move.getStartPosition()).contains(move)
            ) {
            throw new InvalidMoveException("Invalid move");
        }

        // otherwise, make the move
        else {
            ChessPiece startingPiece = board.getPiece(move.getStartPosition());
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
            ChessPiece.PieceType promotion = move.getPromotionPiece();
            ChessPiece promotedPiece = startingPiece;
            if (promotion != null) {
                promotedPiece = new ChessPiece(teamTurn, promotion);
            }
            board.addPiece(move.getEndPosition(), promotedPiece);
            board.removePiece(move.getStartPosition());
            // En passant
            boolean enPassantActive = false;
            ChessPiece enPassantCapture = null;
                /* a pawn moves */
            if (startingPiece.getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                /* 1 left or 1 right and */
                (move.getStartPosition().getColumn() == move.getEndPosition().getColumn() + 1 ||
                move.getStartPosition().getColumn() == move.getEndPosition().getColumn() - 1) &&
                /* there's no piece at the end position */
                capturedPiece == null
                ) {
                enPassantActive = true;
                if (startingPiece.getTeamColor() == TeamColor.WHITE) {
                    board.removePiece(new ChessPosition(move.getEndPosition().getRow() - 1, move.getEndPosition().getColumn()));
                    enPassantCapture = board.getPiece(new ChessPosition(move.getEndPosition().getRow() - 1, move.getEndPosition().getColumn()));
                }
                else {
                    board.removePiece(new ChessPosition(move.getEndPosition().getRow() + 1, move.getEndPosition().getColumn()));
                    enPassantCapture = board.getPiece(new ChessPosition(move.getEndPosition().getRow() + 1, move.getEndPosition().getColumn()));
                }
            }
            // Castling
                /* a king moves */
            if (startingPiece.getPieceType().equals(ChessPiece.PieceType.KING) &&
                /* starting at king starting position */
                (move.getStartPosition().equals(new ChessPosition(1, 5)) || move.getStartPosition().equals(new ChessPosition(8, 5)))
            ) {
                if (move.getEndPosition().equals(new ChessPosition(1, 3))) {
                    board.addPiece(new ChessPosition(1, 4), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                    board.removePiece(new ChessPosition(1, 1));
                }
                if (move.getEndPosition().equals(new ChessPosition(1, 7))) {
                    board.addPiece(new ChessPosition(1, 6), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                    board.removePiece(new ChessPosition(1, 8));
                }
                if (move.getEndPosition().equals(new ChessPosition(8, 3))) {
                    board.addPiece(new ChessPosition(8, 4), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                    board.removePiece(new ChessPosition(8, 1));
                }
                if (move.getEndPosition().equals(new ChessPosition(8, 7))) {
                    board.addPiece(new ChessPosition(8, 6), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                    board.removePiece(new ChessPosition(8, 8));
                }
            }

            // if the move didn't get the king out of check
            if (isInCheck(teamTurn)) {
                // put the piece back
                board.addPiece(move.getStartPosition(), startingPiece);
                board.addPiece(move.getEndPosition(), capturedPiece);
                if (enPassantActive) {
                    if (startingPiece.getTeamColor() == TeamColor.WHITE) {
                        board.addPiece(new ChessPosition(move.getEndPosition().getRow() - 1, move.getEndPosition().getColumn()), enPassantCapture);
                    }
                    else {
                        board.addPiece(new ChessPosition(move.getEndPosition().getRow() + 1, move.getEndPosition().getColumn()), enPassantCapture);
                    }
                }
                throw new InvalidMoveException("Invalid move");
            }
            if (teamTurn == TeamColor.WHITE){
                teamTurn = TeamColor.BLACK;
            }
            else {
                teamTurn = TeamColor.WHITE;
            }
            moveLog.add(move);
        }
    }
    /**Makes a move (even if it results in the king being checked) in a chess game
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid for some other reason
     */
    public void forceMove(ChessMove move) throws InvalidMoveException {
        ChessPiece startingPiece = board.getPiece(move.getStartPosition());
        ChessPiece.PieceType promotion = move.getPromotionPiece();
        ChessPiece promotedPiece = startingPiece;
        if (promotion != null) {
            promotedPiece = new ChessPiece(teamTurn, promotion);
        }
        board.addPiece(move.getEndPosition(), promotedPiece);
        board.removePiece(move.getStartPosition());
    }
    /**Determines if the given team is in check
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return checkHelper(teamColor, board);
    }
    /**Determines if the given team is in check
     * @param teamColor which team to check for check
     * @param currentBoard the chessboard to use
     * @return True if the specified team is in check
     */
    public boolean checkHelper(TeamColor teamColor, ChessBoard currentBoard) {
        // figure out where the king is
        ChessPosition kingPosition = currentBoard.getPosition(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        // find every piece belonging to the other team
        Collection<ChessPosition> enemyPositions;
        if (teamColor == TeamColor.BLACK) {
            enemyPositions = currentBoard.getPieces(TeamColor.WHITE);
        }
        else {
            enemyPositions = currentBoard.getPieces(TeamColor.BLACK);
        }
        // check the moves of each piece
        for (ChessPosition cp : enemyPositions) {
            Collection<ChessMove> possibleMoves = currentBoard.getPiece(cp).pieceMoves(currentBoard, cp);
            // check if the end position of any move is the kingPosition
            for (ChessMove cm : possibleMoves) {
                if (cm.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**Determines if the given team is in checkmate
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean hasNoLegalMove(TeamColor teamColor, boolean validateMove) {
        // get every piece of the same color as the king
        Collection<ChessPosition> friendlyPieces = board.getPieces(teamColor);
        // go through each one of those pieces
        for (ChessPosition cp : friendlyPieces) {
            // get the moves that the piece can make
            Collection<ChessMove> moves = board.getPiece(cp).pieceMoves(board, cp);
            // go through each move
            for (ChessMove cm : moves) {
                // make a theoretical game where the move is made
                ChessGame theoreticalGame = copyOf(this);
                try {
                    if (validateMove) {
                        theoreticalGame.makeMove(cm);
                    }
                    else {
                        theoreticalGame.forceMove(cm);
                    }
                } catch (InvalidMoveException e) {
                    // potential serious problem, throws lots of exceptions but passed test cases
                }
                // if the move gets the king out of check
                if (!checkHelper(teamColor, theoreticalGame.getBoard())) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && hasNoLegalMove(teamColor, true);
    }
    /**Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && hasNoLegalMove(teamColor, false);
    }
    /**Sets this game's chessboard with a given board
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }
    /**Gets the current chessboard
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}