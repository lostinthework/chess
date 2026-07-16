package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import java.util.List;
import java.util.Random;

public class gameService extends authService {
    public gameService(InterfaceUserDAO userDAO, InterfaceAuthDAO authDAO, InterfaceGameDAO gameDAO) {
        super(userDAO, authDAO, gameDAO);
    }
    public GameData getGame(int gameID) {
        return gameDAO.getGamebyID(gameID);
    }
    public List<GameData> getGames() {
        return gameDAO.getGames();
    }
    public int createGame(String gameName) {
        int gameID = new Random().nextInt(Integer.MAX_VALUE) + 1;
        gameDAO.addGame(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }
//    public void updateGame(String username, String color, int gameID) {
//        gameDAO.joinGame(username, color, gameID);
//    }

}
