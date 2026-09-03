FROM maven:3.9-eclipse-temurin-11 as builder
ADD ./app.tgz /
WORKDIR /app
RUN mvn package

FROM eclipse-temurin:11-jre
COPY --from=builder /app/target/ibdev-1.0-SNAPSHOT.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "./app.jar"]
