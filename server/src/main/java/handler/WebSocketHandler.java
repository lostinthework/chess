package handler;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebSocketHandler {

    private final GameService gameService;
    private final Map<Integer, Set<WsContext>> connections;
    private final Map<WsContext, Integer> connectionGames;

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
        this.connections = new HashMap<>();
        this.connectionGames = new HashMap<>();
    }

    public void onConnect(WsContext ctx) {
        System.out.println("WebSocket connected");
    }

    public void onMessage(WsMessageContext ctx) throws DataAccessException {
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
                connectionGames.put(ctx, command.getGameID());
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
        if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            Integer gameID = command.getGameID();
            String username = gameService.getUsername(command.getAuthToken());
            GameData game = gameService.getGame(gameID);
            if (username.equals(game.getWhiteUsername())) {
                game.setWhiteUsername(null);
            } else {
                game.setBlackUsername(null);
            }
            gameService.updateGame(game);
            Set<WsContext> gameConnections = connections.get(gameID);
            if (gameConnections != null) {
                gameConnections.remove(ctx);
                if (gameConnections.isEmpty()) {
                    connections.remove(gameID);
                }
            }
            Notification notification = new Notification(username + " left the game.");
            String json = new Gson().toJson(notification);
            if (gameConnections != null) {
                for (WsContext connection : gameConnections) {
                    connection.send(json);
                }
            }
        }
        if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            try {
                if (gameService.getAuth(command.getAuthToken()) == null) {
                    ctx.send(gson.toJson(
                            new ServerMessage(ServerMessage.ServerMessageType.ERROR)
                    ));
                    return;
                }
                GameData game = gameService.getGame(command.getGameID());
                if (game == null || command.getMove() == null) {
                    ctx.send(gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    return;
                }
                String username = gameService.getUsername(command.getAuthToken());
                ChessGame.TeamColor playerColor;
                if (username.equals(game.getWhiteUsername())) {
                    playerColor = ChessGame.TeamColor.WHITE;
                }
                else if (username.equals(game.getBlackUsername())) {
                    playerColor = ChessGame.TeamColor.BLACK;
                }
                else {
                    ctx.send(gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    return;
                }
                if (game.getGame().getTeamTurn() != playerColor) {
                    ctx.send(gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    return;
                }
                try {
                    game.getGame().makeMove(command.getMove());
                }
                catch (InvalidMoveException e) {
                    ctx.send(gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    return;
                }
                gameService.updateGame(game);
                LoadGame loadGame = new LoadGame(game);
                String json = gson.toJson(loadGame);
                Set<WsContext> gameConnections = connections.get(command.getGameID());
                if (gameConnections != null) {
                    for (WsContext connection : gameConnections) {
                        connection.send(json);
                    }
                }
            }
            catch (Exception e) {
                System.out.println("WebSocket move error: " + e.getMessage());
            }
        }
        if (command.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            try {
                if (gameService.getAuth(command.getAuthToken()) == null) {
                    ctx.send(gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    return;
                }
                int gameID = command.getGameID();
                GameData game = gameService.getGame(gameID);
                if (game == null) {
                    ctx.send(gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    return;
                }
                String username = gameService.getUsername(command.getAuthToken());
                boolean isWhite = username.equals(game.getWhiteUsername());
                boolean isBlack = username.equals(game.getBlackUsername());
                if (!isWhite && !isBlack) {
                    ctx.send(gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    return;
                }
                String winner;
                if (isWhite) {
                    winner = game.getBlackUsername();
                } else {
                    winner = game.getWhiteUsername();
                }
                Notification notification = new Notification(username + " resigned. " + winner + " wins.");
                String json = gson.toJson(notification);
                Set<WsContext> gameConnections = connections.get(gameID);
                if (gameConnections != null) {
                    for (WsContext connection : gameConnections) {
                        connection.send(json);
                    }
                }
                gameService.deleteGame(gameID);
                if (gameConnections != null) {
                    for (WsContext connection : gameConnections) {
                        connectionGames.remove(connection);
                    }
                }
                connections.remove(gameID);
            } catch (Exception e) {
                System.out.println("WebSocket resign error: " + e.getMessage());
            }
        }
    }

    public void onClose(WsContext ctx) {
        System.out.println("WebSocket closed");
        Integer gameID = connectionGames.remove(ctx);
        if (gameID != null) {
            Set<WsContext> gameConnections = connections.get(gameID);
            if (gameConnections != null) {
                gameConnections.remove(ctx);
                if (gameConnections.isEmpty()) {
                    connections.remove(gameID);
                }
            }
        }
    }

    public void onError(WsContext ctx) {
        System.out.println("WebSocket error");
    }
}