# Etapa de construcción
FROM gradle:8.13-jdk21 AS build

WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .

# Construye sin tests
RUN ./gradlew bootJar --no-daemon -x test

# Etapa de ejecución
FROM openjdk:21-jdk-slim

WORKDIR /app
EXPOSE 8080

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]


