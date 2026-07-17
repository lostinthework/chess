package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;
// import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
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

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        // ArrayList<Integer> moves = new int[][]{};
        switch (type) {

            case KING:
                moves = kingMoves(board, myPosition);
                break;

            case QUEEN:
                moves = queenMoves(board, myPosition);
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
                // pawn is white
                if (this.pieceColor == ChessGame.TeamColor.WHITE) {
                    moves = whitePawnMoves(board, myPosition);
                }
                // pawn is black
                else {
                    moves = blackPawnMoves(board, myPosition);
                }
                break;
        }
        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        // Moving up and to the left
        if (row < 8 && column > 1) {
            row += 1;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving up and to the right
        if (row < 8 && column < 8) {
            row += 1;
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving down and to the left
        if (row > 1 && column > 1) {
            row -= 1;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving down and to the right
        if (row > 1 && column < 8) {
            row -= 1;
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving up
        if (row < 8) {
            row += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        // Moving right
        if (column < 8) {
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        column = myPosition.getColumn();
        // Moving left
        if (column > 1) {
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        column = myPosition.getColumn();
        // Moving down
        if (row > 1) {
            row -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        // Moving up and to the left
        while (row < 8 && column > 1) {
            row += 1;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving up and to the right
        while (row < 8 && column < 8) {
            row += 1;
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving down and to the left
        while (row > 1 && column > 1) {
            row -= 1;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving down and to the right
        while (row > 1 && column < 8) {
            row -= 1;
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving up
        while (row < 8) {
            row += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        // Moving right
        while (column < 8) {
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        column = myPosition.getColumn();
        // Moving left
        while (column > 1) {
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        column = myPosition.getColumn();
        // Moving down
        while (row > 1) {
            row -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        // Moving up and to the left
        while (row < 8 && column > 1) {
            row += 1;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving up and to the right
        while (row < 8 && column < 8) {
            row += 1;
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving down and to the left
        while (row > 1 && column > 1) {
            row -= 1;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // Moving down and to the right
        while (row > 1 && column < 8) {
            row -= 1;
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
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
        // Moving 2 up 1 left
        if (row < 7 && column > 1) {
            row += 2;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving 2 left 1 up
        if (row < 8 && column > 2) {
            row += 1;
            column -= 2;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving 2 left 1 down
        if (row > 1 && column > 2) {
            row -= 1;
            column -= 2;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving 2 down 1 left
        if (row > 2 && column > 1) {
            row -= 2;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving 2 down 1 right
        if (row > 2 && column < 8) {
            row -= 2;
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving 2 right 1 down
        if (row > 1 && column < 7) {
            row -= 1;
            column += 2;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving 2 right 1 up
        if (row < 8 && column < 7) {
            row += 1;
            column += 2;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way or an enemy piece is blocking the way
            if (blockingPiece == null || blockingPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving 2 up 1 right
        if (row < 7 && column < 8) {
            row += 2;
            column += 1;
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
        // Moving up
        while (row < 8) {
            row += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        row = myPosition.getRow();
        // Moving right
        while (column < 8) {
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        column = myPosition.getColumn();
        // Moving left
        while (column > 1) {
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
                break;
            }
            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
        }
        column = myPosition.getColumn();
        // Moving down
        while (row > 1) {
            row -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece blocking the way
            if (blockingPiece != null) {
                // The piece is of the opposite color
                if (blockingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Can't go any further
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
        // Moving forward 1 space
        if (row < 8) {
            row += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way
            if (blockingPiece == null) {
                // no promotion
                if (row < 8) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // with promotion
                else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.QUEEN));
                }
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving forward 2 spaces
        if (row == 2) {
            row += 2;
            ChessPiece blockingPiece1 = board.getPiece(new ChessPosition(row - 1, column));
            ChessPiece blockingPiece2 = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way
            if (blockingPiece1 == null && blockingPiece2 == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving forward and left
        if (column > 1) {
            row += 1;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece of the opposite color blocking the way
            if (blockingPiece != null && blockingPiece.getTeamColor() != this.pieceColor) {
                // No promotion
                if (row < 8) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Promotion
                else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.QUEEN));
                }
            }
        }

        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving forward and right
        if (column < 8) {
            row += 1;
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece of the opposite color blocking the way
            if (blockingPiece != null && blockingPiece.getTeamColor() != this.pieceColor) {
                // No promotion
                if (row < 8) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Promotion
                else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.QUEEN));
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
            row -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's nothing blocking the way
            if (blockingPiece == null) {
                // no promotion
                if (row > 1) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // with promotion
                else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.QUEEN));
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
            row -= 1;
            column -= 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece of the opposite color blocking the way
            if (blockingPiece != null && blockingPiece.getTeamColor() != this.pieceColor) {
                // No promotion
                if (row > 1) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Promotion
                else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.QUEEN));
                }
            }
        }

        row = myPosition.getRow();
        column = myPosition.getColumn();

        // Moving down and right
        if (column < 8) {
            row -= 1;
            column += 1;
            ChessPiece blockingPiece = board.getPiece(new ChessPosition(row, column));
            // There's a piece of the opposite color blocking the way
            if (blockingPiece != null && blockingPiece.getTeamColor() != this.pieceColor) {
                // No promotion
                if (row > 1) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                }
                // Promotion
                else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.QUEEN));
                }
            }
        }
        return moves;
    }
}