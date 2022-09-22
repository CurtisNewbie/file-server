#!/bin/bash

# --------- dependencies
fs="file-server/"
fsremote="file-server-remote/"
# ---------

# --------- build
jarname="file-service-build.jar"
# ---------

# --------- remote
remote="alphaboi@curtisnewbie.com"
remote_path="~/services/file-service/build/file-server.jar"
# ---------

mvn clean install -Dmaven.test.skip=true -f "$fsremote/pom.xml"
if [ ! $? -eq 0 ]; then
    exit -1
fi
echo "Installed ${fsremote}"

mvn clean package -Dmaven.test.skip=true -f "$fs/pom.xml"
mvnpkg=$?

if [ ! $mvnpkg -eq 0 ]; then
    exit -1
fi
echo "Installed ${fs}"

scp "$fs/target/${jarname}" "${remote}:${remote_path}"
if [ ! $mvnpkg -eq 0 ]; then
    exit -1
fi

ssh  "${remote}" "cd services; docker-compose up -d --build file-service"
