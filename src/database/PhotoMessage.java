import javax.swing.*;

/**
 * A class representing a message containing a photo
 *
 * @author Henri Goosen (hgoosen), Keshav Sreekantham (ksreekan), Benjamin Chen (chen5254), Samit Gadekar (sgadekar)
 * @version Nov. 3, 2024
 */

public class PhotoMessage extends Message {
    private ImageIcon photo;

    public PhotoMessage(String sender, String text, ImageIcon photo) {
        super(sender, text);
        this.photo = photo;
    }

    public PhotoMessage(String sender, ImageIcon photo) {
        super(sender, "");
        this.photo = photo;
    }

    public PhotoMessage(String data) {
        super(data);
        String[] splitData = data.split(",");
        try {
            this.photo = new ImageIcon(splitData[2]);
        } catch (Exception e) {
            this.photo = null;
        }
    }

    public PhotoMessage() {
        super();
        this.photo = null;
    }

    public ImageIcon getPhoto() {
        return this.photo;
    }

    public void setPhoto(ImageIcon photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return super.toString() + " : " + (this.photo == null ? "(no photo found)" : "(attached a photo)");
    }
}