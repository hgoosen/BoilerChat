import java.util.ArrayList;

/**
 * A class representing a chat between two Users.
 *
 * @author Keshav Sreekantham (ksreekan), Benjamin Chen (chen5254)
 * @version November 16, 2024
 */


public class Chat implements ChatInterface, java.io.Serializable {

    private static int counter = 0;

    private int chatID;
    private String historyIn;
    private ArrayList<Message> history = new ArrayList<>();

    private final User[] users;

    public Chat(User[] users) { // If New Chat
        this.users = users;
        this.historyIn = users[0].getUsername() + "_" + users[1].getUsername();
        setNewChatID();
    }

    public Chat(User[] users, String historyIn) { // If Loading Existing Chat
        this.users = users;
        this.historyIn = historyIn;
        setNewChatID();
    }

    private synchronized void setNewChatID() {
        // Synchronized To Prevent ChatID Conflict
        this.chatID = counter;
        counter++;
    }

    public ArrayList<Message> getHistory() {
        return history;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUser(int user, User name) {
        this.users[user] = name;
    }

    public synchronized void addMessage(Message message) {
        // Synchronized To Prevent Message Conflict
        history.add(message);
    }

    public synchronized void removeMessages(int[] indexes) {
        // index = 0 is first message
        for (int index : indexes) {
            if (index >= 0 && index < history.size()) {
                history.remove(index);
            }
        }
    }

    public boolean equals(Chat chat) {
        return (this.chatID == chat.chatID);
    }

    // for debugging
    public int getChatID() {
        return chatID;
    }

    public String getHistoryIn() {
        return historyIn;
    }
}