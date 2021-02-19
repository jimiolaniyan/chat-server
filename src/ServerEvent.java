import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerEvent extends Remote {
    void login(ClientActions client) throws RemoteException;
    void broadcast(String msg) throws RemoteException;
    void broadcastExclude(String msg, ClientActions sender) throws RemoteException;
}
