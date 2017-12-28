#!/bin/bash
cd build/classes/java/main
rmiregistry -J-Djava.rmi.rmi.server.userCodebaseOnly=false & export PID_REG=$!
java -Djava.rmi.server.useCodebaseOnly=false group.NameServer & export PID_NAME=$!
cd ../../../../
