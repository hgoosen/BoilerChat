import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import static org.junit.Assert.*;

/*
 * JUnit Test for User functionality
 *
 *  @author Isaac Riley
 *  @version November 2, 2024
 */

@RunWith(Enclosed.class)
// Runs user test cases
public class RunUserTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(RunChatTest.TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully.");

        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
    // Class of test cases
    public static class TestCase {
        // Makes receiveInput method, receives input
        private final PrintStream originalOutput = System.out;
        private final InputStream originalSysin = System.in;

        // Creates a new ByteArrayInputStream
        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testIn;
        // Create a new ByteArrayOutputStream
        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;

        //Sends system output to testOut
        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }

        // Resets system I/O to original
        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }

        // Gets output from test main
        private String getOutput() {
            return testOut.toString();
        }

        // Receives input from test class
        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }

        // Tests user fields and constructors to ensure fields are initialized correctly
        @Test
        public void userFieldTest() {
            String input = "username1,password1\nusername2,password2";

            String expected = "username1 password1\nusername2 password2";

            receiveInput(input);
            userFieldTests.main(new String[]{input});

            String stuOut = getOutput();

            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Error! Constructor method not working correctly.",
                    expected.trim(), stuOut.trim());
        }

        // Tests that user chats can be added, deleted, and stored correctly
        @Test
        public void addDeleteChats() {
            String input = "username1,password1\nusername2,password2";

            String expected = "Chats added.\n" +
                              "You are already in this chat!\n" +
                              "Chats are stored correctly.\n" +
                              "Chats successfully updated.";

            receiveInput(input);
            addDeleteChatsTest.main(new String[]{input});

            String stuOut = getOutput();

            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Error! Failed to add or delete chats.",
                    expected.trim(), stuOut.trim());

        }

        // Tests the functionality of adding and removing friends
        @Test
        public void friendsTest() {
            String input = "username1,password1\nusername2,username2";

            String expected = "Friend added.\n" +
                              "You cannot friend yourself!\n" +
                              "That user is already friends with you!\n" +
                              "Friend is stored correctly.\n" +
                              "Friend successfully removed.";

            receiveInput(input);
            testFriends.main(new String[]{input});

            String stuOut = getOutput();

            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Error! Failed to add or remove friends.",
                    expected.trim(), stuOut.trim());
        }

        // Tests functionality of blocking and unblocking users
        @Test
        public void blockedTest() {
            String input = "username1,password1\nusername2,username2";

            String expected = "Successfully blocked user.\n" +
                              "You cannot block yourself!\n" +
                              "That user is already blocked!\n" +
                              "User stored correctly.\n" +
                              "User successfully unblocked.";

            receiveInput(input);
            testBlocked.main(new String[]{input});

            String stuOut = getOutput();

            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Error! Failed to block or unblock user.",
                    expected.trim(), stuOut.trim());
        }

        // Tests the functionality of changing between private and public profile
        @Test
        public void publicTest() {
            String input = "username1,password1";

            String expected = "Public changed correctly.";

            receiveInput(input);
            testPublic.main(new String[]{input});

            String stuOut = getOutput();

            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Error! Failed to block or unblock user.",
                    expected.trim(), stuOut.trim());
        }

        // Tests the functionality of sending messages
        // such as if the user can send messages when they should and should not be able to do so
        @Test
        public void messageTest() {
            String input = "username1,password1\nusername2,password2\nexampleHistory\nusername1,hi there";

            String expected = "This user is on friends-only mode, and you are not friends with them.\n" +
                              "You are on friends-only mode, and you are not friends with them.\n" +
                              "You are not a participant in this chat.\n" +
                              "You have blocked this user and cannot send messages.\n" +
                              "This user has blocked you. You cannot message them.\n" +
                              "Message 1 sent successfully.\n" +
                              "Message 2 sent successfully.\n" +
                              "Message 3 sent successfully.\n" +
                              "Message 4 sent successfully.";

            receiveInput(input);
            testMessages.main(new String[]{input});

            String stuOut = getOutput();

            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Error! Failed to send message correctly.",
                    expected.trim(), stuOut.trim());
        }

/*
        @Test
        public void profilePhotoTest() {
            String input = "test_resources/img.jpg";
            BufferedImage image = null;
            ImageIcon photo = null;
            try {
                image = ImageIO.read(new File(input));
                photo = new ImageIcon(image);
            } catch (IOException e) {
               fail(e.getMessage());
            }
            // General Photo Test
            try {
                User user = new User("username","password");
                assertTrue("Failed: Profile photo not set correctly.", user.setProfilePhoto(photo));
                assertNotNull("Failed: Profile photo not set correctly.", photo);
            } catch (InvalidUserException | InvalidPhotoException e) {
                fail(e.getMessage());
            }
            // Invalid Photo Test
            input = "this_file_does_not_exist.txt";
            image = null;
            photo = null;
            try {
                image = ImageIO.read(new File(input));
                photo = new ImageIcon(image);
            } catch (IOException e) {
                // photo error should be caught here
            }
            try {
                User user = new User("username","password");
                user.setProfilePhoto(photo);
            } catch (InvalidUserException e) {
                fail(e.getMessage());
            } catch (InvalidPhotoException e) {
                return; // Test passed (caught error successfully)
            }

        }
*/

    }



}
