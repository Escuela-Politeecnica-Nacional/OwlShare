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

CREATE TABLE IF NOT EXISTS materias_catalogo (
    codigo   VARCHAR(20) PRIMARY KEY,
    carrera  VARCHAR(50) NOT NULL,
    nombre   VARCHAR(200) NOT NULL,
    semestre INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS horarios (
    id            BIGSERIAL PRIMARY KEY,
    tutor_id      BIGINT NOT NULL REFERENCES usuarios (id),
    codigo_materia VARCHAR(20) REFERENCES materias_catalogo (codigo),
    fecha         VARCHAR(10) NOT NULL,
    hora_inicio   VARCHAR(5) NOT NULL,
    hora_fin      VARCHAR(5) NOT NULL,
    disponible    BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS disponibilidad_tutor (
    id            BIGSERIAL PRIMARY KEY,
    tutor_id      BIGINT NOT NULL REFERENCES usuarios (id),
    dia_semana    VARCHAR(15) NOT NULL,
    hora_inicio   VARCHAR(5) NOT NULL,
    hora_fin      VARCHAR(5) NOT NULL,
    activo        BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS solicitudes_tutoria (
    id             BIGSERIAL PRIMARY KEY,
    estudiante_id  BIGINT NOT NULL REFERENCES usuarios (id),
    horario_id     BIGINT NOT NULL REFERENCES horarios (id),
    codigo_materia VARCHAR(20) NOT NULL REFERENCES materias_catalogo (codigo),
    estado         VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    comentario     VARCHAR(500)
);
