# Etapa de construcción
FROM gradle:8.13-jdk21-corretto AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build --no-daemon

# Etapa de ejecución
FROM openjdk:21-jdk-slim
EXPOSE 8080
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
