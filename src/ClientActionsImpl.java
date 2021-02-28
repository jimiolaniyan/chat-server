import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.JTextArea;

public class ClientActionsImpl implements ClientActions {

    String name;
    JTextArea out;

    public ClientActionsImpl(String n, JTextArea o) {
        name = n;
        out = o;
    }

    public ClientActionsImpl(String n) {
        // For textual client, with a default value
        name = n;
        out = null;
    }

    @Override
    public void getMessage(String msg) throws RemoteException {
        // Prints logs on sysout in any case
        System.out.println(msg);
        if (out != null) {
            out.append(msg+"\n");
        }
    }

    @Override
    public void getMessages(Vector<Message> messages) throws RemoteException {
        // Prints logs on sysout in any case
        messages.forEach(System.out::println);
        if (out != null) {
            for (Message message : messages) {
                out.append(message.toString()+"\n");
            }
        }
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public void kick(String reason) throws RemoteException {
        System.out.println("You have been kicked from the server. Reason : "+reason);
        System.exit(2);
    }

}
