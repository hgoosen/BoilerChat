import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.assertEquals;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * JUnit Test for the Message class functionality
 *
 * @author Samit Gadekar (SamitGadekar)
 * @version November 2, 2024
 */

@RunWith(Enclosed.class)
public class RunMessageTest {

    // Runs all the tests in this class and outputs the information of any failed tests
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(RunMessageTest.class);
        if (result.wasSuccessful()) {
            System.out.println("Success: Message Test ran successfully.");

        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println("Failure: " + failure.toString());
            }
        }
    }

    /**
     * Defines the individual test cases for the Message class
     */
    public static class TestCase {

        private Message message;
        
        @Before
        public void setUp() {
            // Runs a clean instance of message before each test
            message = new Message("senderName", "Hello world!");
        }
    
        @Test
        public void testConstructorWithSenderAndText() {
            Message msg = new Message("Adam", "Hi there!");
            // Checks data of the message
            assertEquals("Sender and Text constuctor for Message did not set Sender correctly", "Adam", msg.getSender());
            assertEquals("Sender and Text constuctor for Message did not set Text correctly", "Hi there!", msg.getText());
        }
    
        @Test
        public void testConstructorWithData() {
            Message msg = new Message("Bob,What's up?");
            // Checks data of the message
            assertEquals("Data constuctor for Message did not set Sender correctly", "Bob", msg.getSender());
            assertEquals("Data constuctor for Message did not set Text correctly", "What's up?", msg.getText());
        }
    
        @Test
        public void testGetSender() {
            assertEquals("Failure: Message failed getSender()", "senderName", message.getSender());
        }
    
        @Test
        public void testGetText() {
            assertEquals("Failure: Message failed getText()", "Hello world!", message.getText());
        }
    
        @Test
        public void testSetSender() {
            message.setSender("newSenderName");
            assertEquals("Failure: Message failed setSender()", "newSenderName", message.getSender());
        }
    
        @Test
        public void testSetText() {
            message.setText("New hello world!");
            assertEquals("Failure: Message failed setText()", "New hello world!", message.getText());
        }
    
        @Test
        public void testToString() {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
            assertEquals("Failure: Message failed toString()", String.format("(%02d:%02d) %s: %s" , calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), message.getSender(), message.getText()) , message.toString());


            // Update the message and test again
            message.setSender("Chris");
            message.setText("Hey guys!");
            assertEquals("Failure: Message failed toString() after modification", String.format("(%02d:%02d) %s: %s" , calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), message.getSender(), message.getText()), message.toString());
        }
    }
    
}
