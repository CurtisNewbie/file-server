#!/bin/bash

mkdir src/main/resources/static/  
cp -r frontend/* src/main/resources/static/  
mvn clean package 
# rm -r src/main/resources/static/  
cp ./**/*.jar .

