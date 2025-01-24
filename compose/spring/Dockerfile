# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 as builder

# Set working directory inside the container
WORKDIR /app

# Copy only the files necessary for dependency resolution and caching
COPY pom.xml .
COPY src ./src

# Build the application and skip tests for faster builds in non-production environments
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM eclipse-temurin:17-jre

# Set working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
