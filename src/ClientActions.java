import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientActions extends Remote {
    String getName() throws RemoteException;
    void getMessage(String msg) throws RemoteException;
}
