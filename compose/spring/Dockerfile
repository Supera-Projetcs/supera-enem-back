## Build Stage
#FROM eclipse-temurin:17-jdk as builder
#
## Set the working directory
#WORKDIR /app
#
## Copy Maven wrapper and pom.xml for dependency resolution
#COPY pom.xml mvnw ./
#COPY .mvn .mvn
#
## Download dependencies to cache them
#RUN ./mvnw dependency:go-offline
#
## Copy the application source
#COPY src ./src
#
#
## Build the application
#RUN ./mvnw package -DskipTests
#
## Runtime Stage
#FROM eclipse-temurin:17-jre
#
## Set the working directory
#WORKDIR /app
#
## Copy the built JAR from the builder stage
#COPY --from=builder /app/target/*.jar app.jar
#
## Expose the port
#EXPOSE 8080
#
## Set environment variables (if any)
#ENV SPRING_PROFILES_ACTIVE=production
#
## Run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jdk
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]