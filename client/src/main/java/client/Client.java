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

    public Client(ServerFacade server) {
        this.server = server;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            System.out.print("> ");
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(BLUE + result);
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
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            try {
                AuthData auth = server.register(params[0], params[1], params[2]);
            }
            catch (/* something */) {
                ResponseException(ResponseException.Code.ClientError, "Username already taken.");
            }
            authToken = auth.getauthToken();

            return String.format("You logged in as %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            AuthData auth = server.login(params[0], params[1]);
            authToken = auth.getauthToken();
            return String.format("You logged in as %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    public String logout() throws ResponseException {
        if (authToken != null) {
            server.logout(authToken);
            authToken = null;
            return "You logged out.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "You are not logged in.");
    }

    public String listGames() throws ResponseException {
        if (authToken != null) {
            List<GameData> games = server.listGames(authToken);
            var result = new StringBuilder();
            var gson = new Gson();
            for (GameData game : games) {
                result.append(gson.toJson(game)).append('\n');
            }
            return result.toString();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "You are not logged in.");
    }

    public String createGame(String... params) throws ResponseException {
        if (authToken != null) {
            if (params.length == 1) {
                int gameID = server.createGame(authToken, params[0]);
                return String.format("Created game %s with game ID %d.", params[0], gameID);
            }
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <name>");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "You are not logged in.");
    }

    public String joinGame(String... params) throws ResponseException {
        if (authToken != null) {
            if (params.length == 2) {
                int gameID;
                try {
                    gameID = Integer.parseInt(params[0]);
                }
                catch (NumberFormatException e) {
                    throw new ResponseException(ResponseException.Code.ClientError, "Invalid game ID.");
                }
                if (!params[1].equals("white") && !params[1].equals("black")) {
                    throw new ResponseException(ResponseException.Code.ClientError, "Invalid color.");
                }
                List<GameData> games = server.listGames(authToken);
                for (GameData game : games) {
                    if (game.getGameID() == gameID) {
                        if ((params[1].equals("white") && game.getWhiteUsername() != null) ||
                            (params[1].equals("black") && game.getBlackUsername() != null)) {
                            throw new ResponseException(ResponseException.Code.ClientError, "Color already taken.");
                        }
                        server.joinGame(authToken, gameID, params[1].toUpperCase());
                        return String.format("Joined game %s as %s.", game.getName(), params[1]);
                    }
                }
                throw new ResponseException(ResponseException.Code.ClientError, "Invalid game ID.");
            }
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <game ID> <white|black>");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "You are not logged in.");
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
