package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.ResponseException;
import model.UserData;
import service.UserService;
import io.javalin.http.Context;


public class Register {
    private final UserService useryService;

    public Register(UserService useryService) {
        this.useryService = useryService;
    }
    public void handle(Context ctx) throws DataAccessException, ResponseException {
        var gson = new Gson();
        ctx.contentType("application/json");

        // Verify input
        UserData user;
        try {
            user = gson.fromJson(ctx.body(), UserData.class);
            if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
            }
        }
        catch (NullPointerException | JsonSyntaxException e) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
        }

        // Register
        var registerResult = useryService.register(user);
        ctx.result(gson.toJson(registerResult));
    }
}