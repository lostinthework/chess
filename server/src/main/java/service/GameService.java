package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import java.util.List;
import java.util.Random;

public class GameService extends AuthService {
    public GameService(InterfaceUserDAO userDAO, InterfaceAuthDAO authDAO, InterfaceGameDAO gameDAO) {
        super(userDAO, authDAO, gameDAO);
    }
    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGamebyID(gameID);
    }
    public List<GameData> getGames() throws DataAccessException {
        return gameDAO.getGames();
    }
    public int createGame(String gameName) throws DataAccessException {
        int gameID = new Random().nextInt(Integer.MAX_VALUE) + 1;
        gameDAO.addGame(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }
    public void joinGame(int gameID, String username, String color) throws DataAccessException {
        gameDAO.joinGame(gameID, username, color);
    }
    public void updateGame(GameData game) throws DataAccessException {
        gameDAO.updateGame(game);
    }
}
