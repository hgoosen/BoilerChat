import java.util.ArrayList;

/**
 * An interface for the Chat class
 *
 * @author Keshav Sreekantham (ksreekan), Benjamin Chen (chen5254)
 * @version November 1, 2024
 */

public interface ChatInterface {

    User[] getUsers();

    ArrayList<Message> getHistory();

    void addMessage(Message message);

    void setUser(int user, User name);

    String getHistoryIn();
}
