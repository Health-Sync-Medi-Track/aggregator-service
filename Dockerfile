# Use a lightweight base image with OpenJDK 11
FROM openjdk:11-jdk-slim

# Set an argument for the jar file location
ARG JAR_FILE=target/*.jar

# Copy the application jar into the container
COPY ${JAR_FILE} application.jar

# Expose the application port
EXPOSE 8080

# Set the entrypoint to run the jar file
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-Xms256m", "-Xmx512m", "-jar", "application.jar"]


