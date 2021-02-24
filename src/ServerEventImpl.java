import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

public class ServerEventImpl implements ServerEvent {

    Vector<ClientActions> clients;
    Vector<Message> messages;

    public ServerEventImpl() {
        clients = new Vector<>();
        messages = new Vector<>();
        System.out.println("Registry ready.");
    }

    @Override
    public void login(ClientActions client) throws RemoteException {
        System.out.println("request");

        // This is work in progress so not perfect yet
        // if (findExistingClient(client) != null) {
            // client.getMessage("Sorry username: " + client.getName() + " already exists. Please select another one");
            // return;
        // }

        //ClientActionsImpl cl = (ClientActionsImpl) client;
        clients.add(client);
        System.out.println("added");

        if (messages.size() > 0) {
            client.getMessage("See all the messages you missed, " + client.getName() + "!");
            client.getMessages(this.messages);
        }

        broadcast("[INFO] New client "+client.getName()+" has logged in");
    }

    private ClientActions findExistingClient(ClientActions client) {
        return clients.stream()
                .filter(c -> {
                    boolean eq = false;
                    try {
                        eq = c.getName().equals(client.getName());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return eq;
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public void broadcast(String msg) throws RemoteException {
        for(Enumeration<ClientActions> e = clients.elements(); e.hasMoreElements();) {
            ClientActions c = (ClientActions) e.nextElement();
            try {
                c.getMessage(msg);
            } catch (RemoteException rex) {
                System.out.println("Client is offline. Disconnected");
                clients.remove(c);
            }
        }
    }

    @Override
    public void broadcastExclude(String msg, ClientActions sender) throws RemoteException {
        if (msg != null && !msg.isEmpty()) {
            Message m = new Message(sender.getName(), msg);
            messages.add(m);

            for (Enumeration<ClientActions> e = clients.elements(); e.hasMoreElements(); ) {
                ClientActions c = (ClientActions) e.nextElement();
                try {
                    if (!c.equals(sender)) {
                        c.getMessage(sender.getName() + " - " + msg);
                    }
                } catch (RemoteException rex) {
                    System.out.println("Client is offline. Disconnected");
                    clients.remove(c);
                }
            }
        }
    }
}
