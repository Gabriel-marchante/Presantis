-- V1.0.0__Initial_Schema.sql
-- Creación de tablas iniciales con UUIDs para PostgreSQL

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE empresas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    cif VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    empresa_id UUID NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuarios_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE jornadas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP,
    ubicacion_inicio VARCHAR(30) NOT NULL,
    ubicacion_fin VARCHAR(30),
    estado VARCHAR(30) NOT NULL,
    CONSTRAINT fk_jornadas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE pausas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    jornada_id UUID NOT NULL,
    inicio TIMESTAMP NOT NULL,
    fin TIMESTAMP,
    motivo VARCHAR(50) NOT NULL,
    CONSTRAINT fk_pausas_jornada FOREIGN KEY (jornada_id) REFERENCES jornadas(id) ON DELETE CASCADE
);

CREATE TABLE solicitudes_cambio (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    jornada_id UUID NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_solicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta TIMESTAMP,
    admin_id UUID,
    CONSTRAINT fk_solicitudes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_solicitudes_jornada FOREIGN KEY (jornada_id) REFERENCES jornadas(id),
    CONSTRAINT fk_solicitudes_admin FOREIGN KEY (admin_id) REFERENCES usuarios(id)
);

CREATE TABLE logs_cambios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    jornada_id UUID NOT NULL,
    campo_modificado VARCHAR(50) NOT NULL,
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255),
    modificado_por_id UUID NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(255),
    CONSTRAINT fk_logs_jornada FOREIGN KEY (jornada_id) REFERENCES jornadas(id),
    CONSTRAINT fk_logs_modificador FOREIGN KEY (modificado_por_id) REFERENCES usuarios(id)
);

CREATE TABLE vacaciones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    motivo VARCHAR(500),
    fecha_solicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vacaciones_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE festivos_dias (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    fecha DATE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    empresa_id UUID,
    CONSTRAINT fk_festivos_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE notificaciones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    mensaje VARCHAR(500) NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificaciones_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- INSERCIÓN DE DATOS INICIALES (SEED)
-- Password para los usuarios: "admin123" -> Hash bcrypt: $2a$10$NP.jEAvx5lzWbr6Uzc3TQeUhC04L7w3BSv7tXA2FAZ29UoWg1.fzm

INSERT INTO empresas (id, nombre, cif) 
VALUES ('11111111-1111-1111-1111-111111111111', 'Empresa Demo S.A.', 'A12345678');

INSERT INTO usuarios (id, nombre, apellidos, email, password_hash, rol, empresa_id, activo)
VALUES 
('22222222-2222-2222-2222-222222222222', 'Admin', 'Principal', 'admin@presantis.com', '$2a$10$NP.jEAvx5lzWbr6Uzc3TQeUhC04L7w3BSv7tXA2FAZ29UoWg1.fzm', 'ADMIN', '11111111-1111-1111-1111-111111111111', TRUE),
('33333333-3333-3333-3333-333333333333', 'Empleado', 'Uno', 'empleado1@presantis.com', '$2a$10$NP.jEAvx5lzWbr6Uzc3TQeUhC04L7w3BSv7tXA2FAZ29UoWg1.fzm', 'EMPLOYEE', '11111111-1111-1111-1111-111111111111', TRUE);
