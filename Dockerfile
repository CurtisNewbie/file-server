FROM openjdk:11

LABEL "author"="yongjie.zhuang"
LABEL "description"="Simple file server with a static website for uploading and downloading files"

EXPOSE 8080/tcp

COPY ./target/file-server-0.0.1.jar .

ENTRYPOINT ["java", "-jar", "file-server-0.0.1.jar", "--base.path=/home/file/server"]