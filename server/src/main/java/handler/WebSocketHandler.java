package handler;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.*;
import model.GameData;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import chess.ChessMove;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler, WsErrorHandler {

    private final GameService gameService;
    private final Map<Integer, Set<WsContext>> connections;
    private final Map<WsContext, Integer> connectionGames;

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
        this.connections = new HashMap<>();
        this.connectionGames = new HashMap<>();
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("WebSocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        Gson gson = new Gson();
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx, command);
                case LEAVE -> leave(ctx, command);
                case MAKE_MOVE -> makeMove(ctx, command, gson);
                case RESIGN -> resign(ctx, command, gson);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WebSocket error: " + e.getMessage());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
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

    @Override
    public void handleError(WsErrorContext ctx) {
        System.out.println("Error: " + ctx.error());
        ctx.error().printStackTrace();
    }


    private void connect(WsMessageContext ctx, UserGameCommand command) {
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

            System.out.println("SENDING LOAD_GAME to " + username +
                    " for game " + command.getGameID());
            LoadGame loadGame = new LoadGame(game);
            ctx.send(new Gson().toJson(loadGame));

            Notification notification = new Notification(username + " joined the game as " + color + ".");
            String json = new Gson().toJson(notification);
            for (WsContext connection : connections.get(command.getGameID())) {
                if (connection.session != ctx.session) {
                    connection.send(json);
                }
            }
        }
        catch (Exception e) {
            System.out.println("WebSocket error: " + e.getMessage());
        }
    }

    private void leave(WsMessageContext ctx, UserGameCommand command) throws DataAccessException {
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

    private void makeMove(WsMessageContext ctx, UserGameCommand command, Gson gson) {
        try {
            if (gameService.getAuth(command.getAuthToken()) == null) {
                ctx.send(gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
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
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Error: It is not your turn.");
                ctx.send(gson.toJson(error));
                return;
            }
            try {
                game.getGame().makeMove(command.getMove());
            }
            catch (InvalidMoveException e) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Error: Invalid move.");
                ctx.send(gson.toJson(error));
                System.out.println("Invalid move: " + e.getMessage());
                return;
            }
            gameService.updateGame(game);
            Set<WsContext> gameConnections = connections.get(command.getGameID());
            if (gameConnections != null) {
                for (WsContext connection : gameConnections) {
                    System.out.println(
                            "connection: " + connection.getClass().getName()
                                    + " same object? " + (connection == ctx)
                    );
                }
            }
            if (gameConnections != null) {
                LoadGame loadGame = new LoadGame(game);
                notifyAll(gameConnections, gson.toJson(loadGame));
                ChessMove move = command.getMove();
                Notification moveNotification = new Notification(
                        username + " moved " + move.getStartPosition() + " to " + move.getEndPosition());
                String notificationJson = gson.toJson(moveNotification);
                notify(gameConnections, notificationJson, ctx);
            }
            ChessGame.TeamColor nextTurn = game.getGame().getTeamTurn();
            if (game.getGame().isInCheckmate(nextTurn)) {
                String winUsername;
                if (nextTurn == ChessGame.TeamColor.WHITE) {
                    winUsername = game.getBlackUsername();
                } else {
                    winUsername = game.getWhiteUsername();
                }
                Notification notification = new Notification(winUsername + " wins.");
                notifyHelper(gameConnections, gson, notification, command);
                return;
            }
            if (game.getGame().isInStalemate(nextTurn)) {
                Notification notification = new Notification("Stalemate!");
                notifyHelper(gameConnections, gson, notification, command);
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
                    notifyAll(gameConnections, notificationJson);
                }
            }
        }
        catch (Exception e) {
            System.out.println("WebSocket move error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void notifyAll(Set<WsContext> gameConnections, String json) {
        for (WsContext connection : gameConnections) {
            connection.send(json);
        }
    }

    private void notify(Set<WsContext> gameConnections, String json, WsMessageContext ctx) {
        for (WsContext connection : gameConnections) {
            if (connection.session != ctx.session) {
                connection.send(json);
            }
        }
    }

    private void notifyHelper(Set<WsContext> gameConnections, Gson gson, Notification notification,
                              UserGameCommand command) throws DataAccessException {
        if (gameConnections != null) {
            String notificationJson = gson.toJson(notification);
            notifyAll(gameConnections, notificationJson);
            connections.remove(command.getGameID());
            for (WsContext connection : gameConnections) {
                connectionGames.remove(connection);
            }
        }
        gameService.deleteGame(command.getGameID());
    }

    private void resign(WsMessageContext ctx, UserGameCommand command, Gson gson) {
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