#!/bin/bash

remotepath="curtisnewbie.com"
jarname="file-service-build.jar"

mvn clean package -Dmaven.test.skip=true  

mvnpkg=$?

if [ ! $mvnpkg -eq 0 ] 
then
    exit -1
fi

scp "target/${jarname}" "zhuangyongj@${remotepath}:~/services/file-service/file-server.jar"
