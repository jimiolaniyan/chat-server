import java.io.IOException;

// Used to write the messages on the client without overloading the main thread

public class WriteRunnable implements Runnable{
    Message message;
    AppendingObjectOutputStream outputStream;

    public WriteRunnable(Message message, AppendingObjectOutputStream outputStream) {
        this.message = message;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                this.outputStream.writeObject(this.message);
                this.outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
