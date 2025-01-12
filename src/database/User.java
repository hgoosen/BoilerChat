import javax.swing.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class representing a user for the social media platform.
 *
 * @author Keshav Sreekantham (ksreekan), Benjamin Chen (chen5254), Henri Goosen (hgoosen)
 * @version December 2, 2024
 */

public class User implements UserInterface, java.io.Serializable {

    private String username;
    private String password;
    private ImageIcon profilePhoto;

    private ArrayList<Chat> chats;

    private ArrayList<String> requests;
    private ArrayList<String> friends;
    private ArrayList<String> blocked;

    private boolean isPublic;
    private final static Object MESSAGELOCK = new Object();

    public User(String username, String password) throws InvalidUserException {

        String regex = "^[a-zA-Z0-9]{4,16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        if (matcher.matches()) {
            this.username = username;
        } else {
            throw new InvalidUserException("Invalid Username");
        }

        String passwordRegex = "^[a-zA-Z0-9]{8,20}$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        Matcher passwordMatcher = passwordPattern.matcher(password);
        if (passwordMatcher.matches()) {
            this.password = password;
        } else {
            throw new InvalidUserException("Invalid Password");
        }

        this.profilePhoto = null;
        this.chats = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.isPublic = true;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ImageIcon getProfilePhoto() {
        return profilePhoto;
    }

    public boolean getPublic() {
        return isPublic;
    }

    // Setters
    public boolean setUsername(String user) {

        // Username Alphanumeric Only, 4-16 Characters
        String regex = "^[a-zA-Z0-9]{4,16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(user);
        if (matcher.matches()) {
            this.username = user;
            return true;
        }
        return false;
    }

    public boolean setPassword(String pass) {
        // Password Alphanumeric Only, 8-20 Characters
        String regex = "^[a-zA-Z0-9]{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pass);
        if (matcher.matches()) {
            this.password = pass;
            return true;
        }
        return false;
    }

    public boolean setProfilePhoto(ImageIcon photo) throws InvalidPhotoException {
        try {
            this.profilePhoto = photo;
            return true;
        } catch (Exception e) {
            throw new InvalidPhotoException(e.getMessage());
        }
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    // Chat Methods
    public ArrayList<Chat> getChats() {
        return chats;
    }

    public boolean addChat(Chat chat) {
        if (chats.contains(chat)) {
            System.out.println("You are already in this chat!");
            return false;
        } else {
            chats.add(chat);
            return true;
        }
    }

    public boolean deleteChat(String user) {
        for (Chat currentChat : chats) {
            if (currentChat.getUsers()[0].getUsername().equals(user)
                    || currentChat.getUsers()[1].getUsername().equals(user)) {
                this.chats.remove(currentChat);
                return true;
            }
        }
        return false;
    }

    // Friends Methods
    public ArrayList<String> getFriends() {
        return friends;
    }

    public boolean addFriend(String user) {
        if (friends.contains(user)) {
            System.out.println("That user is already friends with you!");
            return false;
        } else if (user.equals(this.username)) {
            System.out.println("You cannot friend yourself!");
            return false;
        } else {
            if (isPublic) { // Automatically add friend if public
                friends.add(user);
                return true;
            }

            // Code below is for private users
            if (requests.contains(user)) {
                // If the user we are adding already requested us then...
                friends.add(user);
                requests.remove(user);
                return true;
            }

            addRequest(user); // Add to requests
        }
        return false;
    }

    public boolean removeFriend(String user) {
        for (int i = 0; i < friends.size(); i++) {
            if (user.equals(friends.get(i))) {
                friends.remove(i);
                return true;
            }
        }
        return false;
    }

    // Block Methods
    public ArrayList<String> getBlocked() {
        return blocked;
    }

    public boolean addBlocked(String user) {
        if (blocked.contains(user)) {
            System.out.println("That user is already blocked!");
            return false;
        } else if (user.equals(this.username)) {
            System.out.println("You cannot block yourself!");
            return false;
        } else {
            blocked.add(user);
            return true;
        }
    }

    public boolean removeBlocked(String user) {
        for (int i = 0; i < blocked.size(); i++) {
            if (user.equals(blocked.get(i))) {
                blocked.remove(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getRequests() {
        return requests;
    }

    public boolean addRequest(String user) {
        if (!requests.contains(user)) {
            requests.add(user);
            return true;
        }
        return false;
    }

    public boolean message(Chat chat, Message message) {
        // Checks Public/Friend
        if (chat.getUsers()[0] == null) {
            System.out.println("This chat does not exist.");
            return false;
        }
        if (!(this.isPublic || this.friends.contains(chat.getUsers()[0].getUsername()) ||
                this.friends.contains(chat.getUsers()[1].getUsername()))) {
            System.out.println("You are on friends-only mode, and you are not friends with them.");
            return false;
        }

        if (!this.chats.contains(chat)) {
            System.out.println("You are not a participant in this chat.");
            return false;
        }
        // Checks Blocks/Privacy
        for (User user : chat.getUsers()) {

            if (user.equals(this)) {
                continue;
            }

            if (this.blocked.contains(user.getUsername())) {
                System.out.println("You have blocked this user and cannot send messages.");
                return false;
            }

            if (user.getBlocked().contains(username)) {
                System.out.println("This user has blocked you. You cannot message them.");
                return false;
            }

            if (!user.getFriends().contains(username) && !user.getPublic()) {
                System.out.println("This user is on friends-only mode, and you are not friends with them.");
                return false;
            }
        }
        synchronized (MESSAGELOCK) { // Synchronized to prevent from chat writing at the same time.
            chat.addMessage(message);
        }
        return true;
    }

    public boolean isFriends(String otherUser) {
        return getFriends().contains(otherUser);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) return false;
        User u = (User) obj;
        return this.getUsername().equals(u.getUsername());
    }

    @Override
    public String toString() {
        return username;
    }
}
