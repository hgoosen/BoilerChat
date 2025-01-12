
/*
 * JUnit Test Case for public and private user functionality
 *
 *  @author Isaac Riley
 *  @version November 2, 2024
 */
public class testPublic {
    public static void main(String[] args) {
        String[] userInfo = args[0].split(",");

        try {
            // Creates user
            User user1 = new User(userInfo[0], userInfo[1]);
            // Sets user profile to public
            user1.setPublic(true);
            // Checks if user is correctly set to public
            if (user1.getPublic()) {
                System.out.println("Public changed correctly.");
            } else {
                System.out.println("Public failed to change");
            }
        } catch (InvalidUserException e) {
            e.printStackTrace();
        }
    }
}
