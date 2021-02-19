import java.rmi.Remote;

public interface ServerEvent extends Remote {
    void login(ClientActions client);
}
