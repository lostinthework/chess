package handler;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebSocketHandler {

    private final GameService gameService;
    private final Map<Integer, Set<WsContext>> connections;

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
        this.connections = new HashMap<>();
        this.connectionGames = new HashMap<>();
    }

    public void onConnect(WsContext ctx) {
        System.out.println("WebSocket connected");
    }

    public void onMessage(WsMessageContext ctx) {
        System.out.println("WebSocket message received: " + ctx.message());
        Gson gson = new Gson();
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        System.out.println("Command: " + command.getCommandType());
        System.out.println("Game ID: " + command.getGameID());
        System.out.println("Auth token: " + command.getAuthToken());
        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            try {
                if (gameService.getAuth(command.getAuthToken()) == null) {
                    ctx.send(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    return;
                }
                GameData game = gameService.getGame(command.getGameID());
                if (game == null) {
                    ctx.send(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    return;
                }
                connections.computeIfAbsent(command.getGameID(), id -> new HashSet<>()).add(ctx);
                String username = gameService.getUsername(command.getAuthToken());

                String color;
                if (username.equals(game.getWhiteUsername())) {
                    color = "white";
                } else if (username.equals(game.getBlackUsername())) {
                    color = "black";
                } else {
                    color = "an observer";
                }
                Notification notification = new Notification(username + " joined the game as " + color + ".");
                String json = new Gson().toJson(notification);
                for (WsContext connection : connections.get(command.getGameID())) {
                    connection.send(json);
                }
            }
            catch (Exception e) {
                System.out.println("WebSocket error: " + e.getMessage());
            }
        }
    }

    public void onClose(WsContext ctx) {
        System.out.println("WebSocket closed");
    }

    public void onError(WsContext ctx) {
        System.out.println("WebSocket error");
    }
}