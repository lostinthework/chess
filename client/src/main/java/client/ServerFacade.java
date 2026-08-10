package client;

import chess.ChessMove;
import com.google.gson.Gson;
import model.*;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private WebSocket webSocket;
    private WebSocketFacade webSocketFacade;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    private <T> T makeRequest(String method, String path, String authToken, Object requestClass, Class<T> classy) throws ResponseException {
        Gson gson = new Gson();
        try {
            String json = gson.toJson(requestClass);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + path))
                .header("Content-Type", "application/json");
                switch(method) {
                    case "GET" -> builder.GET();
                    case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(json));
                    case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.ofString(json));
                    case "DELETE" -> builder.method("DELETE", HttpRequest.BodyPublishers.ofString(json));
                }

                if (authToken != null) {
                    builder.header("Authorization", authToken);
                }
                HttpRequest request = builder.build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                if (classy == null) {
                    return null;
                }
                return gson.fromJson(response.body(), classy);
            }

            String message;
            try {
                message = gson.fromJson(response.body(), Map.class).get("message").toString();
            }
            catch (Exception e) {
                message = response.body();
            }

            switch (response.statusCode()) {
                case 400 -> throw new ResponseException(ResponseException.Code.BadRequest, message);
                case 401 -> throw new ResponseException(ResponseException.Code.Unauthorized, message);
                case 403 -> throw new ResponseException(ResponseException.Code.Forbidden, message);
                case 500 -> throw new ResponseException(ResponseException.Code.ServerError, message);
                default -> throw new ResponseException(ResponseException.Code.ServerError,
                                "Unexpected status: " + response.statusCode());
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
        catch (IOException | URISyntaxException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void connectWebSocket(String authToken, int gameID, Client client, boolean observer) throws ResponseException {
        try {
            webSocketFacade = new WebSocketFacade(authToken, gameID, client, observer);
            webSocket = HTTP_CLIENT.newWebSocketBuilder().buildAsync(URI.create(serverUrl.replace("http", "ws") + "/ws"), webSocketFacade).join();
        }
        catch (Exception e) {
            throw new ResponseException (ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        RegisterRequest request = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", null, request, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        LoginRequest request = new LoginRequest(username, password);
        return makeRequest("POST", "/session", null, request, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", authToken, null, null);
    }

    public List<GameData> listGames(String authToken) throws ResponseException {
        ListResult result = makeRequest("GET", "/game", authToken, null, ListResult.class);
        return result.getGames();
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        CreateRequest request = new CreateRequest(gameName);
        CreateResult result = makeRequest("POST", "/game", authToken, request, CreateResult.class);
        return result.getGameID();
    }

    public GameData joinGame(String authToken, int gameID, String color) throws ResponseException {
        JoinRequest request = new JoinRequest(gameID, color);
        return makeRequest("PUT", "/game", authToken, request, GameData.class);
    }

    public GameData move(String authToken, int gameID, ChessMove move) throws ResponseException {
        MoveRequest request = new MoveRequest(gameID, move);
        return makeRequest("PUT", "/game/move", authToken, request, GameData.class);
    }

    public void leave(String authToken, Integer currentGameID) throws ResponseException {
        LeaveRequest request = new LeaveRequest(currentGameID);
        makeRequest("DELETE", "/game/leave", authToken, request, null);
    }

    public void sendMove(ChessMove move) {
        if (webSocket != null && webSocketFacade != null) {
            webSocketFacade.sendMove(webSocket, move);
        }
    }

    public void leaveWebSocket() {
        if (webSocket != null && webSocketFacade != null) {
            webSocketFacade.sendLeave(webSocket);
        }
    }
}