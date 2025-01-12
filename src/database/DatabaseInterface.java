import javax.swing.*;
import java.util.ArrayList;

/**
 * An interface for the Database class
 *
 * @author Henri Goosen (hgoosen), Benjamin Chen (chen5254)
 * @version December 2, 2024
 */
public interface DatabaseInterface {

    void readUser() throws DatabaseReadException;

    void readChat() throws DatabaseReadException;

    boolean editUser(User targetUser, User newUser);

    boolean addUser(User user);

    boolean addChat(Chat chat);

    void assignChat(Chat chat);

    boolean writeDatabase();

    ArrayList<User> searchUser(String search);

    ArrayList<User> getUsers();

    User getUserFromUsername(String username) throws InvalidUserException;

    ArrayList<Chat> getAllChats();

    boolean validateUser(String username, String password);

    boolean addFriend(String client, String friend);

    boolean addBlocked(String username, String blocked);

    boolean setProfilePhoto(String username, ImageIcon photo);

    ImageIcon getProfilePhoto(String username);

    boolean removeFriend(String username, String friendRemoved);

    boolean removeBlocked(String username, String blockedRemoved);

    boolean sendMessage(String username, String otherUsername, Message message);

    ArrayList<String> getUserList(String username, String instanceField);

    ArrayList<Chat> getChats(String username);

    boolean getPrivacyStatus(String username);

    boolean setPrivacyStatus(String username, boolean privacy);

    String showProfile(String currUser, String profileUser);
}
