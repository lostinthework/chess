package client;

public class JoinRequest {
    private final int gameID;
    private final String color;

    public JoinRequest (int gameID, String color) {
        this.gameID = gameID;
        this.color = color;
    }
}
