# Etapa 1: compilar el proyecto
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Etapa 2: ejecutar (imagen liviana, solo Java)
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd -r dogo
COPY --from=build /app/target/dogo-project-0.0.1-SNAPSHOT.jar app.jar
USER dogo
# Ajuste de memoria para el plan gratis (512 MB)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k"
EXPOSE 8080
# Render indica el puerto en la variable PORT
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]

