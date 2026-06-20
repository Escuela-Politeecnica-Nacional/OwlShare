# Etapa 1: Compilación de la aplicación (Java 23)
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

# Copiar el archivo de configuración de Maven (POM) y las fuentes del proyecto
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar el proyecto generando el archivo WAR
RUN mvn clean package -DskipTests

# Etapa 2: Imagen oficial de Tomcat compatible con Java moderno
FROM tomcat:10.1-jdk21-temurin-jammy
WORKDIR /usr/local/tomcat

# 1. Eliminar las aplicaciones por defecto de Tomcat para evitar conflictos
RUN rm -rf webapps/*

# 2. Copiar el archivo WAR generado en la etapa anterior como ROOT.war
COPY --from=build /app/target/owlshare-1.0-SNAPSHOT.war webapps/ROOT.war

# Exponer el puerto por defecto de Tomcat para Azure Web App
EXPOSE 8080

CMD ["catalina.sh", "run"]