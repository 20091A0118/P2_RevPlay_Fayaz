# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/revplay-1.0.0.jar app.jar

# Copy existing uploads (songs and images) from the Git repository context
COPY uploads ./uploads

# Ensure uploads directories exist (creates them if they don't exist)
RUN mkdir -p uploads/audio uploads/images

# Expose the standard port (Render will override this using the PORT env variable)
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
