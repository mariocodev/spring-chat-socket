# Etapa de build: Compila con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Descargamos dependencias para aprovechar la caché de Docker
RUN mvn dependency:go-offline
COPY src ./src
# Compilamos usando el perfil prod de Maven para excluir lo innecesario
RUN mvn clean package -DskipTests -Pprod

# Etapa de runtime: Ejecuta el JAR
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/spring-chat-backend.jar app.jar

#EXPOSE ${PORT:8080}

#HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
#  CMD curl -f ${URL_ACTUATOR} || exit 1

# Al arrancar, le pasamos el perfil activo de Spring por defecto
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}", "-jar", "app.jar"]