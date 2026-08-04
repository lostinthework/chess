package client;

import model.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.*;

public class Client {
    private final ServerFacade server;
    private final Map<Integer, Integer> gameNumberToID = new HashMap<>();
    private String authToken;
    private static final String W = "\u001b[97;47;1m";
    private static final String B = "\u001b[97;100;1m";
    private static final String R = "\u001b[0m";

    public Client(ServerFacade server) {
        this.server = server;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
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
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage() + '\n';
        }
    }

    public String register(String... params) throws ResponseException {
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
        if (authToken != null) {
            server.logout(authToken);
            authToken = null;
            return "You logged out.\n";
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }

    public String listGames() throws ResponseException {
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
        if (authToken != null) {
            if (params.length == 1) {
                int gameID = server.createGame(authToken, params[0]);
                return String.format("Created game %s.\n", params[0]);
            }
            throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <name>");
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }

    private String drawBoard(String color) {
        if (color.equals("white")) {
            return """
                W    a  b  c  d  e  f  g  h    R
                W 8  ♜ RB ♞ RW ♝ RB ♛ RW ♚ RB ♝ RW ♞ RB ♜ RW 8 R
                W 7 RB ♟ RW ♟ RB ♟ RW ♟ RB ♟ RW ♟ RB ♟ RW ♟  7 R
                W 6    RB   RW   RB   RW   RB   RW   RB   RW 6 R
                W 5 RB   RW   RB   RW   RB   RW   RB   RW    5 R
                W 4    RB   RW   RB   RW   RB   RW   RB   RW 4 R
                W 3 RB   RW   RB   RW   RB   RW   RB   RW    3 R
                W 2  ♙ RB ♙ RW ♙ RB ♙ RW ♙ RB ♙ RW ♙ RB ♙ RW 2 R
                W 1 RB ♖ RW ♘ RB ♗ RW ♕ RB ♔ RW ♗ RB ♘ RW ♖  1 R
                W    a  b  c  d  e  f  g  h    R
                """;
        }
        else {
            return """
                W    h  g  f  e  d  c  b  a    R
                W 1 RW ♖ RB ♘ RW ♗ RB ♔ RW ♕ RB ♗ RW ♘ RB ♖ RW 1 R
                W 2 RB ♙ RW ♙ RB ♙ RW ♙ RB ♙ RW ♙ RB ♙ RW ♙ RW 2 R
                W 3    RB   RW   RB   RW   RB   RW   RB   RW 3 R
                W 4 RB   RW   RB   RW   RB   RW   RB   RW    4 R
                W 5    RB   RW   RB   RW   RB   RW   RB   RW 5 R
                W 6 RB   RW   RB   RW   RB   RW   RB   RW    6 R
                W 7 RW ♟ RB ♟ RW ♟ RB ♟ RW ♟ RB ♟ RW ♟ RB ♟ RW 7 R
                W 8 RB ♜ RW ♞ RB ♝ RW ♚ RB ♛ RW ♝ RB ♞ RW ♜ RW 8 R
                W    h  g  f  e  d  c  b  a    R
                """;
        }
    }

    public String joinGame(String... params) throws ResponseException {
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
                        server.joinGame(authToken, gameID, params[1].toUpperCase());
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
        if (authToken != null) {
            if (params.length == 1) {
                return drawBoard("white");
            }
            throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <game>");
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }


    public String help() {
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
}
