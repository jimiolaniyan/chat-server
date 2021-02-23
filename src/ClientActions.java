import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface ClientActions extends Remote {
    String getName() throws RemoteException;
    void getMessage(String msg) throws RemoteException;
    void getMessages(Vector<Message> msg) throws RemoteException;
}
