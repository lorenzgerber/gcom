#!/bin/bash
cd bin
# Start RMI registry
rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false
# Launch the name server
java -Djava.rmi.server.useCodebaseOnly=false gcom.NameServer

