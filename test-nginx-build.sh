#!/bin/bash

remotepath="curtisnewbie.com"

# build angular
(
cd frontend/angular/file-server-front/; 
ng build --prod;
)

scp -r "./frontend/angular/file-server-front/dist/file-server/" "zhuangyongj@${remotepath}:/home/zhuangyongj/services/nginx/html/file-service-web/"




