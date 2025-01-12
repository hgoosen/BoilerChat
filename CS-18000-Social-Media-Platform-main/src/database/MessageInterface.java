/**
 * An interface for the message class
 *
 * @author Keshav Sreekantham (ksreekan)
 * @version October 28, 2024
 */

public interface MessageInterface {

    String getSender();

    String getText();

    void setSender(String sender);

    void setText(String text);
}
