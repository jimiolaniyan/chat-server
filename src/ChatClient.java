import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.*;
import java.awt.event.ActionListener;

public class ChatClient extends JFrame {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.out.println("Usage: java ChatClient <rmiregistry host>");
                return;
            }

            // Window
            JFrame win = new JFrame("Chat Client");
            win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            win.setSize(600, 400);

            // Panel
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            JLabel yourTxt = new JLabel("Enter your username : ");
            JTextField inField = new JTextField(150);
            JButton send = new JButton("Send");
            panel.add(yourTxt);
            panel.add(inField);
            panel.add(send);

            // Output
            JTextArea printer = new JTextArea("Welcome!\n");
            printer.setEditable(false);
            JScrollPane sp = new JScrollPane(printer);
            DefaultCaret caret = (DefaultCaret)printer.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

            // Add to window
            win.getContentPane().add(BorderLayout.SOUTH, panel);
            win.getContentPane().add(BorderLayout.CENTER, sp);

            // Display
            win.setVisible(true);

            String host = args[0];
            Registry registry = LocateRegistry.getRegistry(host);

            ServerEvent se = (ServerEvent) registry.lookup("ChatService");

            printer.append("Please enter your client name\n");
            
            // One dynamic action listener. Can be improved, but good enough for a first GUI
            send.addActionListener(e ->
            {
                // Tries to login
                try {
                    String name = inField.getText();
                    inField.setText("");
                    boolean doesNameExists_gui = se.checkUsernameExists(name);
                    if (doesNameExists_gui) {
                        printer.append("Sorry, the requested username is already in use. Please enter a new name.\n");
                    } else {
                        // Login OK
                        printer.append("Name available ! Logging you in...\n\n");
                        ClientActions ca = (ClientActions) UnicastRemoteObject.exportObject(new ClientActionsImpl(name, printer), 0);
                        se.login(ca);
                        
                        // Remove ourselves from listener
                        for (ActionListener a : send.getActionListeners()) {
                            send.removeActionListener(a);
                        }

                        yourTxt.setText("Your message : ");

                        printer.append("Welcome to the chatroom, "+ca.getName()+" !\n");

                        // Bind the new listener on the button for normal flow
                        send.addActionListener(f -> {
                            // Original command/message loop, but in a listener
                            try {
                                String line;
                                line = inField.getText();
                                inField.setText("");
                                // Command treatment
                                if (line.startsWith("/history ")) {
                                    if (line.split(" ").length == 2) {
                                        if (line.split(" ")[1].equals("all")) {
                                            se.history(-1, ca);
                                        } else {
                                            try {
                                                Integer i = Integer.parseInt(line.split(" ")[1]);
                                                se.history(i, ca);
                                            } catch (Exception exp) {
                                                printer.append("Please provide a valid number (or 'all' for all history).\n");
                                            }
                                        }
                                    } else {                                            
                                        printer.append("Usage: /history nb_of_messages or /history all\n");
                                    }
                                } else if (line.startsWith("/close")){
                                    se.broadcast("[INFO] "+ca.getName()+" has logged out.", null);
                                    System.exit(0);
                                } else {
                                    // Default behavior : write message on self display + send to others
                                    printer.append(line+"\n");
                                    se.broadcast(line, ca);
                                }  
                            } catch (Exception exp) {
                                printer.append("An error occured. Please check the internet connection and restart the application.\n");
                            }
                        });
                    }

                } catch (Exception ex) {
                    printer.append("An error occured. Please restart the application, and verify your internet connection.\n");
                }
            });

        } catch (Exception e) {
            System.err.println("Error on client :" + e);
            e.printStackTrace();
        }
    }

}