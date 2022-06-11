FROM openjdk:11

ARG JAR_FILE=./target/openvidu-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /home/spring/chat.jar

WORKDIR /home/spring/chat.jar

EXPOSE 8080