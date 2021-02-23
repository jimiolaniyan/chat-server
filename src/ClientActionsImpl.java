import java.rmi.RemoteException;
import java.util.Vector;

public class ClientActionsImpl implements ClientActions {

    String name;

    public ClientActionsImpl(String n) {
        name = n;
    }

    @Override
    public void getMessage(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public void getMessages(Vector<Message> messages) throws RemoteException {
        messages.forEach(System.out::println);
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

}
