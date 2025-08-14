# ===== Build stage =====
FROM maven:3.9.6-eclipse-temurin-22 AS build
WORKDIR /workspace

# Copy Maven project files first for better layer caching
COPY pom.xml ./
COPY azure-app/pom.xml azure-app/pom.xml
COPY azure-blob/pom.xml azure-blob/pom.xml
COPY azure-cosmos/pom.xml azure-cosmos/pom.xml

# Pre-fetch dependencies
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy sources
COPY . .

# Build selected module (and its dependencies)
ARG MODULE=azure-app
RUN mvn -q -DskipTests -pl ${MODULE} -am package

# ===== Runtime stage =====
FROM eclipse-temurin:22-jre-alpine
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
WORKDIR /app

# Copy the fat jar. Assumes artifactId == module directory name.
ARG MODULE=azure-app
COPY --from=build /workspace/${MODULE}/target/${MODULE}-*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
