package handler;
import service.gameService;
import service.userService;
import io.javalin.http.Context;

public class clear {
    private final userService useryService;
    private final gameService gamesService;

    public clear(userService useryService, gameService gamesService) {
        this.useryService = useryService;
        this.gamesService = gamesService;
    }

    public void handle(Context ctx) {
        // clear everything
        useryService.clear();
        gamesService.clear();
        ctx.contentType("application/json");
        ctx.result("{}");
    }
}