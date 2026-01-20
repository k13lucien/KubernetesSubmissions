FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY . /app

RUN ./mvnw clean package -DskipTests

RUN mkdir -p /usr/src/app/cache

CMD ["java", "-jar", "target/todo-app-0.0.1-SNAPSHOT.jar"]