package client;

import com.google.gson.Gson;
import dataaccess.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.awt.Color.BLUE;

public class Client {
    private final ServerFacade server;
    private String authToken;
//    private String cyan = "\u001b[36;1m";

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
                case "play" -> play();
                case "observe" -> observe();
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
            AuthData auth = server.login(params[0], params[1]);
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
            List<GameData> games = server.listGames(authToken);
            var result = new StringBuilder();
            result.append("Games:\n");
            for (GameData game : games) {
                String whitename = "";
                String blackname = "";
                if (game.getWhiteUsername() != null) {
                    whitename = game.getWhiteUsername();
                }
                if (game.getBlackUsername() != null) {
                    blackname = game.getBlackUsername();
                }
                String prettyprint = String.format("---------------------\n" +
                                                   "Name:          %s\n" +
                                                   "Game ID:       %d\n" +
                                                   "White player:  %s\n" +
                                                   "Black player:  %s\n\n",
                                                   game.getName(),
                                                   game.getGameID(),
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
                return String.format("Created game %s with game ID %d.\n", params[0], gameID);
            }
            throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <name>");
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }

    public String joinGame(String... params) throws ResponseException {
        if (authToken != null) {
            if (params.length == 2) {
                int gameID;
                try {
                    gameID = Integer.parseInt(params[0]);
                }
                catch (NumberFormatException e) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Invalid game ID.");
                }
                if (!params[1].equals("white") && !params[1].equals("black")) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Invalid color.");
                }
                List<GameData> games = server.listGames(authToken);
                for (GameData game : games) {
                    if (game.getGameID() == gameID) {
                        if ((params[1].equals("white") && game.getWhiteUsername() != null) ||
                            (params[1].equals("black") && game.getBlackUsername() != null)) {
                            throw new ResponseException(ResponseException.Code.BadRequest, "Color already taken.");
                        }
                        server.joinGame(authToken, gameID, params[1].toUpperCase());
                        return String.format("Joined game %s as %s.\n", game.getName(), params[1]);
                    }
                }
                throw new ResponseException(ResponseException.Code.BadRequest, "Invalid game ID.");
            }
            throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <game ID> <white|black>");
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "You are not logged in.");
    }

    public String play() throws ResponseException {

    }

    public String observe() throws ResponseException {

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
                - join <game ID> <white|black>
                - quit
                """;
    }
}
