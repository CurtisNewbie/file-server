#!/bin/bash

if [ -d src/main/resources/static/ ]; then
    rm -r src/main/resources/static/
fi

mkdir src/main/resources/static/  

echo "Build angular? [y/Y]"

read ans

if [ $ans == "y" ] || [ $ans == "Y" ]
then
    (
        cd frontend/angular/file-server-front/; 
        ng build;
    )
fi


(
    cd frontend/angular/file-server-front/; 
    cp -r dist/file-server/* ../../../src/main/resources/static/;
)

mvn clean package 

rm -r src/main/resources/static/  

ssh -l zhuangyongj 192.168.10.128 "/home/zhuangyongj/exec/fileserver/kill-fs.sh"

scp "target/file-server-0.0.1.jar" "zhuangyongj@192.168.10.128:~/exec/fileserver/fileserver.jar"
