package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.ResponseException;
import service.UserService;
import io.javalin.http.Context;

public class Logout {
    private final UserService useryService;

    public Logout(UserService useryService) {
        this.useryService = useryService;
    }

    public void handle(Context ctx) throws DataAccessException, ResponseException {
        var gson = new Gson();
        ctx.contentType("application/json");

        // Verify authentication
        String authToken = ctx.header("Authorization");
        // Logout
        useryService.logout(authToken);
        ctx.result("{}");
    }
}