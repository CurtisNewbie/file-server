#!/bin/bash

remote="alphaboi@curtisnewbie.com"
remote_path="/home/alphaboi/services/nginx/html/file-service-web/"

# build angular
(
cd frontend/angular/file-server-front/; 
ng build --prod;
)

scp -r ./frontend/angular/file-server-front/dist/file-server/* "${remote}:${remote_path}"




