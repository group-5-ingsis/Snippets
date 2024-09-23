# Use JDK 21
FROM openjdk:21-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy only the Gradle wrapper files first to cache Gradle installation
COPY gradlew ./
COPY gradle ./gradle

# Download and cache the Gradle distribution
RUN ./gradlew --version

# Copy the rest of the application code
COPY . .

# Expose the application port (this should match the port defined in application.properties)
EXPOSE 8082

# Run the Spring Boot application using Gradle
CMD ["./gradlew", "bootRun"]