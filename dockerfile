# Etapa 1: Compilación de la aplicación
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

# Copiar el archivo de configuración de Maven (POM) y las fuentes del proyecto
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar el proyecto generando el archivo JAR
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera para la ejecución en Azure
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

# Copiar el JAR explícitamente usando los datos de tu pom.xml (artifactId y version)
COPY --from=build /app/target/owlshare-1.0-SNAPSHOT.jar app.jar

# Exponer el puerto por defecto (puedes cambiarlo si tu Main levanta un servidor en otro puerto)
EXPOSE 8080

# Comando para ejecutar la aplicación usando tu clase ec.edu.epn.Main definida en el manifiesto
ENTRYPOINT ["java", "-jar", "app.jar"]