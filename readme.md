# Java RMI Chat

## How to compile

Please copy-paste the compile.sh.prop and rename it compile.sh, then change the CLASSPATH to put in your own absolute path to the folder containing the project.

Then, chmod the script : chmod +x compile.sh
And execute it.

The script will compile the whole project, run the rmi registry, and display it's port (first of the two rows printed at the end of the project).

You can finally use the two commented commands to launch the server and the client(s), and test the program.

## Before you recompile

Please kill the rmi registry by using the previously printed port number, otherwise it will crash when trying to relaunch the registry.

## Internal commands

### For the server

None

### For the client

/close : closes the client and terminates the connection