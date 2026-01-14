FROM alpine/java:22-jdk

LABEL authors="lucien"

WORKDIR /usr/src/app

COPY src/RandomString.java .

RUN javac RandomString.java

ENTRYPOINT ["java", "RandomString"]