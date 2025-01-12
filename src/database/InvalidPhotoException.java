/**
 * An exception for handling errors when reading or creating photos.
 *
 * @author Henri Goosen (hgoosen)
 * @version Nov. 3, 2024
 */

public class InvalidPhotoException extends Exception {

    public InvalidPhotoException(String message) {
        super(message);
    }
}
