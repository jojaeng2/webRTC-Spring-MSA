FROM openjdk:11

ARG JAR_FILE=/target/*.jar
COPY ${JAR_FILE} /home/spring/chat.jar

WORKDIR /home/spring

EXPOSE 8080