#!/bin/bash

if [ -d src/main/resources/static/ ]; then
    rm -r src/main/resources/static/
fi

mkdir src/main/resources/static/  

cp -r frontend/* src/main/resources/static/  


