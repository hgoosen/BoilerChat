import java.sql.SQLOutput;
/*
 * JUnit Test Case for User friend functionality
 *
 *  @author Isaac Riley
 *  @version November 2, 2024
 */
public class testFriends {
    public static void main(String[] args) {
        String[] users = args[0].split("\\n");
        String[] user1Info = users[0].split(",");
        String[] user2Info = users[1].split(",");
        try {
            // Creates users
            User user1 = new User(user1Info[0], user1Info[1]);
            User user2 = new User(user2Info[0], user2Info[1]);

            // Adds user2 as friend
            if (user1.addFriend("username2")) {
                System.out.println("Friend added.");
                // Checks if user1 can friend self
                if (user1.addFriend("username1")) {
                    System.out.println("Added self as friend.");
                } // Else prints: You cannot friend yourself!
                // Checks if user1 can friend the same user twice
                if (user1.addFriend("username2")) {
                    System.out.println("Added same user twice");
                } // Else Prints: That user is already friends with you!
                // Checks that user1's friend was stored correctly
                if (user1.getFriends().contains("username2")) {
                    System.out.println("Friend is stored correctly.");
                } else {
                    System.out.println("Friend is stored incorrectly.");
                }
                // Removes user2 as a friend
                if (user1.removeFriend("username2")) {
                    // Checks that user2 was unfriended
                    if (user1.getFriends().isEmpty()) {
                        System.out.println("Friend successfully removed.");
                    }
                } else {
                    System.out.println("removeFriend failed to find friend.");
                }
            } else {
                System.out.println("Friend failed to add");
            }
        } catch (InvalidUserException e) {
            e.printStackTrace();
        }
    }
}
