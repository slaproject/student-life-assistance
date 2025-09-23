FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom files first for better layer caching
COPY pom.xml ./
COPY student-app-common/pom.xml ./student-app-common/
COPY student-app-backend/pom.xml ./student-app-backend/

# Copy source code
COPY student-app-common/src ./student-app-common/src
COPY student-app-backend/src ./student-app-backend/src

# Build the application
RUN mvn -B package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/student-app-backend/target/student-app-backend-1.0.0-SNAPSHOT.jar app.jar

# Environment variables will be passed from Koyeb
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]