# Use lightweight JDK 21 image
FROM openjdk:21

# Set working directory
WORKDIR /app

# Copy jar into container
COPY target/SmartContactManager-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render will override with PORT env variable)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","/app/app.jar"]