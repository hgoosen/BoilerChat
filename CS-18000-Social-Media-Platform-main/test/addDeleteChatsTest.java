
/*
 * JUnit Test Case for adding and deleting user chats
 *
 *  @author Isaac Riley
 *  @version November 2, 2024
 */
public class addDeleteChatsTest {
    public static void main(String[] args) {
        //add and delete Chats
        //add & delete & check if already in chats and boolean returns
        String[] userChats = args[0].split("\\n");
        String[] user1Info = userChats[0].split(",");
        String[] user2Info = userChats[1].split(",");
        try {
            User user1 = new User(user1Info[0],user1Info[1]);
            User user2 = new User(user2Info[0],user2Info[1]);
            User[] users = {user1, user2};
            Chat chat1 = new Chat(users);
            Chat chat2 = new Chat(users);


            if (user1.addChat(chat1) && user1.addChat(chat2)) { // Adds user to 2 chats
                System.out.println("Chats added.");
                if (!(user1.addChat(chat1))) { // Checks if you can add 2 of the same chat
                    // Prints "You are already in this chat."
                    if (user1.getChats().get(0).equals(chat1) && user1.getChats().get(1).equals(chat2)) { // Checks that chats are stored in correct order
                        System.out.println("Chats are stored correctly.");
                        if (user1.deleteChat("username2")) { // Checks if chat is successfully deleted
                            if (user1.getChats().getFirst().equals(chat2)) {
                                System.out.println("Chats successfully updated.");
                            } else {
                                System.out.println("Chats failed to update");
                            }
                        } else {
                            System.out.println("Chat was not removed.");
                        }
                    } else {
                        System.out.println("Chats are not stored correctly.");
                    }
                } else {
                    System.out.println("User added chat they were already in.");
                }
            } else {
                System.out.println("User failed to add chat.");
            }
        } catch (InvalidUserException e) {
            e.printStackTrace();
        }



    }
}
