# Use a base image with JDK and Gradle installed
FROM gradle:latest AS builder

# Set the working directory
WORKDIR /home/gradle/src

# Copy only the Gradle files to cache dependencies
COPY build.gradle settings.gradle /home/gradle/src/

# Copy the whole source
COPY src /home/gradle/src/src

# Build the application
RUN gradle build -x test

# Use a lighter base image
FROM amazoncorretto:21-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /home/gradle/src/build/libs/tech-challenge-*.jar /app/tech-challenge.jar
COPY src/main/resources /app/resources
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Specify the command to run the application
CMD ["java", "-jar", "tech-challenge.jar"]
