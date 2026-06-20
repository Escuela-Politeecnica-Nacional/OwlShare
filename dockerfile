# Etapa 1: Compilación de la aplicación
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

# Copiar el archivo de configuración de Maven (POM) y las fuentes del proyecto
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar el proyecto generando el archivo WAR
RUN mvn clean package -DskipTests

# Etapa 2: Imagen oficial de Tomcat para la ejecución en Azure
# Usamos Tomcat 10.1 (que coincide con tu pom.xml) y JDK 23
FROM tomcat:10.1-jdk23-temurin-alpine
WORKDIR /usr/local/tomcat

# 1. Eliminar las aplicaciones por defecto de Tomcat para evitar conflictos de rutas
RUN rm -rf webapps/*

# 2. Copiar el archivo WAR generado en la etapa anterior.
# Lo renombramos a 'ROOT.war' para que la aplicación responda directamente en la raíz (/)
COPY --from=build /app/target/owlshare-1.0-SNAPSHOT.war webapps/ROOT.war

# Tomcat corre por defecto en el puerto 8080 (coincide con tu configuración de Azure)
EXPOSE 8080

# El punto de entrada por defecto de la imagen de Tomcat levantará el servidor automáticamente,
# por lo que no es estrictamente necesario sobreescribir el ENTRYPOINT.
CMD ["catalina.sh", "run"]