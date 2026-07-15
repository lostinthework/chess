package dataaccess;

import model.UserData;

public interface InterfaceUserDAO {
    public void addUser(UserData userData);
    public UserData getUser(String username, String password);
    public UserData checkUser(String username);
    public void deleteUserData();
}