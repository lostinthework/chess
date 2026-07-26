package dataaccess;

import model.AuthData;

public interface InterfaceAuthDAO {
    public void addAuth(AuthData authData) throws DataAccessException;
    public void deleteAuth(AuthData authData) throws DataAccessException;
    public void deleteAuthData() throws DataAccessException;
    public AuthData getAuth(String authToken) throws DataAccessException;
}
