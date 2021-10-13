FROM ubuntu:latest

FROM openjdk:11
WORKDIR '/app'
ADD target/SmartContactManager-0.0.1-SNAPSHOT.jar SmartContactManager-0.0.1-SNAPSHOT.jar
EXPOSE 8081
COPY ../../.. .
ENTRYPOINT ["java","-jar","SmartContactManager-0.0.1-SNAPSHOT.jar"]