package client;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class WebSocketFacade implements WebSocket.Listener {

    private final Gson gson = new Gson();
    private final Client client;

    public WebSocketFacade(Client client) {
        this.client = client;
    }

    public void sendConnect(WebSocket webSocket, String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        webSocket.sendText(gson.toJson(command), true);
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

        ServerMessage message = gson.fromJson(data.toString(), ServerMessage.class);

        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            // We'll handle the actual notification here next
            Notification notification = gson.fromJson(data.toString(), Notification.class);
            client.notify(notification);
        }

        webSocket.request(1);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("WebSocket error: " + error.getMessage());
    }
}