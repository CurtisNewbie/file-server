#!/bin/bash

if [ -d src/main/resources/static/ ]; then
    rm -r src/main/resources/static/
fi

mkdir src/main/resources/static/  

echo "Build angular? [y/Y]"

read ans

if [ -z $and ]
then
    if [ $ans == "y" ] || [ $ans == "Y" ]
    then
        (
        cd frontend/angular/file-server-front/; 
        ng build --prod;
        )
    fi


    (
    cd frontend/angular/file-server-front/; 
    cp -r dist/file-server/* ../../../src/main/resources/static/;
    )
fi

mvn clean package 

mvnpkg=$?

rm -r src/main/resources/static/  

if [ ! $mvnpkg -eq 0 ] 
then
    exit -1
fi

# ssh -l zhuangyongj 192.168.10.128 "/home/zhuangyongj/exec/fileserver/kill-fs.sh"

scp "target/file-server-1.0.0.jar" "zhuangyongj@192.168.10.128:~/services/file-service/file-server-1.0.0.jar"
