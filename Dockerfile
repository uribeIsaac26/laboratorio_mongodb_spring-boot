# Usamos la imagen oficial de Temurin para Java 21
FROM eclipse-temurin:21-jre-alpine

# Creamos un directorio para la app
WORKDIR /app

# Copiamos el archivo JAR generado por Gradle
# Asegúrate de ejecutar ./gradlew bootJar antes
COPY build/libs/*.jar app.jar

# Exponemos el puerto de Spring Boot (por si quieres probarlo desde fuera)
EXPOSE 8080

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]