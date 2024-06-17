# Use the official Gradle image to build the application
FROM gradle:7.4.2-jdk17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Install necessary tools
USER root
RUN apt-get update && apt-get install -y openjdk-21-jdk

# Set JAVA_HOME for Java 21
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Copy the entire project directory into the container
COPY . .

# Ensure gradlew has executable permissions
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build --no-daemon

# Use a lightweight OpenJDK image to run the application
FROM openjdk:21-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built application from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
