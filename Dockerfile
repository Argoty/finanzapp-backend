# Etapa de construcción
FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y

COPY . .

# Generar el .jar sin ejecutar los tests
RUN ./gradlew bootJar --no-daemon -x test

# Etapa de ejecución
FROM openjdk:21-jdk-slim

EXPOSE 8080

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]


