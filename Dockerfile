# Stage 1: Build the jar
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Stage 2: Run the jar
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/SmartContactManager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]