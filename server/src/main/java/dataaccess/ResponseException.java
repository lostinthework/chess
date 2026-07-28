package dataaccess;

public class ResponseException extends Exception {
    public enum Code {
        ServerError,
        BadRequest,
        Unauthorized,
        Forbidden
    }

    public final Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

}
