package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.UserData;
import service.UserService;
import io.javalin.http.Context;

public class Login {
    private final UserService useryService;

    public Login(UserService useryService) {
        this.useryService = useryService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var gson = new Gson();
        ctx.contentType("application/json");

        // Verify input
        UserData user;
        try {
            user = gson.fromJson(ctx.body(), UserData.class);
            if (user.getUsername() == null || user.getPassword() == null) {
                throw new DataAccessException("Error: bad request");
            }
        }
        catch (DataAccessException | JsonSyntaxException e) {
            ctx.status(400);
            ctx.result(gson.toJson(new ErrorHandler("Error: bad request")));
            return;
        }

        // Login
        try {
            var loginResult = useryService.Login(user.getUsername(), user.getPassword());
            ctx.result(gson.toJson(loginResult));
        }
        catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(new ErrorHandler(e.getMessage())));
        }
    }
}