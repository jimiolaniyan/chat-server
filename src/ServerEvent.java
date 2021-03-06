import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerEvent extends Remote {
    void login(ClientActions client) throws RemoteException;
    void broadcast(String msg, ClientActions sender) throws RemoteException;
    void history(Integer nbOfMessages, ClientActions client) throws RemoteException;
    boolean checkUsernameExists(String username) throws RemoteException;
}
