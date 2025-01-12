
/*
 * JUnit Test Case for testing user's ability to block
 * and unblock users
 *
 *  @author Isaac Riley
 *  @version November 2, 2024
 */
public class testBlocked {
    public static void main(String[] args) {
        String[] users = args[0].split("\\n");
        String[] user1Info = users[0].split(",");
        String[] user2Info = users[1].split(",");
        try {
            User user1 = new User(user1Info[0], user1Info[1]);
            User user2 = new User(user2Info[0], user2Info[1]);
            // User1 blocks user2
            if (user1.addBlocked("username2")) {
                System.out.println("Successfully blocked user.");
                // Checks if user1 can block self
                if (user1.addBlocked("username1")) {
                    System.out.println("Allowed to block self.");
                } // Else prints: You cannot block yourself!
                // Checks if user1 can block same user again
                if (user1.addBlocked("username2")) {
                    System.out.println("Allowed to block user twice");
                } //Else prints: That user is already blocked!
                // Checks if their blocked user is stored
                if (user1.getBlocked().get(0).equals("username2")) {
                    System.out.println("User stored correctly.");
                } else {
                    System.out.println("User did not store correctly.");
                }
                // Unblocks user2
                if (user1.removeBlocked("username2")) {
                    // Checks if user2 is no longer blocked
                    if (user1.getBlocked().isEmpty()) {
                        System.out.println("User successfully unblocked.");
                    } else {
                        System.out.println("User not unblocked.");
                    }
                } else {
                    System.out.println("User not unblocked.");
                }
            } else {
                System.out.println("Failed to block user");
            }
        } catch (InvalidUserException e) {
            e.printStackTrace();
        }
    }
}
