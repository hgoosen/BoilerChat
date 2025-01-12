import java.io.IOException;
import java.util.ArrayList;
/*
* JUnit Test Case for Chat History functionality
*
*  @author Isaac Riley
*  @version November 2, 2024
 */
public class testChatHistory {
    public static void main(String[] args) {
        String[] messages = args[0].split("\\n");
        String[] user1Info = messages[0].split(",");
        String[] user2Info = messages[1].split(",");
        // CHAT CONSTRUCTOR CHANGED - Ben
        try {
            // Creates users, array of users, a chat, and messages
            User user1 = new User(user1Info[0], user1Info[1]);
            User user2 = new User(user2Info[0], user2Info[1]);
            User[] users = {user1, user2};
            Chat chat = new Chat(users);
            Message message1 = new Message(messages[2]);
            Message message2 = new PhotoMessage(messages[3]);
            // Adds messages to the chat
            chat.addMessage(message1);
            chat.addMessage(message2);
            // Makes ArrayList of chat history
            ArrayList<Message> history = chat.getHistory();
            // Prints chat history
            for (Message m : history) {
                if (m instanceof PhotoMessage) {
                    System.out.println(m.getSender() + " : (attached a photo)");
                } else {
                    System.out.println(m.getSender() + " : " + m.getText());
                }
            }
        } catch (InvalidUserException e) {
            e.printStackTrace();
        }

    }
}
