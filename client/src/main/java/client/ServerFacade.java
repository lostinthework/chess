package client;

import com.google.gson.Gson;
import dataaccess.ResponseException;
import model.AuthData;
import model.GameData;
import sun.nio.ch.DatagramChannelImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    private <T> T makeRequest(String method, String path, String authToken, Object requestClass, Class<T> classy) throws ResponseException {
        Gson gson = new Gson();
        String json = gson.toJson(requestClass);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + path))
                .header("Content-Type", "application/json");
                switch(method) {
                    case "GET" -> builder.GET();
                    case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(json));
                    case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.ofString(json));
                    case "DELETE" -> builder.DELETE();
                }

                if (authToken != null) {
                    builder.header("Authorization", authToken);
                }
                HttpRequest request = builder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            if (classy == null) {
                return null;
            }
            return gson.fromJson(response.body(), classy);
        }
        throw new ResponseException(ResponseException.Code.ServerError, "Error");

    }

    public AuthData register(String username, String password, String email) throws URISyntaxException {
        RegisterRequest request = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", null, request, AuthData.class);
    }

    public AuthData login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
    }

    public void logout(String authToken) {

    }

    public List<GameData> listGames(String authToken) {

    }

    public int createGame(String authToken, String gameName) {
        CreateRequest request = new CreateRequest(gameName);
    }

    public void joinGame(String authToken, int gameID, String color) {
        JoinRequest request = new JoinRequest(gameID, color);
    }
}