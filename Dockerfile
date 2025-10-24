# Stage 1: Build the application using Maven
# We use the official Maven image which has mvn pre-installed.
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copy the pom.xml file first to leverage Docker layer caching
COPY pom.xml ./

# Download all dependencies
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Package the application, skipping tests
RUN mvn package -DskipTests


# Stage 2: Create the final, lightweight container image
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the executable JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
