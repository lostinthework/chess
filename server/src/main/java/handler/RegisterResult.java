package handler;

public class RegisterResult {
    private String username;
    private String authToken;

    public RegisterResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}