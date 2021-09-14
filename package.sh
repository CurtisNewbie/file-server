#!/bin/bash

if [ -d src/main/resources/static/ ]; then
    rm -r src/main/resources/static/
fi

mkdir src/main/resources/static/  

echo "Need to build angular app? [y/Y]" 
read answer 

if [ $answer = "y" ] || [ $answer = "Y" ] 
then 
    echo "Building angular app" 

    # build angular app
    (
    cd frontend/angular/file-server-front/; 
    ng build;
    )
else  
    echo "Skip building angular app" 
fi

# copy the build to resources/static folder
echo "Copying angular build to resources/static folder"
(
cd frontend/angular/file-server-front/; 
cp -r dist/file-server/* ../../../src/main/resources/static/;
)

mvn clean package 

echo "Need to clean up .../resources/static folder? [y/Y]" 
read answer 

if [ $answer = "y" ] || [ $answer = "Y" ] 
then 
    rm -r src/main/resources/static/  
fi

ls -lh "target/file-server-1.0.0.jar"
