import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * A class representing any one message within a chat
 *
 * @author Keshav Sreekantham (ksreekan), Benjamin Chen (chen5254), Samit Gadekar (sgadekar)
 * @version October 31, 2024
 */

public class Message implements MessageInterface, java.io.Serializable {

    private String sender;
    private String text;
    private Calendar calendar;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
        this.calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
    }

    public Message(String data) {
        String[] info = data.split(",");
        this.sender = info[0];
        this.text = info[1];
    }

    public Message() {
        this.sender = null;
        this.text = null;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimeStamp() {
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    @Override
    public String toString() {
        return String.format("(%02d:%02d) %s: %s", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), sender, text);
    }
}