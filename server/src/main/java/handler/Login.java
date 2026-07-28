package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import model.UserData;
import org.eclipse.jetty.server.Response;
import service.UserService;
import io.javalin.http.Context;

public class Login {
    private final UserService useryService;

    public Login(UserService useryService) {
        this.useryService = useryService;
    }

    public void handle(Context ctx) throws DataAccessException, ResponseException {
        var gson = new Gson();
        ctx.contentType("application/json");

        // Verify input
        UserData user;
        try {
            user = gson.fromJson(ctx.body(), UserData.class);
            if (user.getUsername() == null || user.getPassword() == null) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
            }
        }
        catch (NullPointerException | JsonSyntaxException e) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
        }

        // Login
        var loginResult = useryService.login(user.getUsername(), user.getPassword());
        ctx.result(gson.toJson(loginResult));
    }
}