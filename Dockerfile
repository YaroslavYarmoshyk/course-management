FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/*.jar ./course-management.jar

ENTRYPOINT ["java", "-jar", "/app/course-management.jar"]
