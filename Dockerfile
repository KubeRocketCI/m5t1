# Dockerfile
FROM openjdk:17-jdk-slim
COPY ./target/*.jar /home/app.jar
ENTRYPOINT ["java","-jar","/home/app.jar"]