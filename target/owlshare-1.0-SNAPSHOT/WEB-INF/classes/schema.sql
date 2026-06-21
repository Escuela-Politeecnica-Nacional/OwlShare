-- Esquema base para OwlShare (PostgreSQL)
-- Hibernate también puede crear/actualizar la tabla con hbm2ddl.auto=update

CREATE TABLE IF NOT EXISTS usuarios (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    nombre          VARCHAR(100) NOT NULL,
    segundo_nombre  VARCHAR(100),
    apellido        VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    rol             VARCHAR(20) NOT NULL,
    semestre        VARCHAR(50),
    carrera         VARCHAR(50),
    materias        VARCHAR(1000)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios (LOWER(email));
