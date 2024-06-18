# Use the official OpenJDK image as a parent image
FROM openjdk:22-jdk

# Set the working directory
WORKDIR /app

# Copy the application JAR file to the container
COPY target/eventcore.jar /app/app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java","-jar","/app/app.jar"]