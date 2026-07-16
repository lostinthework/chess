package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.userService;
import io.javalin.http.Context;

import java.util.Map;

public class logout {
    private final userService useryService;

    public logout(userService useryService) {
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