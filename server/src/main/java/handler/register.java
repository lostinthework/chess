package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.UserData;
import service.userService;
import spark.Request;
import spark.Response;

public class register {
    private final userService useryService;

    public register(userService useryService) {
        this.useryService = useryService;
    }
    public Object handle(Request req, Response res) throws DataAccessException {
        var gson = new Gson();
        res.type("application/json");

        // Verify input
        UserData user;
        try {
            user = gson.fromJson(req.body(), UserData.class);
            if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
                throw new DataAccessException("Error: bad request");
            }
        }
        catch (DataAccessException | JsonSyntaxException e) {
            res.status(400);
            return gson.toJson(new ErrorHandler("Error: bad request"));
        }

        // Register
        try {
            var RegisterResult = useryService.register(user);
            return gson.toJson(RegisterResult);
        }
        catch (DataAccessException e) {
            res.status(403);
            return gson.toJson(new ErrorHandler(e.getMessage()));
        }
    }
}