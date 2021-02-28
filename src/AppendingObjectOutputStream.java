import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/*
    Many thanks to Andreas Dolk for his workaround !
    https://stackoverflow.com/questions/1194656/appending-to-an-objectoutputstream

    With this, we can write serialized objects without meeting the AC error ! (at least not in our tests)
    We just need to write a dummy message with the normal ObjectOutputStream class when creating the file.

    The class is written as it is.
*/

public class AppendingObjectOutputStream extends ObjectOutputStream {

    public AppendingObjectOutputStream(OutputStream out) throws IOException {
      super(out);
    }
  
    @Override
    protected void writeStreamHeader() throws IOException {
      reset();
    }
  
  }