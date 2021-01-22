FROM maven:3-jdk-11 as builder
ADD ./app.tgz /
WORKDIR /app
RUN mvn package

FROM openjdk:11
COPY --from=builder /app/target/ibdev-1.0-SNAPSHOT.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "./app.jar"]
