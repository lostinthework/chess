package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.userService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class logout {
    private final userService useryService;

    public logout(userService useryService) {
        this.useryService = useryService;
    }

    public Object handle(Request req, Response res) throws DataAccessException {
        var gson = new Gson();
        res.type("application/json");

        // Verify authentication
        String authToken = req.headers("Authorization");
        try {
            // Logout
            useryService.logout(authToken);
        }
        catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorHandler("Error: unauthorized"));
        }
        return "{}";
    }
}