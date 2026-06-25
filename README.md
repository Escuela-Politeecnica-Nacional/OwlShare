# OwlShare
OwlShare is a simple and secure file sharing application that allows users to share files with others easily. It provides a user-friendly interface and ensures that your files are protected during the sharing process.

## Features
- Secure file sharing with end-to-end encryption
- User-friendly interface for easy navigation
- Support for multiple file formats
- Ability to share files with specific users or groups
- Option to set expiration dates for shared files

PLATAFORMA DIGITAL COLABORATIVA PARA EL INTERCAMBIO ACADÉMICO ENTRE ESTUDIANTES

Stack actual
Maven WAR (sin Spring Boot)
Servlet + JSP (Jakarta EE)
ORM con JPA (Hibernate)
PostgreSQL en la nube vía variables de entorno (DB_URL, DB_USER, DB_PASSWORD)
Configuración de la base de datos
El proyecto lee credenciales desde .env o variables de entorno del sistema.

Variables necesarias para Supabase/PostgreSQL:

DB_URL=jdbc:postgresql://db.yszzewnynkvmkeygvksh.supabase.co:5432/postgres?sslmode=require
DB_USER=postgres
DB_PASSWORD=owlshare2026
DB_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
HIBERNATE_HBM2DDL_AUTO=update
Ejecutar en local
Crea el archivo .env en la raíz del proyecto con las variables anteriores.
Ejecuta:
mvn clean test
mvn clean package
Despliegue
Crea una base PostgreSQL administrada en Supabase.
Copia la URL JDBC al .env o al panel de variables de entorno del hosting.
Sube el WAR a tu contenedor Servlet/Tomcat.
Verifica que las tablas se creen con HIBERNATE_HBM2DDL_AUTO=update.
Cuando el esquema quede estable, cambia a validate y usa migraciones.
Notas
El valor sb_publishable_* de Supabase no se usa para JDBC; sirve para API cliente.
Para producción real, conviene usar migraciones SQL y dejar HIBERNATE_HBM2DDL_AUTO=validate.