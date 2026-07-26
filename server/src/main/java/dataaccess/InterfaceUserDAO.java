package dataaccess;

import model.UserData;

public interface InterfaceUserDAO {
    public void addUser(UserData userData) throws DataAccessException;
    public UserData getUser(String username, String password) throws DataAccessException;
    public UserData checkUser(String username) throws DataAccessException;
    public void deleteUserData() throws DataAccessException;
}