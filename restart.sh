#!/bin/bash
kill -9 $PID_REG
kill -9 $PID_NAME

cd build/classes/java/main
rmiregistry -J-Djava.rmi.rmi.server.userCodebaseOnly=false & export PID_REG=$!
java -Djava.rmi.server.useCodebaseOnly=false group.NameServer & export PID_NAME=$!
cd ../../../../
