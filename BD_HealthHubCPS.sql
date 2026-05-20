
-- TIPOS ENUM

CREATE TYPE rol_usuario AS ENUM ('PACIENTE', 'MEDICO', 'ADMIN');
CREATE TYPE complejidad_estudio AS ENUM ('CONSULTA', 'DIAGNOSTICO', 'ALTA_COMPLEJIDAD');
CREATE TYPE estado_turno AS ENUM ('AGENDADO', 'CANCELADO', 'COMPLETADO');
CREATE TYPE tipo_archivo AS ENUM ('RECETA', 'ESTUDIO', 'RADIOGRAFIA', 'OTRO');
CREATE TYPE formato_archivo AS ENUM ('PDF', 'JPG', 'PNG');


-- TABLAS


CREATE TABLE obra_social (
    id_obra_social SERIAL PRIMARY KEY,
    nombre VARCHAR(80) UNIQUE NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE especialidad (
    id_especialidad SERIAL PRIMARY KEY,
    nombre VARCHAR(80) UNIQUE NOT NULL
);

CREATE TABLE tipo_estudio (
    id_tipo_estudio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    complejidad complejidad_estudio NOT NULL,
    tiempo_minutos INT NOT NULL,
    tarifa_base DECIMAL(10,2) NOT NULL
);

CREATE TABLE consultorio (
    id_consultorio SERIAL PRIMARY KEY,
    numero VARCHAR(10) UNIQUE NOT NULL,
    ubicacion VARCHAR(100) NOT NULL
);

CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    dni VARCHAR(15) UNIQUE NOT NULL,
    contrasenia VARCHAR(100) NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    apellido VARCHAR(60) NOT NULL,
    email VARCHAR(120) NOT NULL,
    telefono VARCHAR(30),
    rol rol_usuario NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_alta DATE NOT NULL
);

CREATE TABLE paciente (
    id_usuario INT PRIMARY KEY,
    fecha_nacimiento DATE NOT NULL,
    domicilio VARCHAR(150) NOT NULL,
    id_obra_social INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_obra_social) REFERENCES obra_social(id_obra_social)
);

CREATE TABLE medico (
    id_usuario INT PRIMARY KEY,
    matricula VARCHAR(30) UNIQUE NOT NULL,
    id_especialidad INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_especialidad) REFERENCES especialidad(id_especialidad)
);

CREATE TABLE cobertura (
    id_obra_social INT NOT NULL,
    id_tipo_estudio INT NOT NULL,
    porcentaje_cobertura DECIMAL(4,2) NOT NULL CHECK (porcentaje_cobertura BETWEEN 0 AND 100),
    vigente BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_obra_social, id_tipo_estudio),
    FOREIGN KEY (id_obra_social) REFERENCES obra_social(id_obra_social) ON DELETE CASCADE,
    FOREIGN KEY (id_tipo_estudio) REFERENCES tipo_estudio(id_tipo_estudio) ON DELETE CASCADE
);

CREATE TABLE historia_clinica (
    id_historia SERIAL PRIMARY KEY,
    id_paciente INT UNIQUE NOT NULL,
    fecha_creacion DATE NOT NULL,
    observaciones TEXT,
    FOREIGN KEY (id_paciente) REFERENCES paciente(id_usuario) ON DELETE CASCADE
);

CREATE TABLE turno (
    id_turno SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado estado_turno NOT NULL DEFAULT 'AGENDADO',
    monto_final DECIMAL(10,2) NOT NULL,
    id_paciente INT NOT NULL,
    id_medico INT NOT NULL,
    id_consultorio INT NOT NULL,
    id_tipo_estudio INT NOT NULL,
    motivo_cancel VARCHAR(200),
    UNIQUE (id_medico, fecha, hora),
    UNIQUE (id_consultorio, fecha, hora),
    FOREIGN KEY (id_paciente) REFERENCES paciente(id_usuario),
    FOREIGN KEY (id_medico) REFERENCES medico(id_usuario),
    FOREIGN KEY (id_consultorio) REFERENCES consultorio(id_consultorio),
    FOREIGN KEY (id_tipo_estudio) REFERENCES tipo_estudio(id_tipo_estudio)
);

CREATE TABLE archivo_adjunto (
    id_archivo SERIAL PRIMARY KEY,
    id_historia INT NOT NULL,
    id_medico_carga INT NOT NULL,
    tipo tipo_archivo NOT NULL,
    formato formato_archivo NOT NULL,
    url VARCHAR(255) NOT NULL,
    fecha_carga DATE NOT NULL,
    FOREIGN KEY (id_historia) REFERENCES historia_clinica(id_historia) ON DELETE CASCADE,
    FOREIGN KEY (id_medico_carga) REFERENCES medico(id_usuario)
);

CREATE TABLE resultado (
    id_resultado SERIAL PRIMARY KEY,
    id_turno INT UNIQUE NOT NULL,
    descripcion TEXT NOT NULL,
    autorizado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_autorizacion DATE,
    id_medico_autoriza INT,
    CHECK (
        (autorizado = TRUE  AND id_medico_autoriza IS NOT NULL AND fecha_autorizacion IS NOT NULL) OR
        (autorizado = FALSE AND id_medico_autoriza IS NULL     AND fecha_autorizacion IS NULL)
    ),
    FOREIGN KEY (id_turno) REFERENCES turno(id_turno) ON DELETE CASCADE,
    FOREIGN KEY (id_medico_autoriza) REFERENCES medico(id_usuario)
);



-- DATOS INICIALES


-- Obras sociales
INSERT INTO obra_social (nombre) VALUES ('OSDE');
INSERT INTO obra_social (nombre) VALUES ('Swiss Medical');
INSERT INTO obra_social (nombre) VALUES ('Galeno');
INSERT INTO obra_social (nombre) VALUES ('Sin Obra Social');

-- Especialidades médicas que ofrece la clínica
INSERT INTO especialidad (nombre) VALUES ('Clinica Medica');
INSERT INTO especialidad (nombre) VALUES ('Cardiologia');
INSERT INTO especialidad (nombre) VALUES ('Traumatologia');
INSERT INTO especialidad (nombre) VALUES ('Pediatria');
INSERT INTO especialidad (nombre) VALUES ('Dermatologia');

-- Tipos de estudio disponibles con su complejidad, duración y tarifa base
INSERT INTO tipo_estudio (nombre, complejidad, tiempo_minutos, tarifa_base)
VALUES ('Consulta general', 'CONSULTA', 30, 30000.00);

INSERT INTO tipo_estudio (nombre, complejidad, tiempo_minutos, tarifa_base)
VALUES ('Electrocardiograma', 'DIAGNOSTICO', 45, 80000.00);

INSERT INTO tipo_estudio (nombre, complejidad, tiempo_minutos, tarifa_base)
VALUES ('Radiografia simple', 'DIAGNOSTICO', 30, 60000.00);

INSERT INTO tipo_estudio (nombre, complejidad, tiempo_minutos, tarifa_base)
VALUES ('Resonancia magnetica', 'ALTA_COMPLEJIDAD', 90, 450000.00);

INSERT INTO tipo_estudio (nombre, complejidad, tiempo_minutos, tarifa_base)
VALUES ('Tomografia computada', 'ALTA_COMPLEJIDAD', 60, 380000.00);

-- Consultorios físicos del edificio
INSERT INTO consultorio (numero, ubicacion) VALUES ('101', 'Planta baja - Ala norte');
INSERT INTO consultorio (numero, ubicacion) VALUES ('102', 'Planta baja - Ala norte');
INSERT INTO consultorio (numero, ubicacion) VALUES ('201', 'Primer piso - Cardiologia');
INSERT INTO consultorio (numero, ubicacion) VALUES ('301', 'Tercer piso - Imagenes');

-- Matriz de coberturas: qué % cubre cada obra social por tipo de estudio
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (1, 1, 99.99);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (1, 2, 80.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (1, 3, 80.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (1, 4, 60.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (1, 5, 60.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (2, 1, 99.99);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (2, 2, 99.99);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (2, 3, 90.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (2, 4, 70.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (2, 5, 70.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (3, 1, 90.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (3, 2, 70.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (3, 3, 70.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (3, 4, 50.00);
INSERT INTO cobertura (id_obra_social, id_tipo_estudio, porcentaje_cobertura) VALUES (3, 5, 50.00);

-- Admin inicial 
INSERT INTO usuario (dni, contrasenia, nombre, apellido, email, telefono, rol, fecha_alta)
VALUES ('123', 'admin', 'Admin', 'Inicial', 'admin@healthhub.com', '0000-000000', 'ADMIN', CURRENT_DATE);