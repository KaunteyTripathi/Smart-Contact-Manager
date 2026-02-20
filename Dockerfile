# Stage 1: Build the jar
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven files first to cache dependencies
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Build the Spring Boot jar
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the jar
FROM eclipse-temurin:21-jdk  # Use the full JDK image, slim not available

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/SmartContactManager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]