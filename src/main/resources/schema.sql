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

CREATE TABLE IF NOT EXISTS material (
    id                   BIGSERIAL PRIMARY KEY,
    titulo               VARCHAR(200) NOT NULL,
    codigo_materia       VARCHAR(20) NOT NULL,
    id_tutor             BIGINT NOT NULL REFERENCES usuarios (id),
    nombre_archivo       VARCHAR(260) NOT NULL,
    ruta_almacenamiento  VARCHAR(512) NOT NULL,
    descripcion          VARCHAR(500),
    costo                DECIMAL(10, 2) NOT NULL,
    categoria_academica  VARCHAR(100),
    estado               VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE',
    comentario_admin     VARCHAR(1000),
    id_admin_revisor     BIGINT,
    fecha_revision       TIMESTAMP,
    fecha_registro       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS material_adquisicion (
    id                 BIGSERIAL PRIMARY KEY,
    id_material        BIGINT NOT NULL REFERENCES material (id),
    id_estudiante      BIGINT NOT NULL REFERENCES usuarios (id),
    fecha_adquisicion  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id_material, id_estudiante)
);

-- Usuario administrador (crear manualmente en BD con rol ADMIN).
-- La contraseña debe almacenarse con el hash generado por PasswordUtil.hash().
