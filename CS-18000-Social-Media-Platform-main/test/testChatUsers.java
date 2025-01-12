
/*
* JUnit Test Case for ChatUsers functionality
*
*  @author Isaac Riley
*  @version November 1, 2024
 */
public class testChatUsers {
    public static void main(String[] args) {
        String[] users = args[0].split("\\n");
        String[] user1Info = users[0].split(",");
        String[] user2Info = users[1].split(",");
        String[] user3Info = users[2].split(",");
        try {
            // Creates users and array of users
            User user1 = new User(user1Info[0], user1Info[1]);
            User user2 = new User(user2Info[0], user2Info[1]);
            User user3 = new User(user3Info[0], user3Info[1]);

            User[] chatUsers = {user1, user2};
            // Creates new chat and checks that users were stored correctly
            Chat chat = new Chat(chatUsers,users[3]);
            User[] returned = chat.getUsers();
            // Prints users in chat
            for (User u : returned) {
                System.out.println(u.getUsername() + " " + u.getPassword());
            }
            // Sets users in chat to different users
            chat.setUser(0, user3);
            chat.setUser(1, user1);
            // Prints new users in chat
            for (User u2 : returned) {
                System.out.println(u2.getUsername() + " " + u2.getPassword());
            }

        } catch (InvalidUserException e) {
            e.printStackTrace();
            System.out.println("User Invalid");
        }
    }
}
