# Etapa de construcción
FROM gradle:8.13-jdk21 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
# Usamos el wrapper y omitimos tests
RUN ./gradlew build --no-daemon -x test

# Etapa de ejecución
FROM openjdk:21-jdk-slim
EXPOSE 8080
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

