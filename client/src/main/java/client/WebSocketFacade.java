package client;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class WebSocketFacade implements WebSocket.Listener {

    private final Gson gson = new Gson();
    private final String authToken;
    private final int gameID;
    private final Client client;
    private final boolean observer;

    public WebSocketFacade(String authToken, int gameID, Client client, boolean observer) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.client = client;
        this.observer = observer;
    }

    public void sendConnect(WebSocket webSocket, String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, null);
        webSocket.sendText(gson.toJson(command), true);
    }

    public void sendLeave(WebSocket webSocket) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, null);
        webSocket.sendText(gson.toJson(command), true);
    }

    public void sendObserve(WebSocket webSocket) {
        UserGameCommand command =
                new UserGameCommand(UserGameCommand.CommandType.OBSERVE, authToken, gameID, null);
        webSocket.sendText(gson.toJson(command), true);
    }

    public void sendMove(WebSocket webSocket, ChessMove move) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        webSocket.sendText(gson.toJson(command), true);
    }

    public void sendResign(WebSocket webSocket) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID, null);
        webSocket.sendText(gson.toJson(command), true);
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("WebSocket opened");
        sendConnect(webSocket, authToken, gameID);
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
//        System.out.println("Received from server: " + data);

        ServerMessage message = gson.fromJson(data.toString(), ServerMessage.class);

        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            Notification notification = gson.fromJson(data.toString(), Notification.class);
            client.notify(notification);
        }
        else if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGame loadGame = gson.fromJson(data.toString(), LoadGame.class);
            client.updateGame(loadGame.getGame());
        }
        else if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            System.out.println("WebSocket error from server.");
        }

        webSocket.request(1);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("WebSocket error: " + error.getMessage());
    }
}