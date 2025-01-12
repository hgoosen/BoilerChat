import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import javax.swing.*;

import static org.junit.Assert.*;

/**
 * JUnit Test for the PhotoMessage class functionality
 *
 * @author Henri Goosen (hgoosen)
 * @version November 3, 2024
 */

@RunWith(Enclosed.class)
public class RunPhotoMessageTest {

    // Runs all the tests in this class and outputs the information of any failed tests
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(RunPhotoMessageTest.class);
        if (result.wasSuccessful()) {
            System.out.println("Success: PhotoMessage Test ran successfully.");

        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println("Failure: " + failure.toString());
            }
        }
    }

    /**
     * Defines the individual test cases for the PhotoMessage class
     */
    public static class TestCase {

        private PhotoMessage photoMessage;
        
        @Before
        public void setUp() {
            // Runs a clean instance of PhotoMessage before each test
            photoMessage = new PhotoMessage("senderName", new ImageIcon("test_resources/img.jpg"));
        }
    
        @Test
        public void testConstructorWithSenderAndText() {
            PhotoMessage msg = null;
            msg = new PhotoMessage("Adam", "penguin", new ImageIcon("test_resources/img.jpg"));
            // Checks data of the PhotoMessage
            assertEquals("Sender and Text constructor for PhotoMessage did not set Sender correctly", "Adam", msg.getSender());
            assertNotNull("Sender and Text constructor for PhotoMessage did not set Photo correctly", msg.getPhoto());
        }
    
        @Test
        public void testConstructorWithData() {
            PhotoMessage msg = null;
            msg = new PhotoMessage("Bob,,test_resources/img.jpg");

            // Checks data of the PhotoMessage
            assertEquals("Data constructor for PhotoMessage did not set Sender correctly", "Bob", msg.getSender());
            assertEquals("Data constructor for PhotoMessage did not set Text correctly","" , msg.getText());
            assertNotNull("Data constructor for PhotoMessage did not set Photo correctly", msg.getPhoto());
        }
    
        @Test
        public void testGetSender() {
            assertEquals("Failure: PhotoMessage failed getSender()", "senderName", photoMessage.getSender());
        }
    
        @Test
        public void testGetText() {
            assertEquals("Failure: PhotoMessage failed getText()", "", photoMessage.getText());
        }
    
        @Test
        public void testSetSender() {
            photoMessage.setSender("newSenderName");
            assertEquals("Failure: PhotoMessage failed setSender()", "newSenderName", photoMessage.getSender());
        }
    
        @Test
        public void testSetText() {
            photoMessage.setText("New hello world!");
            assertEquals("Failure: PhotoMessage failed setText()", "New hello world!", photoMessage.getText());
        }
    
        @Test
        public void testToString() {
            assertTrue("Failure: PhotoMessage failed toString()", photoMessage.toString().contains("senderName") && photoMessage.toString().contains("photo"));
            
            // Update the PhotoMessage and test again
            photoMessage.setSender("Chris");
            photoMessage.setPhoto(new ImageIcon("test_resources/img.jpg"));
            assertTrue("Failure: PhotoMessage failed toString() after modification", photoMessage.toString().contains("Chris") && photoMessage.toString().contains("photo"));
        }
    }
    
}
