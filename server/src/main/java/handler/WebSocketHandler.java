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
import io.javalin.websocket.WsErrorContext;
import chess.ChessMove;

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
                System.out.println("Got game: " + game);
                System.out.println("Game turn: " + game.getGame().getTeamTurn());
                System.out.println("Player color: " + playerColor);
                if (game.getGame().getTeamTurn() != playerColor) {
                    System.out.println("NOT THEIR TURN");
                    Notification notification = new Notification("It is not your turn.");

                    ctx.send(gson.toJson(notification));
                    return;
                }
                System.out.println("TURN IS CORRECT");
                try {
                    System.out.println("BEFORE makeMove");
                    game.getGame().makeMove(command.getMove());
                    System.out.println("AFTER makeMove");
                }
                catch (InvalidMoveException e) {
                    System.out.println("Invalid move: " + e.getMessage());
                    Notification notification = new Notification("Invalid move.");
                    ctx.send(gson.toJson(notification));
                    return;
                }
                System.out.println("BEFORE updateGame");
                gameService.updateGame(game);
                System.out.println("AFTER updateGame");
                Set<WsContext> gameConnections = connections.get(command.getGameID());
                if (gameConnections != null) {
                    ChessMove move = command.getMove();
                    Notification moveNotification = new Notification(
                            username + " moved " + move.getStartPosition() + " to " + move.getEndPosition());
                    String notificationJson = gson.toJson(moveNotification);
                    for (WsContext connection : gameConnections) {
                        connection.send(notificationJson);
                    }
                }
                ChessGame.TeamColor nextTurn = game.getGame().getTeamTurn();
                if (game.getGame().isInCheckmate(nextTurn)) {
                    String winUsername;
                    if (nextTurn == ChessGame.TeamColor.WHITE) {
                        winUsername = game.getWhiteUsername();
                    } else {
                        winUsername = game.getBlackUsername();
                    }
                    Notification notification = new Notification(winUsername + " wins.");
                    if (gameConnections != null) {
                        String notificationJson = gson.toJson(notification);
                        for (WsContext connection : gameConnections) {
                            connection.send(notificationJson);
                        }
                        connections.remove(command.getGameID());
                        for (WsContext connection : gameConnections) {
                            connectionGames.remove(connection);
                        }
                    }
                    gameService.deleteGame(command.getGameID());
                    return;
                }
                if (game.getGame().isInStalemate(nextTurn)) {
                    Notification notification = new Notification("Stalemate!");
                    if (gameConnections != null) {
                        String notificationJson = gson.toJson(notification);
                        for (WsContext connection : gameConnections) {
                            connection.send(notificationJson);
                        }
                        connections.remove(command.getGameID());
                        for (WsContext connection : gameConnections) {
                            connectionGames.remove(connection);
                        }
                    }
                    gameService.deleteGame(command.getGameID());
                    return;
                }
                if (game.getGame().isInCheck(nextTurn)) {
                    String checkedUsername;
                    if (nextTurn == ChessGame.TeamColor.WHITE) {
                        checkedUsername = game.getWhiteUsername();
                    } else {
                        checkedUsername = game.getBlackUsername();
                    }
                    Notification notification = new Notification(checkedUsername + " is in check.");
                    if (gameConnections != null) {
                        String notificationJson = gson.toJson(notification);
                        for (WsContext connection : gameConnections) {
                            connection.send(notificationJson);
                        }
                    }
                }
                LoadGame loadGame = new LoadGame(game);
                String json = gson.toJson(loadGame);
                if (gameConnections != null) {
                    for (WsContext connection : gameConnections) {
                        connection.send(json);
                    }
                }
            }
            catch (Exception e) {
                System.out.println("WebSocket move error: " + e.getMessage());
                e.printStackTrace();
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

    public void onError(WsErrorContext ctx) {
        System.out.println("Error: " + ctx.error());
        ctx.error().printStackTrace();
        System.out.println("WebSocket error");
    }
}