package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.ResponseException;
import model.AuthData;
import model.GameData;
import websocket.messages.Notification;

import java.util.*;

public class Client {
    private final ServerFacade server;
    private final Map<Integer, Integer> gameNumberToID = new HashMap<>();
    private String authToken;
    private static final String FW = "\u001b[97;1m";
    private static final String FB = "\u001b[30;1m";
    private static final String BW = "\u001b[47;1m";
    private static final String BB = "\u001b[100;1m";
    private static final String R = "\u001b[0m";

    private final Scanner scanner = new Scanner(System.in);
    private boolean observer = false;
    private String color = null;
    private Integer currentGameID = null;
    private ChessGame currentGame = null;

    public Client(ServerFacade server) {
        this.server = server;
    }

    public void run() {
        String result = "";
        System.out.print("\uD83D\uDC51 Welcome to 240 chess. Type Help to get started. \uD83D\uDC51\n");
        while (!result.equals("quit")) {
            System.out.print(">>> ");
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(result);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split("\\s+");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "list" -> listGames();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observe(params);
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage() + '\n';
        }
    }

    public String register(String... params) throws ResponseException {
        if (currentGameID != null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must leave the game before registering a new user.");
        }
        if (params.length == 3) {
            AuthData auth;
            try {
                auth = server.register(params[0], params[1], params[2]);
            }
            catch (ResponseException e) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Username already taken.");
            }
            authToken = auth.getauthToken();

            return String.format("You logged in as %s.\n", params[0]);
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (authToken != null) {
            throw new ResponseException(ResponseException.Code.BadRequest, String.format("You must log out before you can log in again."));
        }
        if (params.length == 2) {
            AuthData auth;
            try {
                auth = server.login(params[0], params[1]);
            }
            catch (ResponseException e) {
                throw new ResponseException(ResponseException.Code.BadRequest, String.format("Incorrect username or password."));
            }
            authToken = auth.getauthToken();
            return String.format("You logged in as %s.\n", params[0]);
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <username> <password>");
    }

    public String logout() throws ResponseException {
        if (currentGameID != null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must leave the game before logging out.");
        }
        if (authToken != null) {
            server.logout(authToken);
            authToken = null;
            return "You logged out.\n";
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }

    public String listGames() throws ResponseException {
        if (currentGameID != null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must leave the current game before listing games.");
        }
        if (authToken != null) {
            gameNumberToID.clear();
            List<GameData> games = server.listGames(authToken);
            var result = new StringBuilder();
            result.append("Games:\n");
            int gameNumber = 0;
            for (GameData game : games) {
                gameNumber++;
                gameNumberToID.put(gameNumber, game.getGameID());
                String whitename = "";
                String blackname = "";
                if (game.getWhiteUsername() != null) {
                    whitename = game.getWhiteUsername();
                }
                if (game.getBlackUsername() != null) {
                    blackname = game.getBlackUsername();
                }
                String prettyprint = String.format("---------------------\n" +
                                                   ">>> Game %d <<<\n" +
                                                   "Name:          %s\n" +
                                                   "White player:  %s\n" +
                                                   "Black player:  %s\n\n",
                                                   gameNumber,
                                                   game.getName(),
                                                   whitename,
                                                   blackname);
                result.append(prettyprint);
            }
            return result.toString();
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }

    public String createGame(String... params) throws ResponseException {
        if (currentGameID != null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must leave the current game before creating a new game.");
        }
        if (authToken != null) {
            if (params.length == 1) {
                int gameID = server.createGame(authToken, params[0]);
                return String.format("Created game %s.\n", params[0]);
            }
            throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <name>");
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }

    private String piece(int row, int col) {
        ChessPiece piece = currentGame.getBoard().getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return "   ";
        }
        String character = switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? FW + " ♔ " : FB + " ♚ ";
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? FW + " ♕ " : FB + " ♛ ";
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? FW + " ♖ " : FB + " ♜ ";
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? FW + " ♗ " : FB + " ♝ ";
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? FW + " ♘ " : FB + " ♞ ";
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? FW + " ♙ " : FB + " ♟ ";
        };
        return character + R;
    }

    private String drawBoard(String color) {
        if (color.equals("white")) {
            return
                FW + BW + "    a  b  c  d  e  f  g  h    " + R + "\n" +
                FW + BW + " 8 " + R + BW + piece(8, 1) + BB + piece(8, 2) + BW + piece(8, 3) + BB + piece(8, 4) + BW + piece(8, 5) + BB + piece(8, 6) + BW + piece(8, 7) + BB + piece(8, 8) + FW + BW + " 8 " + R + "\n" +
                FW + BW + " 7 " + R + BB + piece(7, 1) + BW + piece(7, 2) + BB + piece(7, 3) + BW + piece(7, 4) + BB + piece(7, 5) + BW + piece(7, 6) + BB + piece(7, 7) + BW + piece(7, 8) + FW + BW + " 7 " + R + "\n" +
                FW + BW + " 6 " + R + BW + piece(6, 1) + BB + piece(6, 2) + BW + piece(6, 3) + BB + piece(6, 4) + BW + piece(6, 5) + BB + piece(6, 6) + BW + piece(6, 7) + BB + piece(6, 8) + FW + BW + " 6 " + R + "\n" +
                FW + BW + " 5 " + R + BB + piece(5, 1) + BW + piece(5, 2) + BB + piece(5, 3) + BW + piece(5, 4) + BB + piece(5, 5) + BW + piece(5, 6) + BB + piece(5, 7) + BW + piece(5, 8) + FW + BW + " 5 " + R + "\n" +
                FW + BW + " 4 " + R + BW + piece(4, 1) + BB + piece(4, 2) + BW + piece(4, 3) + BB + piece(4, 4) + BW + piece(4, 5) + BB + piece(4, 6) + BW + piece(4, 7) + BB + piece(4, 8) + FW + BW + " 4 " + R + "\n" +
                FW + BW + " 3 " + R + BB + piece(3, 1) + BW + piece(3, 2) + BB + piece(3, 3) + BW + piece(3, 4) + BB + piece(3, 5) + BW + piece(3, 6) + BB + piece(3, 7) + BW + piece(3, 8) + FW + BW + " 3 " + R + "\n" +
                FW + BW + " 2 " + R + BW + piece(2, 1) + BB + piece(2, 2) + BW + piece(2, 3) + BB + piece(2, 4) + BW + piece(2, 5) + BB + piece(2, 6) + BW + piece(2, 7) + BB + piece(2, 8) + FW + BW + " 2 " + R + "\n" +
                FW + BW + " 1 " + R + BB + piece(1, 1) + BW + piece(1, 2) + BB + piece(1, 3) + BW + piece(1, 4) + BB + piece(1, 5) + BW + piece(1, 6) + BB + piece(1, 7) + BW + piece(1, 8) + FW + BW + " 1 " + R + "\n" +
                FW + BW + "    a  b  c  d  e  f  g  h    " + R + "\n";
        }
        else {
            return
                FB + BB + "    h  g  f  e  d  c  b  a    " + R + "\n" +
                FB + BB + " 1 " + R + BW + piece(1, 8) + BB + piece(1, 7) + BW + piece(1, 6) + BB + piece(1, 5) + BW + piece(1, 4) + BB + piece(1, 3) + BW + piece(1, 2) + BB + piece(1, 1) + FB + BB + " 1 " + R + "\n" +
                FB + BB + " 2 " + R + BB + piece(2, 8) + BW + piece(2, 7) + BB + piece(2, 6) + BW + piece(2, 5) + BB + piece(2, 4) + BW + piece(2, 3) + BB + piece(2, 2) + BW + piece(2, 1) + FB + BB + " 2 " + R + "\n" +
                FB + BB + " 3 " + R + BW + piece(3, 8) + BB + piece(3, 7) + BW + piece(3, 6) + BB + piece(3, 5) + BW + piece(3, 4) + BB + piece(3, 3) + BW + piece(3, 2) + BB + piece(3, 1) + FB + BB + " 3 " + R + "\n" +
                FB + BB + " 4 " + R + BB + piece(4, 8) + BW + piece(4, 7) + BB + piece(4, 6) + BW + piece(4, 5) + BB + piece(4, 4) + BW + piece(4, 3) + BB + piece(4, 2) + BW + piece(4, 1) + FB + BB + " 4 " + R + "\n" +
                FB + BB + " 5 " + R + BW + piece(5, 8) + BB + piece(5, 7) + BW + piece(5, 6) + BB + piece(5, 5) + BW + piece(5, 4) + BB + piece(5, 3) + BW + piece(5, 2) + BB + piece(5, 1) + FB + BB + " 5 " + R + "\n" +
                FB + BB + " 6 " + R + BB + piece(6, 8) + BW + piece(6, 7) + BB + piece(6, 6) + BW + piece(6, 5) + BB + piece(6, 4) + BW + piece(6, 3) + BB + piece(6, 2) + BW + piece(6, 1) + FB + BB + " 6 " + R + "\n" +
                FB + BB + " 7 " + R + BW + piece(7, 8) + BB + piece(7, 7) + BW + piece(7, 6) + BB + piece(7, 5) + BW + piece(7, 4) + BB + piece(7, 3) + BW + piece(7, 2) + BB + piece(7, 1) + FB + BB + " 7 " + R + "\n" +
                FB + BB + " 8 " + R + BB + piece(8, 8) + BW + piece(8, 7) + BB + piece(8, 6) + BW + piece(8, 5) + BB + piece(8, 4) + BW + piece(8, 3) + BB + piece(8, 2) + BW + piece(8, 1) + FB + BB + " 8 " + R + "\n" +
                FB + BB + "    h  g  f  e  d  c  b  a    " + R + "\n";
        }
    }

    public String joinGame(String... params) throws ResponseException {
        if (currentGameID != null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must leave the current game before joining a new game.");
        }
        if (authToken != null) {
            if (params.length == 2) {
                Integer gameID;
                try {
                    gameID = gameNumberToID.get(Integer.parseInt(params[0]));
                }
                catch (NumberFormatException e) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Invalid game number.");
                }
                if (gameID == null) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "You must list games before joining one.");
                }
                if (!params[1].equals("white") && !params[1].equals("black")) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Invalid color.");
                }
                List<GameData> games = server.listGames(authToken);
                for (GameData game : games) {
                    if (game.getGameID() == gameID &&
                        ((params[1].equals("white") && game.getWhiteUsername() != null) ||
                        (params[1].equals("black") && game.getBlackUsername() != null))) {
                        throw new ResponseException(ResponseException.Code.BadRequest, "Color already taken.");
                    }
                    if (game.getGameID() == gameID) {
                        GameData joinedGame = server.joinGame(authToken, gameID, params[1].toUpperCase());
                        currentGameID = game.getGameID();
                        currentGame = joinedGame.getGame();
                        color = params[1];
                        server.connectWebSocket(authToken, currentGameID, this, observer);
                        return drawBoard(params[1]);
                    }
                }
                throw new ResponseException(ResponseException.Code.BadRequest, "Invalid game number.");
            }
            throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <game> <white|black>");
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }

    // Make sure to fix this before phase 6 so that it actually checks to see if the game exists!
    public String observe(String... params) throws ResponseException {
        if (currentGameID != null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must leave the current game before observing a new game.");
        }
        if (authToken != null) {
            if (params.length == 1) {
                Integer gameID;
                try {
                    gameID = gameNumberToID.get(Integer.parseInt(params[0]));
                }
                catch (NumberFormatException e) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Invalid game number.");
                }
                if (gameID == null) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Invalid game number.");
                }
                List<GameData> games = server.listGames(authToken);
                for (GameData game : games) {
                    if (game.getGameID() == gameID) {
                        currentGameID = game.getGameID();
                        currentGame = game.getGame();
                        observer = true;
                        color = "white";
                        server.connectWebSocket(authToken, currentGameID, this, observer);
                        return drawBoard(color);
                    }
                }
            }
            throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <game>");
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }

    public String help() {
        if (currentGameID != null) {
            if (observer) {
                return """
                        - redraw
                        - leave
                        - highlight <piece>
                        """;
            }
            else {
                return """
                    - redraw
                    - leave
                    - move <piece> <square>
                    - resign
                    - highlight <piece>
                    """;
            }
        }
        if (authToken == null) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """
                - logout
                - list
                - create <name>
                - join <game> <white|black>
                - observe <game>
                - quit
                """;
    }

    public void notify(Notification notification) {
        System.out.println(notification.getMessage());
    }

    public String redraw() throws ResponseException {
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
        }
        if (currentGameID == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must join a game before redrawing the board.");
        }
        return drawBoard(color);
    }

    public String leave() throws ResponseException {
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
        }
        if (currentGameID == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must join or observe a game before leaving it.");
        }

        server.leaveWebSocket();
//        server.leave(authToken, currentGameID);
        currentGameID = null;
        currentGame = null;
        color = null;
        observer = false;

        return "You left the game.\n";
    }

    public void updateGame(GameData game) {
        ChessGame board = game.getGame();
        currentGame = board;
        System.out.print("\n" + drawBoard(color));
    }

    private ChessPosition notationToPosition (char file, int rank) throws ResponseException {
        if (rank < 1 || rank > 8) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <piece> <square>");
        }
        int col = switch (file) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <piece> <square>");
        };
        return new ChessPosition(rank, col);
    }

    private ChessMove notationToMove (String startPosition, String endPosition, String color) throws ResponseException {
        ChessPosition start = notationToPosition(startPosition.charAt(0), startPosition.charAt(1) - '0');
        ChessPosition end = notationToPosition(endPosition.charAt(0), endPosition.charAt(1) - '0');
        ChessPiece.PieceType promotionPiece = null;
        if ((color.equals("white") && end.getRow() == 8) || (color.equals("black") && end.getRow() == 1)) {
            System.out.print("Promote to which piece (queen, rook, bishop, knight): ");
            String input = scanner.nextLine().trim().toLowerCase();
            promotionPiece = switch (input) {
                case "queen" -> ChessPiece.PieceType.QUEEN;
                case "rook" -> ChessPiece.PieceType.ROOK;
                case "bishop" -> ChessPiece.PieceType.BISHOP;
                case "knight" -> ChessPiece.PieceType.KNIGHT;
                default -> throw new ResponseException(ResponseException.Code.BadRequest, "Invalid promotion piece.");
            };
        }
        return new ChessMove(start, end, promotionPiece);
    }

    public String move(String... params) throws ResponseException {
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
        }
        if (currentGameID == null || observer) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must join a game before making a move.");
        }
        if (params.length == 2 && params[0].length() == 2 && params[1].length() == 2) {
//            try {
//                currentGame = server.move(authToken, currentGameID, notationToMove(params[0], params[1], color)).getGame();
//            }
//            catch (Exception e) {
//                throw new ResponseException(ResponseException.Code.BadRequest, e.getMessage());
//            }
//            return drawBoard(color);

            ChessMove move = notationToMove(params[0], params[1], color);
            server.sendMove(move);

            return "";
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <piece> <square>");
    }

    public String resign() throws ResponseException {
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
        }
        if (currentGameID == null || observer) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must join a game before resigning.");
        }
        return "";
    }

    public String highlight(String... params) throws ResponseException {
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
        }
        if (currentGameID == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "You must join or observe a game before highlighting.");
        }
        if (params.length == 1) {
            return "";
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <piece>");
    }
}
