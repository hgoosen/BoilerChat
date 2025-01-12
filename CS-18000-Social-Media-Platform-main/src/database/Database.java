import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * A class to handle File IO.
 *
 * @author Henri Goosen (hgoosen), Benjamin Chen (chen5254)
 * @version December 2, 2024
 */

public class Database implements DatabaseInterface {

    private final String userFile;
    private final String chatFile;
    private ArrayList<User> users;
    private ArrayList<Chat> chats;

    private final static Object LOCK = new Object();

    public Database(String userFile, String chatFile) {
        this.userFile = userFile;
        this.chatFile = chatFile;
        this.users = new ArrayList<>();
        this.chats = new ArrayList<>();
    }

    public void readUser() throws DatabaseReadException {
        // Convert the userFile string to a File object
        File realUserFile = new File(userFile);

        // Check if the file exists
        if (!realUserFile.exists()) {
            throw new DatabaseReadException("User file does not exist");
        }

        // Check if the path points to a valid file (not a directory)
        if (!realUserFile.isFile()) {
            throw new DatabaseReadException("User path is not a valid file");
        }

        // Check if the file is empty
        if (realUserFile.length() == 0) {
            return;
        }

        // Read objects from the file.
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(realUserFile))) {
            while (true) {
                try {
                    Object user = ois.readObject();
                    addUser((User) user);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new DatabaseReadException("User File Not Found");
        } catch (IOException | ClassNotFoundException e) {
            throw new DatabaseReadException("User Reading Error");
        }
    }

    public void readChat() throws DatabaseReadException {
        // Convert the chatFile string to a File object
        File realChatFile = new File(chatFile);

        // Check if the file exists
        if (!realChatFile.exists()) {
            throw new DatabaseReadException("Chat File does not exist");
        }

        // Check if the path points to a valid file (not a directory)
        if (!realChatFile.isFile()) {
            throw new DatabaseReadException("Chat Path is not a valid file");
        }

        // Check if the file is empty
        if (realChatFile.length() == 0) {
            return;
        }

        // Read objects from the file.
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(realChatFile))) {
            while (true) {
                try {
                    Object chat = ois.readObject();
                    chats.add((Chat) chat);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new DatabaseReadException("Chat File Not Found");
        } catch (IOException | ClassNotFoundException e) {
            throw new DatabaseReadException("Chat Reading Error");
        }
    }

    public void assignChat(Chat selectedChat) {
        synchronized (LOCK) {
            // Assigns users to the specified chat.
            for (User eachDatabaseUser : this.users) {
                for (User eachChatUser : selectedChat.getUsers()) {
                    if (eachChatUser.equals(eachDatabaseUser)) {
                        eachDatabaseUser.addChat(selectedChat);
                    }
                }
            }
        }
    }

    public boolean editUser(User targetUser, User newUser) {
        synchronized (LOCK) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).equals(targetUser)) {

                    users.set(i, newUser);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addUser(User newUser) {
        synchronized (LOCK) {
            for (User oldUser : users) {
                if (oldUser.equals(newUser)) {
                    return false;
                }
            }
            users.add(newUser);
        }
        return true;
    }

    public boolean addChat(Chat newChat) {
        synchronized (LOCK) {
            for (Chat oldChat : chats) {
                if (oldChat.equals(newChat)) {
                    return false;
                }
            }
            chats.add(newChat);
        }
        return true;
    }

    public boolean writeDatabase() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFile))) {
            for (User user : users) {
                oos.writeObject(user);
            }
            oos.flush();
        } catch (IOException e) {
            System.out.println("User Write Error");
            return false;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chatFile))) {
            for (Chat chat : chats) {
                oos.writeObject(chat);
            }
            oos.flush();
        } catch (IOException e) {
            System.out.println("Chat Write Error");
            return false;
        }
        return true;
    }

    // Everything above this line is to generate the database on start.
    // Everything below this line is to access and alter the database.

    public ArrayList<User> getUsers() {
        return users;
    }

    public User getUserFromUsername(String username) throws InvalidUserException {
        for (User user : this.users) {
            if (user.getUsername().equals(username)) return user;
        }
        throw new InvalidUserException("This user does not exist");
    }

    public ArrayList<Chat> getAllChats() {
        return chats;
    }

    public ArrayList<User> searchUser(String search) {
        ArrayList<User> results = new ArrayList<>();

        for (User u : this.users) { // Adds Exact Matches First
            if (u.getUsername().equals(search)) {
                results.add(u);
            }
        }

        for (User u : this.users) { // Then Adds Close Matches
            if ((u.getUsername().contains(search)
                    || u.getUsername().equalsIgnoreCase(search))
                    && !results.contains(u)) {
                results.add(u);
            }
        }
        return results;
    }

    public boolean validateUser(String username, String password) {
        for (User currentUser : this.getUsers()) {
            if (currentUser.getUsername().equals(username)
                    && currentUser.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public boolean addFriend(String client, String friend) {
        try {
            User friendUser = getUserFromUsername(friend);
            synchronized (LOCK) {
                getUserFromUsername(friend).addFriend(client);
                getUserFromUsername(client).addFriend(friend);
            }
            return true;
        } catch (InvalidUserException e) {
            return false;
        }
    }

    public boolean addBlocked(String username, String blocked) {
        try {
            synchronized (LOCK) {
                removeFriend(username, blocked);
                return getUserFromUsername(username).addBlocked(blocked);
            }
        } catch (InvalidUserException e) {
            return false;
        }
    }

    public boolean setProfilePhoto(String username, ImageIcon photo) {
        try {
            synchronized (LOCK) {
                return getUserFromUsername(username).setProfilePhoto(photo);
            }
        } catch (InvalidUserException | InvalidPhotoException e) {
            return false;
        }
    }

    public ImageIcon getProfilePhoto(String username) {
        try {
            return getUserFromUsername(username).getProfilePhoto();
        } catch (InvalidUserException e) {
            return null;
        }
    }

    public boolean removeFriend(String username, String friendRemoved) {
        try {
            synchronized (LOCK) {
                getUserFromUsername(friendRemoved).removeFriend(username);
                return getUserFromUsername(username).removeFriend(friendRemoved);
            }
        } catch (InvalidUserException e) {
            return false;
        }
    }

    public boolean removeBlocked(String username, String blockedRemoved) {
        try {
            synchronized (LOCK) {
                return getUserFromUsername(username).removeBlocked(blockedRemoved);
            }
        } catch (InvalidUserException e) {
            return false;
        }
    }

    public boolean sendMessage(String username, String otherUsername, Message message) {
        try {
            synchronized (LOCK) {
                for (int i = 0; i < getUserFromUsername(username).getChats().size(); i++) {
                    Chat chat = getUserFromUsername(username).getChats().get(i);
                    // In the list of chats of username.
                    // If the first or second user has the same username as otherUser.
                    if (chat.getUsers()[0].getUsername().equals(otherUsername) ||
                            chat.getUsers()[1].getUsername().equals(otherUsername)) {
                        // Send Message To Chat
                        getUserFromUsername(username).message(chat, message);
                    }
                }
            }
            return true;
        } catch (InvalidUserException e) {
            return false;
        }
    }

    // Friend and block merged because of repetition.
    public ArrayList<String> getUserList(String username, String instanceField) {
        User user;
        try {
            user = getUserFromUsername(username);
        } catch (InvalidUserException e) {
            return null;
        }
        return switch (instanceField) {
            case "friends" -> user.getFriends();
            case "blocked" -> user.getBlocked();
            case "requests" -> user.getRequests();
            default -> null;
        };
    }

    public ArrayList<Chat> getChats(String username) {
        try {
            return getUserFromUsername(username).getChats();
        } catch (InvalidUserException e) {
            return null;
        }
    }

    public boolean getPrivacyStatus(String username) {
        try {
            return getUserFromUsername(username).getPublic();
        } catch (InvalidUserException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean setPrivacyStatus(String username, boolean privacy) {
        try {
            synchronized (LOCK) {
                getUserFromUsername(username).setPublic(privacy);
            }
            return true;
        } catch (InvalidUserException e) {
            return false;
        }
    }

    public String showProfile(String currUser, String profileUser) {
        User user;
        boolean isPub;
        boolean areFriends;
        try {
            user = getUserFromUsername(profileUser);
            isPub = user.getPublic();
            areFriends = user.isFriends(currUser);
        } catch (InvalidUserException e) {
            throw new RuntimeException(e);
        }

        return String.format("Username: %s\n" +
                "Public: %b\n" +
                "Are you friends? %b", profileUser, isPub, areFriends);
    }
}
