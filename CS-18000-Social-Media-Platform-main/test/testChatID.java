
/*
* JUnit Test Case for ChatID functionality
*
*  @author Isaac Riley
*  @version November 2, 2024
 */
public class testChatID {
    public static void main(String[] args) {
        String[] userData = args[0].split("\\n");
        String[] user1Info = userData[0].split(",");
        String[] user2Info = userData[1].split(",");
        try {
            // Creates users and list of users
            User user1 = new User(user1Info[0], user1Info[1]);
            User user2 = new User(user2Info[0], user2Info[1]);

            User[] users1 = {user1, user2};
            User[] users2 = {user2, user1};

            // CHAT CONSTRUCTOR CHANGED - Ben
            Chat chat1 = new Chat(users1);
            // Prints historyIn file name
            System.out.println(chat1.getHistoryIn());

        } catch (InvalidUserException e) {
            e.printStackTrace();
            System.out.println("Invalid User!");
        }


    }
}
