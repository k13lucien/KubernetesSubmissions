FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy source code
COPY src/PingPong.java .

# Download PostgreSQL JDBC driver
ADD https://jdbc.postgresql.org/download/postgresql-42.7.3.jar postgresql.jar

# Compile the Java program
RUN javac -cp postgresql.jar PingPong.java

# Run the program with the correct classpath
CMD ["java", "-cp", ".:postgresql.jar", "PingPong"]
