FROM maven:3.8.4-openjdk-17-slim AS build
ENV DOCKER_HOST=tcp://host.docker.internal:2375
WORKDIR /app

# First, copy only the pom.xml file and resolve dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Next, copy the source code and build the application
COPY src/ ./src/
RUN mvn package --no-transfer-progress

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar ./course-management.jar
ENTRYPOINT ["java", "-jar", "/app/course-management.jar"]
