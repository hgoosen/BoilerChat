import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;


import java.io.*;

import static org.junit.Assert.*;

/*
* JUnit Test for Chat functionality
*
*  @author Isaac Riley
*  @version November 2, 2024
 */

@RunWith(Enclosed.class)
public class RunChatTest {
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

    // Sets up test cases
    public static class TestCase {
        // Makes receiveInput method, receives input
        private final PrintStream originalOutput = System.out;
        private final InputStream originalSysin = System.in;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testIn;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;

        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }

        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }

        private String getOutput() {
            return testOut.toString();
        }

        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }

        // Tests that chatIDs are unique & equals()
        @Test
        public void testChatConstructor() {
            // Set the input
            // Separate each input with a newline.
            String input = "username1,password1\nusername2,password2";

            // Pair the input with the expected result
            String expected = "username1_username2";

            // Runs the program with the input values
            // Replace TestProgram with the name of the class with the main method
            receiveInput(input);
            testChatID.main(new String[]{input});
            System.out.flush();

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Error! Chat IDs are not unique",
                    expected.trim(), stuOut.trim());
        }

        // Tests getChatHistory & addMessage()
        @Test
        public void testChatHistory() {
            User one = null;
            User two = null;

            try {
                one = new User("Abba", "password");
                two = new User("Baab", "password");
            } catch (Exception e) {
                fail("Could not create users.");
            }

            Chat myChat = new Chat(new User[] {one, two});
            myChat.addMessage(new Message("Abba", "Hi!"));
            myChat.addMessage(new Message("Baab", "Hey!"));

            assertEquals("Error! Chat History not returned", 2, myChat.getHistory().size());
        }

        // Tests getUser() & setUser()
        @Test
        public void testChatUsers() {
            String input = "username1,password1\nusername2,password2\nusername3,password3\nexampleHistory";

            String expected = "username1 password1\nusername2 password2\nusername3 password3\nusername1 password1";

            receiveInput(input);
            testChatUsers.main(new String[]{input});

            String stuOut = getOutput();

            stuOut = stuOut.replace("\r\n", "\n");
            Assert.assertEquals("Error! Users were not changed or did not return correctly",
                    expected.trim(), stuOut.trim());
        }

        // Tests the functionality of chat history
        @Test
        public void testHistoryIn() {

            String input = "username1,password1\nusername2,password2\nexampleHistory1\nexampleHistory2";

            String expected = "exampleHistory1 exampleHistory2";

            receiveInput(input);
            testHistoryIn.main(new String[]{input});

            String stuOut = getOutput();
            stuOut = stuOut.replace("\r\n", "\n");
            Assert.assertEquals("Error! HistoryIn was not correctly stored or returned",
                    expected.trim(), stuOut.trim());
        }

    }
}
