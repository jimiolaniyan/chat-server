import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatServer {
    public static void main(String[] args) {
        try {
            ServerEventImpl se = new ServerEventImpl();
            ServerEvent se_stub = (ServerEvent) UnicastRemoteObject.exportObject(se, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.bind("ChatService", se_stub);

            System.out.println("Server ready");

            // Get client name
            Scanner input = new Scanner(System.in);
            String line;

            do {
                System.out.print("#> ");
                line =  input.nextLine();
                if (!line.equals("/close"))
                    if (line.startsWith("/say ")) {
                        se.broadcast("[SERVER] Broadcast: "+line.replace("/say ", ""));
                    } else if (line.startsWith("/kick ")) {
                        if (line.split(" ").length >= 3) {
                            se.kick(line.split(" ")[1], line.split(" ")[2]);
                        } else {
                            System.out.println("Usage: /kick username reason");
                        }
                    } else {
                        System.out.println("Command not recognized.");
                    }
            } while(!line.equals("/close"));

            input.close();
            se.broadcast("[INFO] Server is shutting down !");
            se.kickAll("server closed.");
            registry.unbind("ChatService");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Error on server :" + e);
            e.printStackTrace();
        }
    }
}