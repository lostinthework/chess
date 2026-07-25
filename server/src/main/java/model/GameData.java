package model;

public class GameData {

    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }

    public int getGameID() {
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getName() {
        return gameName;
    }

    public void join(String username, String color) {
        // Join the game depending on color
        if (color.equals("WHITE")) {
            whiteUsername = username;
        }
        else {
            blackUsername = username;
        }
    }
}