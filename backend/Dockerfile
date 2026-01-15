# Imagen base con Java 21
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copia todo el proyecto
COPY . .

# Construye la aplicación
RUN chmod +x ./gradlew && ./gradlew clean build -x test

# Expone el puerto
EXPOSE 8080

# Ejecuta la aplicación
CMD ["java", "-jar", "build/libs/backend-0.0.1-SNAPSHOT.jar"]
