package client;

public class JoinRequest {
    private final int gameID;
    private final String playerColor;

    public JoinRequest (int gameID, String playerColor) {
        this.gameID = gameID;
        this.playerColor = playerColor;
    }
}
