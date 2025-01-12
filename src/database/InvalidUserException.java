/**
 * An exception for handling errors when reading or creating users.
 *
 * @author Benjamin Chen (chen5254)
 * @version October 30, 2024
 */

public class InvalidUserException extends Exception {

    public InvalidUserException(String message) {
        super(message);
    }
}
