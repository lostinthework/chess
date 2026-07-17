package service;

import dataaccess.InterfaceAuthDAO;
import dataaccess.InterfaceGameDAO;
import dataaccess.InterfaceUserDAO;
import model.AuthData;

public class AuthService {

    public dataaccess.InterfaceUserDAO userDAO;
    public dataaccess.InterfaceAuthDAO authDAO;
    public dataaccess.InterfaceGameDAO gameDAO;

    public AuthService(InterfaceUserDAO userDAO, InterfaceAuthDAO authDAO, InterfaceGameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    // get authData from authToken
    public AuthData getAuth(String authToken) {
        return authDAO.getAuth(authToken);
    }

    // delete authData
    public void deleteAuth(AuthData authData) {
        authDAO.deleteAuth(authData);
    }

    // get username from authToken
    public String getUsername(String authToken) {
        return getAuth(authToken).getUsername();
    }

    // clear everything
    public void clear() {
        userDAO.deleteUserData();
        authDAO.deleteAuthData();
        gameDAO.deleteGameData();
    }
}