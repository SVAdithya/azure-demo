FROM maven:3.8.5-openjdk-11
WORKDIR /app
COPY . .
RUN mvn clean install
CMD ["java", "-jar", "target/azure-app-1.0-SNAPSHOT.jar"]
