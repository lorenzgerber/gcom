#!/bin/bash
cd build/classes/main
# Launch the name server
java -Djava.rmi.server.useCodebaseOnly=false gcom.NameServer

