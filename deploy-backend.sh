#!/bin/bash

fs="file-server/"
fsremote="file-server-remote/"
remotepath="curtisnewbie.com"
jarname="file-service-build.jar"

mvn clean install -Dmaven.test.skip=true -f "$fsremote/pom.xml"
mvn clean package -Dmaven.test.skip=true -f "$fs/pom.xml"

mvnpkg=$?

if [ ! $mvnpkg -eq 0 ] 
then
    exit -1
fi

scp "$fs/target/${jarname}" "zhuangyongj@${remotepath}:~/services/file-service/build/file-server.jar"
