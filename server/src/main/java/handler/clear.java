package handler;
import service.gameService;
import service.userService;
import spark.*;

public class clear {
    private final userService useryService;
    private final gameService gamesService;

    public clear(userService useryService, gameService gamesService) {
        this.useryService = useryService;
        this.gamesService = gamesService;
    }

    public Object handle(Request req, Response res) {
        // clear everything
        useryService.clear();
        gamesService.clear();
        return "{}";
    }
}