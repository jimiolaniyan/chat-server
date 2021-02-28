# Java RMI Chat

## How to compile

Please copy-paste the compile.sh.prop and rename it compile.sh, then change the `CLASSPATH` to put in your own absolute path to the folder containing the project.

Then, chmod the script : chmod +x compile.sh
And execute it.

The script will check if a rmi registry is already running (if yes, it kills it), compile the whole project, run the rmi registry, and display it's port (first of the two rows printed at the end of the project).

Three applications will be available : the server, the GUI client, and the text-based client.

You can finally use the three commented commands to launch the server and the client(s), and test the program.

## Features

You can send messages from any client to the others and receive messages from other clients as well.
The server retains the history of messages sent, and sends it to new users that try to log in.
It also saves them on a persistency file, and restores the whole history on each startup.  
A client can get all or part of the history through one simple /history command.

Two types of clients are available : a (legacy) text-based client, for console users, and a GUI client.
If you meet any problem with the graphical interface, we encourage you to use the text-based client.

## Details about the implementation

The server displays a prompt, but it can be hidden by the display of logs. You can still enter the commands when the prompt is not visible.

The text-based client displays no prompt on the main loop. You can write your commands and messages, and press enter to send them at any time after logging in.

The GUI has no support for keyboard shortcuts (like enter to send a message) because of how the events are implemented.
Please use the send button.

## Internal commands

### For the server

`/say [text]`  
`/kick [username] [reason]`  
`/kickall [reason]`  
`/close`

### For the client

`/history [number of messages]` : gets the `x` last messages sent on the chatroom, or all messages by using the `/history all` command.  
`/close` : closes the client and terminates the connection
