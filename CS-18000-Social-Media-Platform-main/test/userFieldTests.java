
/*
 * JUnit Test for User constructor and
 * User fields
 *
 *  @author Isaac Riley
 *  @version November 2, 2024
 */
public class userFieldTests {
    public static void main(String[] args) {
        // fieldTests
        String[] users = args[0].split("\\n");
        String[] user1Info = users[0].split(",");
        String[] user2Info = users[1].split(",");
        // Creates two users
        try {
            User user1 = new User(user1Info[0], user1Info[1]);
            User user2 = new User(user2Info[0], user2Info[1]);
            // Checks if all fields are initialized correctly
            if ((user1.getChats() != null)
                   && (user2.getFriends() != null)
                  && (user1.getBlocked() != null)
                  && (user1.getPublic())) {
                // Prints username and password
                System.out.println(user1.getUsername() + " " + user1.getPassword() + "\n" +
                     user2.getUsername() + " " + user2.getPassword());
         } else {
              System.out.println("ArrayLists are null!");
            }
        } catch (InvalidUserException e) {
            e.printStackTrace();
        }




    }
}
