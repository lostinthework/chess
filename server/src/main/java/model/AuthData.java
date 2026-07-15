package model;

public class AuthData {

    private String authToken;
    private String username;

    public AuthData(String username, String authToken) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getauthToken() {
        return authToken;
    }
    public String getUsername() {
        return username;
    }
}