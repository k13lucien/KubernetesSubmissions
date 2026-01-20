FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY src/PingPong.java .
RUN javac PingPong.java
CMD ["java", "PingPong"]