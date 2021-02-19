import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatServer {
    public static void main(String[] args) {
        try {
            ServerEventImpl se = new ServerEventImpl();
            ServerEvent se_stub = (ServerEvent) UnicastRemoteObject.exportObject(se, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.bind("ChatService", se_stub);

            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Error on server :" + e);
            e.printStackTrace();
        }
    }
}