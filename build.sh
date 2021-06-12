#!/bin/bash

if [ -d src/main/resources/static/ ]; then
    rm -r src/main/resources/static/
fi

mkdir src/main/resources/static/  

# build angular app, and copy the build to resources/static folder
(
    cd frontend/angular/file-server-front/; 
    ng build;
    cp -r dist/file-server/* ../../../src/main/resources/static/;
)

# cp -r frontend/vanilla/* src/main/resources/static/  

mvn clean package 

rm -r src/main/resources/static/  

cp ./target/file-server-0.0.1.jar .

