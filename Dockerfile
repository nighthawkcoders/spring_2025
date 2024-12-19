# syntax=docker/dockerfile:1
FROM openjdk:18-alpine3.13
WORKDIR /app
RUN apk update && apk upgrade && \
    apk add --no-cache git 
RUN apt-get update && apt-get install -y libpulse0
COPY . /app
RUN ./mvnw package
CMD ["java", "-jar", "target/spring-0.0.1-SNAPSHOT.jar"]
EXPOSE 8085
