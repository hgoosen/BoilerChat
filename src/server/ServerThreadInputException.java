/**
 * An exception for handling errors when a server thread reads from a client
 *
 * @author Keshav Sreekantham
 * @version November 9, 2024
 */

public class ServerThreadInputException extends RuntimeException {
    public ServerThreadInputException(String message) {
        super(message);
    }
}
