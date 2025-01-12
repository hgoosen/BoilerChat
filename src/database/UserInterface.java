import javax.swing.*;
import java.util.ArrayList;

/**
 * A class representing a user for the social media platform.
 *
 * @author Keshav Sreekantham (ksreekan), Benjamin Chen (chen5254)
 * @version December 2, 2024
 */

public interface UserInterface {
    String getUsername();

    String getPassword();

    ImageIcon getProfilePhoto();

    boolean getPublic();

    boolean setUsername(String username);

    boolean setPassword(String password);

    boolean setProfilePhoto(ImageIcon photo) throws InvalidPhotoException;

    void setPublic(boolean isPublic);

    ArrayList<Chat> getChats();

    boolean addChat(Chat chat);

    boolean deleteChat(String username);

    ArrayList<String> getFriends();

    boolean addFriend(String username);

    boolean removeFriend(String username);

    ArrayList<String> getBlocked();

    boolean addBlocked(String username);

    boolean removeBlocked(String username);

    ArrayList<String> getRequests();

    boolean addRequest(String username);

    boolean message(Chat chat, Message message);

    boolean isFriends(String username);
}

