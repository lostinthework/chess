package handler;


public class ListResult {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;

    public ListResult(int gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }
}