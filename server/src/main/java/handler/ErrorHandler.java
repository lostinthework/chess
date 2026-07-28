package handler;

public class ErrorHandler {
    private final String message;
    // What
    public ErrorHandler(String message) {
        this.message = "Error: " + message;
    }
    // Return message
    public String getMessage() {
        return message;
    }
}