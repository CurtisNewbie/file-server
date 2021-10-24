#!/bin/bash

# remotepath="192.168.10.128"
remotepath="192.168.31.103"

mvn clean package 

mvnpkg=$?

if [ ! $mvnpkg -eq 0 ] 
then
    exit -1
fi

scp "target/file-server-1.0.0.jar" "zhuangyongj@${remotepath}:~/services/file-service/file-server-1.0.0.jar"
