package dataaccess;

import model.AuthData;

public interface InterfaceAuthDAO {
    public void addAuth(AuthData authData);
    public void deleteAuth(AuthData authData);
    public void deleteAuthData();
    public AuthData getAuth(String authToken);
}
