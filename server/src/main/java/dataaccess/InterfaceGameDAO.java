package dataaccess;

import model.GameData;
import java.util.List;

public interface InterfaceGameDAO{
    public List<GameData> getGames();
    public GameData getGamebyID(int gameID);
    public void addGame(GameData gameData);
    public void joinGame(int gameID, String username, String color);
    public void deleteGameData();
}
