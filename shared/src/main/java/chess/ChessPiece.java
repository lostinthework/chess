package chess;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }
    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
    @Override
    public String toString() {
        return pieceColor + " " + type;
    }
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }
    public PieceType getPieceType() {
        return type;
    }
    // Returns all the positions a chess piece can move to without taking into account illegal moves that leave the king in danger
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case KING:
                moves = kingMoves(board, myPosition);
                break;
            case QUEEN:
                moves = rookMoves(board, myPosition);
                moves.addAll(bishopMoves(board, myPosition));
                break;
            case BISHOP:
                moves = bishopMoves(board, myPosition);
                break;
            case KNIGHT:
                moves = knightMoves(board, myPosition);
                break;
            case ROOK:
                moves = rookMoves(board, myPosition);
                break;
            case PAWN:
                if (this.pieceColor == ChessGame.TeamColor.WHITE) {
                    moves = whitePawnMoves(board, myPosition);
                }
                else {
                    moves = blackPawnMoves(board, myPosition);
                }
        }
        return moves;
    }
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        if (row < 8 && column > 1) { /* Moving up and to the left */
            row++;
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row < 8 && column < 8) { /* Moving up and to the right */
            row++;
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row > 1 && column > 1) { /* Moving down and to the left */
            row--;
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row > 1 && column < 8) { /* Moving down and to the right */
            row--;
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row < 8) { /* Moving up */
            row++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        if (column < 8) { /* Moving right */
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        column = myPosition.getColumn();
        if (column > 1) { /* Moving left */
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        column = myPosition.getColumn();
        if (row > 1) { /* Moving down */
            row--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        return moves;
    }
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        while (row < 8 && column > 1) { /* Moving up and to the left */
            row++;
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                moveHelper(blockingPiece, row, column, moves, myPosition);
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        while (row < 8 && column < 8) { /* Moving up and to the right */
            row++;
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                moveHelper(blockingPiece, row, column, moves, myPosition);
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        while (row > 1 && column > 1) { /* Moving down and to the left */
            row--;
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                moveHelper(blockingPiece, row, column, moves, myPosition);
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        while (row > 1 && column < 8) { /* Moving down and to the right */
            row--;
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                moveHelper(blockingPiece, row, column, moves, myPosition);
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        return moves;
    }
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        if (row < 7 && column > 1) { /* Moving 2 up 1 left */
            row += 2;
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row < 8 && column > 2) { /* Moving 2 left 1 up */
            row++;
            column -= 2;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row > 1 && column > 2) { /* Moving 2 left 1 down */
            row--;
            column -= 2;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row > 2 && column > 1) { /* Moving 2 down 1 left */
            row -= 2;
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row > 2 && column < 8) { /* Moving 2 down 1 right */
            row -= 2;
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row > 1 && column < 7) { /* Moving 2 right 1 down */
            row--;
            column += 2;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row < 8 && column < 7) { /* Moving 2 right 1 up */
            row++;
            column += 2;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row < 7 && column < 8) { /* Moving 2 up 1 right */
            row += 2;
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        return moves;
    }
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        while (row < 8) { /* Moving up */
            row++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                moveHelper(blockingPiece, row, column, moves, myPosition);
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        while (column < 8) { /* Moving right */
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                moveHelper(blockingPiece, row, column, moves, myPosition);
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        column = myPosition.getColumn();
        while (column > 1) { /* Moving left */
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                moveHelper(blockingPiece, row, column, moves, myPosition);
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        column = myPosition.getColumn();
        while (row > 1) { /* Moving down */
            row--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                moveHelper(blockingPiece, row, column, moves, myPosition);
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        return moves;
    }
    private Collection<ChessMove> whitePawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        if (row < 8) { /* Moving forward 1 space */
            row++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            if (blockingPiece == null) { /* There's nothing blocking the way */
                if (row < 8) { /* no promotion */
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                else { /* with promotion */
                    addPawnMoves(moves, myPosition, row, column);
                }
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (row == 2) { /* Moving forward 2 spaces */
            row += 2;
            ChessPiece blockingPiece1 = board.getPiece(new ChessPosition(row - 1, column));
            ChessPiece blockingPiece2 = board.getPiece(new ChessPosition(row, column));
            if (blockingPiece1 == null && blockingPiece2 == null) { /* There's nothing blocking the way */
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (column > 1) { /* Moving forward and left */
            row++;
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece of the opposite color blocking the way
            if (blockingPiece != null && blockingPiece.getTeamColor() != this.pieceColor) {
                if (row < 8) { /* No promotion */
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Promotion
                else {
                    addPawnMoves(moves, myPosition, row, column);
                }
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if (column < 8) { /* Moving forward and right */
            row++;
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece of the opposite color blocking the way
            if (blockingPiece != null && blockingPiece.getTeamColor() != this.pieceColor) {
                if (row < 8) { /* No promotion */
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                else { /* Promotion */
                    addPawnMoves(moves, myPosition, row, column);
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> blackPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        // Moving down 1 space
        if (row > 1) {
            row--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way
            if (blockingPiece == null) {
                // no promotion
                if (row > 1) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // with promotion
                else {
                    addPawnMoves(moves, myPosition, row, column);
                }
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving down 2 spaces
        if (row == 7) {
            row -= 2;
            ChessPiece blockingPiece1 = board.getPiece(new ChessPosition(row + 1, column));
            ChessPiece blockingPiece2 = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way
            if (blockingPiece1 == null && blockingPiece2 == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving down and left
        if (column > 1) {
            row--;
            column--;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece of the opposite color blocking the way
            if (blockingPiece != null && blockingPiece.getTeamColor() != this.pieceColor) {
                // No promotion
                if (row > 1) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Promotion
                else {
                    addPawnMoves(moves, myPosition, row, column);                }
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving down and right
        if (column < 8) {
            row--;
            column++;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece of the opposite color blocking the way
            if (blockingPiece != null && blockingPiece.getTeamColor() != this.pieceColor) {
                // No promotion
                if (row > 1) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Promotion
                else {
                    addPawnMoves(moves, myPosition, row, column);
                }
            }
        }
        return moves;
    }
    void moveHelper(ChessPiece blockingPiece, int row, int column, Collection<ChessMove> moves, ChessPosition myPosition) {
        if (blockingPiece.getTeamColor() != this.pieceColor) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
    }
    void addPawnMoves(Collection<ChessMove> moves, ChessPosition myPosition, int row, int column) {
        moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.ROOK));
        moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.QUEEN));
    }
}