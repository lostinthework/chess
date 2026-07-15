package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class MemoryAuthDAO implements InterfaceAuthDAO {
    // Storing data
    private List<AuthData> auths = new ArrayList<>();

    public void addAuth(AuthData authData) {
        auths.add(authData);
    }

    public void deleteAuth(AuthData authData) {
        auths.remove(authData);
    }

    public void deleteAuthData() {
        auths = new ArrayList<>();
    }

    public AuthData getAuth(String authToken) {
        // Iterate through the list until finding matching data
        for (AuthData authData : auths) {
            if (authData.getauthToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }
}