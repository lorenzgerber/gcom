#!/bin/bash
cd build/classes/java/main
rmiregistry -J-Djava.rmi.rmi.server.useCodebaseOnly=false &
