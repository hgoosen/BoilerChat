/**
 * An interface for the Server class
 *
 * @author Keshav Sreekantham
 * @version November 9, 2024
 */

public interface ServerInterface {
    boolean logInUser(String user, String pass);

    boolean registerUser(String user, String pass);

    void run();

}
