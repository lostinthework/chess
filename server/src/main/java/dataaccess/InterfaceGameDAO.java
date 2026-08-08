package dataaccess;

import model.GameData;
import java.util.List;

public interface InterfaceGameDAO{
    public List<GameData> getGames() throws DataAccessException;
    public GameData getGamebyID(int gameID) throws DataAccessException;
    public void addGame(GameData gameData) throws DataAccessException;
    public void joinGame(int gameID, String username, String color) throws DataAccessException;
    public void deleteGameData() throws DataAccessException;
    public void updateGame(GameData game) throws DataAccessException;
}