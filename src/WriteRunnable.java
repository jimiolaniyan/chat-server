import java.io.IOException;
import java.io.ObjectOutputStream;

public class WriteRunnable implements Runnable{
    Message message;
    ObjectOutputStream outputStream;

    public WriteRunnable(Message message, ObjectOutputStream outputStream) {
        this.message = message;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                this.outputStream.writeObject(this.message);
                this.outputStream.reset();
                this.outputStream.flush();
                System.out.println("[INFO] Finished writing -> '" +
                        this.message.body + "' from -> " + this.message.username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
