package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.UserService;
import io.javalin.http.Context;

public class Logout {
    private final UserService useryService;

    public Logout(UserService useryService) {
        this.useryService = useryService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var gson = new Gson();
        ctx.contentType("application/json");

        // Verify authentication
        String authToken = ctx.header("Authorization");
        try {
            // Logout
            useryService.logout(authToken);
        }
        catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(new ErrorHandler("Error: unauthorized")));
            return;
        }
        ctx.result("{}");
    }
}