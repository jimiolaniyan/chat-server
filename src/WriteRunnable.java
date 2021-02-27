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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
