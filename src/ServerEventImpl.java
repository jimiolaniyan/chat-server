import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Vector;

public class ServerEventImpl implements ServerEvent {

    Vector<ClientActions> clients;
    Vector<Message> messages;

    OutputStream messagesFileOut;
    OutputStream bufferOut;
    AppendingObjectOutputStream output;

    InputStream messagesFileIn;
    InputStream bufferIn;
    ObjectInputStream input;

    ObjectOutputStream tmpSaveWriter;

    public ServerEventImpl() {
        System.out.println("[INFO] Registry ready.");

        clients = new Vector<ClientActions>();
        messages = new Vector<Message>();
        tmpSaveWriter = null;
        System.out.println("[INFO] Data structures defined.");

        boolean shouldCreate = false;

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
            System.out.println("[INFO] messages.dat does not exists.");
            System.out.println("[INFO] If this is the first run, this is normal. It will be created automatically in a few moments.");
            System.out.println("[INFO] Otherwise, it may have been deleted or corrupted.");
            // We should create the file
            shouldCreate = true;

        }

        try {
            System.out.println("[INFO] Opening persistency file on append mode for message saving feature.");
            messagesFileOut = new FileOutputStream("messages.dat", true);
            bufferOut = new BufferedOutputStream(messagesFileOut);
            if (shouldCreate) {
                // Create file by writing an object with the normal way
                System.out.println("Writing file...");
                tmpSaveWriter = new ObjectOutputStream(bufferOut);
                tmpSaveWriter.writeObject(new Message("Server", "Hello world ! This server is now set up and ready to run."));
                // Close everything, to be sure
                tmpSaveWriter.close();
                bufferOut.close();
                messagesFileOut.close();
                // And then open back all
                messagesFileOut = new FileOutputStream("messages.dat", true);
                bufferOut = new BufferedOutputStream(messagesFileOut);
            }
            // Continue with the append stream
            output = new AppendingObjectOutputStream(bufferOut);
            System.out.println("[INFO] Opened persistency file.");
        } catch (Exception e) {
            System.out.println("[ERROR] Error on persistency file loading. Exit.");
            System.exit(1);
        }
    }

    @Override
    public boolean checkUsernameExists(String username) throws RemoteException {
        return clients.stream().filter(c -> {
            boolean eq = false;
            try {
                eq = c.getName().equals(username);
            } catch (RemoteException e) {
                System.out.println("This client is gone!");
            }
            return eq;
        }).findFirst().orElse(null) != null;
    }

    @Override
    public void login(ClientActions client) throws RemoteException {
        clients.add(client);
        System.out.println("[INFO] "+client.getName()+" has logged in.");

        if (messages.size() > 0) {
            // Catch up on what everybody wrote
            client.getMessage("See all the messages you missed, " + client.getName() + "!");
            client.getMessages(this.messages);
        }

        broadcast("[SERVER] New client " + client.getName() + " has logged in.", client, true);
    }

    private void broadcast(String msg, ClientActions sender, boolean fromServer) throws RemoteException {
        if (msg != null && !msg.isEmpty()) {
            // We don't save messages from the server
            // so fromServer must be false
            if (sender != null && !fromServer) {
                Message m = new Message(sender.getName(), msg);
                messages.add(m);
                new Thread(new WriteRunnable(m, output)).start();
            }

            for (Enumeration<ClientActions> e = clients.elements(); e.hasMoreElements(); ) {
                // Send to anyone (almost, depends on the policy set by the caller)
                ClientActions c = e.nextElement();
                try {
                    if (!c.equals(sender)) {
                        // Two formats, one with the name of the sender, the other for logs and such
                        if (sender != null && !fromServer) {
                            c.getMessage(sender.getName() + " - " + msg);
                        } else {
                            c.getMessage(msg);
                        }
                    }
                } catch (RemoteException rex) {
                    System.out.println("[INFO] client has logged out");
                    clients.remove(c);
                }
            }
        }
    }

    @Override
    public void broadcast(String msg, ClientActions sender) throws RemoteException {
        // Public method available for clients
        broadcast(msg, sender, false);
    }

    public void kickAll(String reason) {
        // Goodbye all
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
        // Goodbye user
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

    public void history(Integer nbOfMessages, ClientActions client) throws RemoteException {
        // Send history to client
        if (nbOfMessages == -1) {
            client.getMessages(this.messages);
        } else {
            ListIterator<Message> iter = this.messages.listIterator(this.messages.size());
            while(iter.hasPrevious()) {
                client.getMessage(iter.previous().toString());
                nbOfMessages--;
                if (nbOfMessages <= 0) {
                    break;
                }
            }
        } 
    }
}
