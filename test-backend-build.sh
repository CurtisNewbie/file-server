#!/bin/bash

# remotepath="192.168.10.128"
remotepath="192.168.31.103"
jarname="file-server-1.0.3-SNAPSHOT.jar"

mvn clean package -Dmaven.test.skip=true  

mvnpkg=$?

if [ ! $mvnpkg -eq 0 ] 
then
    exit -1
fi

scp "target/${jarname}" "zhuangyongj@${remotepath}:~/services/file-service/file-server.jar"
