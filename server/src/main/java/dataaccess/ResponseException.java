package dataaccess;

public class ResponseException extends Exception {
    public enum Code {
        ServerError,
        ClientError,
    }

    public final Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

}
