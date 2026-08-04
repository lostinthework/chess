package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class MemoryUserDAO implements InterfaceUserDAO {
    // Store users
    private List<UserData> users = new ArrayList<>();
    // add user
    public void addUser(UserData userData) {
        users.add(userData);
    }
    // get user
    public UserData getUser(String username, String password) {
        for (UserData user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
    // check user
    public UserData checkUser(String username) {
        for (UserData user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    // delete user
    public void deleteUserData() {
        users = new ArrayList<>();
    }
}