

/*
* JUnit Test Case for HistoryIn functionality
*
*  @author Isaac Riley
*  @version November 2, 2024
 */
public class testHistoryIn {
    public static void main(String[] args) {
        String[] historyIn = args[0].split("\\n");
        String[] user1Info = historyIn[0].split(",");
        String[] user2Info = historyIn[1].split(",");
        try {
            // Creates users, user array, and chats
            User user1 = new User(user1Info[0], user1Info[1]);
            User user2 = new User(user2Info[0], user2Info[1]);
            User[] users = {user1, user2};
            Chat chat1 = new Chat(users, historyIn[2]);
            Chat chat2 = new Chat(users, historyIn[3]);
            // Prints chat's historyIn files
            System.out.println(chat1.getHistoryIn() + " " + chat2.getHistoryIn());
        } catch (InvalidUserException e) {
            e.printStackTrace();
        }
    }
}
