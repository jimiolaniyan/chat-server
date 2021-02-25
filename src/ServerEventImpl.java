import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

public class ServerEventImpl implements ServerEvent {

    Vector<ClientActions> clients;
    Vector<Message> messages;

    OutputStream messagesFileOut;
    OutputStream bufferOut;
    ObjectOutput output;

    InputStream messagesFileIn;
    InputStream bufferIn;
    ObjectInputStream input;

    public ServerEventImpl() {
        System.out.println("[INFO] Registry ready.");

        clients = new Vector<>();
        messages = new Vector<>();
        System.out.println("[INFO] Data structures defined.");

        try {
            System.out.println("[INFO] Importing previously saved messsages, this may take a while...");
            messagesFileIn = new FileInputStream("messages.dat");
            bufferIn = new BufferedInputStream(messagesFileIn);
            input = new ObjectInputStream(bufferIn);

            try {
                for (;;) {
                    Message m = (Message) input.readObject();
                    messages.add(m);
                }
            } catch (EOFException e) {
                // end of stream
                System.out.println("[INFO] Task ended. Reason : reached the end of the file.");
            } catch (IOException e) {
                // some other I/O error: print it, log it, etc.
                System.out.println("[WARNING] Task ended with errors. Reason : "+e.getMessage());
            }

            input.close();
            bufferIn.close();
            messagesFileIn.close();

            System.out.println("[INFO] Importation task is done.");
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("[INFO] messages.dat does not exists.");
            System.out.println("[INFO] If this is the first run, this is normal. It will be created automatically in a few moments.");
            System.out.println("[INFO] Otherwise, it may have been deleted or corrupted.");
        }

        try {
            System.out.println("[INFO] Opening persistency file on append mode for message saving feature.");
            messagesFileOut = new FileOutputStream("messages.dat", true);
            bufferOut = new BufferedOutputStream(messagesFileOut);
            output = new ObjectOutputStream(bufferOut);
            System.out.println("[INFO] Opened persistency file.");
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("[ERROR] Error on persistency file loading. Exit.");
            System.exit(1);
        }
    }

    @Override
    public void login(ClientActions client) throws RemoteException {
        // This is work in progress so not perfect yet
        // if (findExistingClient(client) != null) {
        // client.getMessage("Sorry username: " + client.getName() + " already exists.
        // Please select another one");
        // return;
        // }

        // ClientActionsImpl cl = (ClientActionsImpl) client;
        clients.add(client);
        System.out.println("[INFO] "+client.getName()+" has logged in.");

        if (messages.size() > 0) {
            client.getMessage("See all the messages you missed, " + client.getName() + "!");
            client.getMessages(this.messages);
        }

        broadcast("[SERVER] New client " + client.getName() + " has logged in.");
    }

    private ClientActions findExistingClient(ClientActions client) {
        return clients.stream().filter(c -> {
            boolean eq = false;
            try {
                eq = c.getName().equals(client.getName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return eq;
        }).findFirst().orElse(null);
    }

    @Override
    public void broadcast(String msg) throws RemoteException {
        for (Enumeration<ClientActions> e = clients.elements(); e.hasMoreElements();) {
            ClientActions c = (ClientActions) e.nextElement();
            try {
                c.getMessage(msg);
            } catch (RemoteException rex) {
                System.out.println("[INFO] Client is offline. Disconnected");
                clients.remove(c);
            }
        }
    }

    @Override
    public void broadcastExclude(String msg, ClientActions sender) throws RemoteException {
        if (msg != null && !msg.isEmpty()) {
            Message m = new Message(sender.getName(), msg);
            messages.add(m);
            try {
                output.writeObject(m);
                output.flush();
            } catch (IOException e1) {
                System.out.println("[ERROR] Can't write to persistency file. Reason : "+e1.getMessage());
            }

            for (Enumeration<ClientActions> e = clients.elements(); e.hasMoreElements(); ) {
                ClientActions c = (ClientActions) e.nextElement();
                try {
                    if (!c.equals(sender)) {
                        c.getMessage(sender.getName() + " - " + msg);
                    }
                } catch (RemoteException rex) {
                    System.out.println("[INFO] Client is offline. Disconnected");
                    clients.remove(c);
                }
            }
        }
    }

    public void kickAll(String reason) {
        for (Enumeration<ClientActions> e = clients.elements(); e.hasMoreElements();) {
            ClientActions c = (ClientActions) e.nextElement();
            try {
                c.kick(reason);
            } catch (RemoteException rex) {
                System.out.print("");
            }
        }
    }

    public void kick(String username, String reason) {
        for (Enumeration<ClientActions> e = clients.elements(); e.hasMoreElements();) {
            ClientActions c = (ClientActions) e.nextElement();
            try {
                if (c.getName().equals(username)) {
                    c.kick(reason);
                }
            } catch (RemoteException rex) {
                System.out.print("");
            }
        }
    }
}
