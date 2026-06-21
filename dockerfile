# Etapa 1: Compilación de la aplicación (Java 23)
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Usamos directamente Java 23 base e instalamos Tomcat manualmente
FROM eclipse-temurin:23-jdk-jammy
WORKDIR /usr/local

# Definir variables para la descarga limpia de Tomcat 10
ENV TOMCAT_VERSION=10.1.24
RUN apt-get update && apt-get install -y curl && \
    curl -O https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    tar -xvf apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    mv apache-tomcat-${TOMCAT_VERSION} tomcat && \
    rm apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    rm -rf tomcat/webapps/*

WORKDIR /usr/local/tomcat

# Copiar el archivo WAR generado en la etapa de build
COPY --from=build /app/target/owlshare-1.0-SNAPSHOT.war webapps/ROOT.war

EXPOSE 8080

CMD ["./bin/catalina.sh", "run"]