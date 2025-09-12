# Start with a base JDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file (make sure you build it first using mvn/gradle)
COPY target/BHIMSMSserviceAPI-0.0.1-SNAPSHOT.jar app.jar

RUN apt-get update && apt-get install -y curl redis-tools postgresql-client

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
