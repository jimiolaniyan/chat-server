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

            boolean doesNameExists;

            // Get client name
            Scanner input = new Scanner(System.in);
            String name;
            do {
                System.out.print("Please enter your client name : ");
                name =  input.nextLine();
                doesNameExists = se.checkUsernameExists(name);
                if (doesNameExists == true) {
                    System.out.println("Sorry, the requested username is already in use. Please enter a new name.");
                }
            } while (doesNameExists == true);

            ClientActions ca = (ClientActions) UnicastRemoteObject.exportObject(new ClientActionsImpl(name), 0);
            se.login(ca);

            System.out.println("Welcome to the chatroom "+ca.getName()+" !");

            String line;

            do {
                line =  input.nextLine();
                if (!line.equals("/close"))
                    if (line.startsWith("/history ")) {
                        if (line.split(" ").length == 2) {
                            //se.kick(line.split(" ")[1], line.replace("/kick "+line.split(" ")[1]+" ", ""));
                            if (line.split(" ")[1] == "all") {
                                se.history(-1, ca);
                            } else {
                                try {
                                    Integer i = Integer.parseInt(line.split(" ")[1]);
                                    se.history(i, ca);
                                } catch (Exception e) {
                                    System.out.println("Please provide a valid number (or 'all' for all history).");
                                }
                            }
                        } else {
                            System.out.println("Usage: /history nb_of_messages or /history all");
                        }
                    } else {
                        se.broadcast(line, ca);
                    }
            } while(!line.equals("/close"));

            input.close();
            se.broadcast("[INFO] "+name+" has logged out.", null);
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Error on client :" + e);
            e.printStackTrace();
        }
    }

}