import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

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

            // Get client name
            System.out.print("Please enter your client name : ");
            Scanner input = new Scanner(System.in);
            String name =  input.nextLine();
            //input.close();

            ClientActions ca = (ClientActions) UnicastRemoteObject.exportObject(new ClientActionsImpl(name), 0);
            se.login(ca);

            String line;

            do {
                line =  input.nextLine();
                if (!line.equals("/close"))
                    se.broadcastExclude(line, ca);
            } while(!line.equals("/close"));

            input.close();
            se.broadcast("[INFO] "+name+" has logged out.");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Error on client :" + e);
            e.printStackTrace();
        }
    }

}