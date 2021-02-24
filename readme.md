# Java RMI Chat

## How to compile

Please copy-paste the compile.sh.prop and rename it compile.sh, then change the CLASSPATH to put in your own absolute path to the folder containing the project.

Then, chmod the script : chmod +x compile.sh
And execute it.

The script will check if a rmi registry is already running (if yes, it kills it), compile the whole project, run the rmi registry, and display it's port (first of the two rows printed at the end of the project).

You can finally use the two commented commands to launch the server and the client(s), and test the program.

## Features

You can send messages from any client to the others and receive messages from other clients as well.
The server retains the history of messages sent, and sends it to new users that try to log in.
It also saves them on a persistency file, and restores the whole history on each startup.  

## Internal commands

### For the server

None for now

### For the client

/close : closes the client and terminates the connection