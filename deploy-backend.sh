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
echo "Installed ${fsremote}"

if [ ! $? -eq 0 ]; then
    exit -1
fi

mvn clean package -Dmaven.test.skip=true -f "$fs/pom.xml"
echo "Installed ${fs}"

mvnpkg=$?

if [ ! $mvnpkg -eq 0 ]; then
    exit -1
fi

scp "$fs/target/${jarname}" "${remote}:${remote_path}"

ssh  "${remote}" "cd services; docker-compose up -d --build file-service"
