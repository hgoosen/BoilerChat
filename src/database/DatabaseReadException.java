/**
 * An exception for handling errors when reading from the database.
 *
 * @author Benjamin Chen (chen5254)
 * @version October 30,2024
 */

public class DatabaseReadException extends Exception {

    public DatabaseReadException(String message) {
        super(message);
    }
}