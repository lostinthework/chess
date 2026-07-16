package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.UserData;
import service.userService;
import io.javalin.http.Context;


public class register {
    private final userService useryService;

    public register(userService useryService) {
        this.useryService = useryService;
    }
    public void handle(Context ctx) throws DataAccessException {
        var gson = new Gson();
        ctx.contentType("application/json");

        // Verify input
        UserData user;
        try {
            user = gson.fromJson(ctx.body(), UserData.class);
            if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
                throw new DataAccessException("Error: bad request");
            }
        }
        catch (DataAccessException | JsonSyntaxException e) {
            ctx.status(400);
            ctx.result(gson.toJson(new ErrorHandler("Error: bad request")));
            return;
        }

        // Register
        try {
            var registerResult = useryService.register(user);
            ctx.result(gson.toJson(registerResult));
        }
        catch (DataAccessException e) {
            ctx.status(403);
            ctx.result(gson.toJson(new ErrorHandler(e.getMessage())));
        }
    }
}