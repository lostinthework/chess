package handler;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import service.GameService;
import service.UserService;
import io.javalin.http.Context;

public class Clear {
    private final UserService useryService;
    private final GameService gamesService;

    public Clear(UserService useryService, GameService gamesService) {
        this.useryService = useryService;
        this.gamesService = gamesService;
    }

    public void handle(Context ctx) throws DataAccessException, ResponseException {
        try {
            // clear everything
            useryService.clear();
            gamesService.clear();
            ctx.contentType("application/json");
            ctx.result("{}");
        }
        catch (DataAccessException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }
}