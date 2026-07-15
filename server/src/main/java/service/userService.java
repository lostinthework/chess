package service;

import dataaccess.DataAccessException;
import dataaccess.*;
import handler.LoginResult;
import handler.RegisterResult;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class userService extends authService {
    public userService(InterfaceUserDAO userDAO, InterfaceAuthDAO authDAO, InterfaceGameDAO gameDAO) {
        // super from authService
        super(userDAO, authDAO, gameDAO);
    }

    public RegisterResult register(UserData userData) throws DataAccessException {
        var user = userDAO.checkUser(userData.getUsername());
        // Check if the requested username has already been taken
        if (user != null) {
            throw new DataAccessException("Error: already taken");
        }
        // add the username to the database
        userDAO.addUser(userData);
        // give the user an authToken
        String authToken = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(userData.getUsername(), authToken));
        // return the username and authToken
        return new RegisterResult(userData.getUsername(), authToken);
    }

    public LoginResult login(String username, String password) throws DataAccessException {
        // check if the username was correct
        var user = userDAO.getUser(username, password);
        if (user == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // create an authToken and return it with the username
        String authToken = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(username, authToken));
        return new LoginResult(username, authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        // Check authToken
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        else {
            deleteAuth(authData);
        }
    }
}