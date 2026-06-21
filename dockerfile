# Etapa 1: Compilación de la aplicación (Java 23)
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Imagen oficial de Tomcat con Java 23 para evitar conflictos de bytecode
FROM tomcat:10.1-jdk23
WORKDIR /usr/local/tomcat

RUN rm -rf webapps/*

# Copiar el archivo WAR generado
COPY --from=build /app/target/owlshare-1.0-SNAPSHOT.war webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]