FROM eclipse-temurin:17-jdk-jammy
ARG JAR_FILE=target/*.jar
WORKDIR /app
COPY ${JAR_FILE} ./course-management.jar
ENTRYPOINT ["java", "-jar", "/app/course-management.jar"]
