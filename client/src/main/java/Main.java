import chess.*;
import client.Client;
import client.ServerFacade;

public class Main {
    public static void main(String[] args) {
        ServerFacade server = new ServerFacade("http://localhost:8080");
        Client client = new Client(server);
        try {
            client.run();
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}