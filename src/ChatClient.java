import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.out.println("Usage: java ChatClient <rmiregistry host>");
                return;
            }

            String host = args[0];
            Registry registry = LocateRegistry.getRegistry(host);

            ServerEvent se = (ServerEvent) registry.lookup("ChatService");

            ClientActions ca = (ClientActions) UnicastRemoteObject.exportObject(new ClientActionsImpl(), 0);
            se.login(ca);
        } catch (Exception e) {
            System.err.println("Error on client :" + e);
            e.printStackTrace();
        }
    }

}