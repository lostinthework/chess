package model;

public class LeaveRequest {
    private double gameID;

    public LeaveRequest(double gameID) {
        this.gameID = gameID;
    }

    public double getGameID() {
        return gameID;
    }
}
