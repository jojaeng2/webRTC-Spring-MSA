FROM openjdk:11

ARG JAR_FILE=/target/*.jar
COPY ${JAR_FILE} /home/spring/chat.jar

COPY ${JAR_FILE} chat.jar

ENTRYPOINT ["java","-jar","chat.jar"]
