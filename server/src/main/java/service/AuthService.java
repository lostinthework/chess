package service;

import dataaccess.DataAccessException;
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
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    // delete authData
    public void deleteAuth(AuthData authData) throws DataAccessException {
        authDAO.deleteAuth(authData);
    }

    // get username from authToken
    public String getUsername(String authToken) throws DataAccessException {
        return getAuth(authToken).getUsername();
    }

    // clear everything
    public void clear() throws DataAccessException {
        userDAO.deleteUserData();
        authDAO.deleteAuthData();
        gameDAO.deleteGameData();
    }
}