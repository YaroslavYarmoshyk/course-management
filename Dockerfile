FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src/ ./src/
RUN mvn clean package

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar ./course-management.jar
ENTRYPOINT ["java", "-jar", "/app/course-management.jar"]
