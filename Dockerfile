#### ---- Stage 1: Build ---- ####
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests


#### ---- Stage 2: Runtime ---- ####
FROM eclipse-temurin:17-jdk-jammy

LABEL maintainer="dhee31" \
      description="Universal Spring Boot Docker Image"

WORKDIR /app

# Universal JAR copy
COPY --from=build /app/target/*.jar app.jar

# App port (can be overridden by Spring)
EXPOSE 8080

# Security (non-root)
RUN useradd -ms /bin/bash appuser
USER appuser

# Optimized JVM for containers
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
