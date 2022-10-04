FROM openjdk:17-jdk-alpine

LABEL author="yongjie.zhuang"
LABEL descrption="File Service"
ENV TZ=Asia/Shanghai

COPY . /usr/src/file-service
WORKDIR /usr/src/file-service

EXPOSE 8080/tcp
EXPOSE 7010/tcp

CMD ["java", "-jar", "file-server-1.0.0.jar", "--spring.profiles.active=test"]
