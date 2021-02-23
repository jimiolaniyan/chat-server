import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    String username;
    Date timestamp;
    String body;

    public Message(String username, String body) {
        this.username = username;
        this.body = body;
        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        return this.username + " said '" + this.body + "' at " + sdfTime.format(this.timestamp) + " on " +
                sdfDate.format(this.timestamp);
    }
}
