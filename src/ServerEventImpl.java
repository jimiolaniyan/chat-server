import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

public class ServerEventImpl implements ServerEvent {

    Vector<ClientActions> clients;

    public ServerEventImpl() {
        clients = new Vector<ClientActions>();
        System.out.println("Registry ready.");
    }

    @Override
    public void login(ClientActions client) throws RemoteException {
        System.out.println("request");
        //ClientActionsImpl cl = (ClientActionsImpl) client;
        clients.add(client);
        System.out.println("added");
        broadcast("[INFO] New client "+client.getName()+" has logged in");
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
        for(Enumeration<ClientActions> e = clients.elements(); e.hasMoreElements();) {
            ClientActions c = (ClientActions) e.nextElement();
            try {
                if (c != sender) {
                    c.getMessage(sender.getName()+" - "+msg);
                }
            } catch (RemoteException rex) {
                System.out.println("Client is offline. Disconnected");
                clients.remove(c);
            }
        }
    }
}
