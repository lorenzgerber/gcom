#!/bin/bash
cd build/classes/java/main
java -Djava.rmi.server.useCodebaseOnly=false group.NameServer &
