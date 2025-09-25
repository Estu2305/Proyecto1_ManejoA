DROP TABLE IF EXISTS Cliente_Servicio CASCADE;
DROP TABLE IF EXISTS Servicio CASCADE;
DROP TABLE IF EXISTS Inventario CASCADE;
DROP TABLE IF EXISTS Ejercicio_Equipo CASCADE;
DROP TABLE IF EXISTS Rutina_Ejercicio CASCADE;
DROP TABLE IF EXISTS Rutina CASCADE;
DROP TABLE IF EXISTS Ejercicio CASCADE;
DROP TABLE IF EXISTS Equipo CASCADE;
DROP TABLE IF EXISTS Asistencia CASCADE;
DROP TABLE IF EXISTS Pago CASCADE;
DROP TABLE IF EXISTS TipoPago CASCADE;
DROP TABLE IF EXISTS Cliente_Membresia CASCADE;
DROP TABLE IF EXISTS Cliente_Entrenador CASCADE;
DROP TABLE IF EXISTS Cliente CASCADE;
DROP TABLE IF EXISTS Empleado CASCADE;
DROP TABLE IF EXISTS Rol CASCADE;
DROP TABLE IF EXISTS Membresia CASCADE;
DROP TABLE IF EXISTS Sucursal CASCADE;

CREATE TABLE Sucursal (
    id_sucursal SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(150)
);

CREATE TABLE Rol (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE Empleado (
    id_empleado SERIAL PRIMARY KEY,
    id_sucursal INT NOT NULL REFERENCES Sucursal(id_sucursal) ON DELETE CASCADE,
    id_rol INT NOT NULL REFERENCES Rol(id_rol) ON DELETE RESTRICT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasenia VARCHAR(100) NOT NULL
);

CREATE TABLE Membresia (
    id_membresia SERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL UNIQUE,
    descuento NUMERIC(5,2) DEFAULT 0,
    precio NUMERIC(10,2) NOT NULL,
    duracion_meses INT NOT NULL
);

CREATE TABLE Cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(120) UNIQUE,
    telefono VARCHAR(20),
    fecha_registro DATE DEFAULT CURRENT_DATE
);

CREATE TABLE Cliente_Membresia (
    id_cliente INT REFERENCES Cliente(id_cliente) ON DELETE CASCADE,
    id_membresia INT REFERENCES Membresia(id_membresia),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    estado VARCHAR(20) DEFAULT 'Activa'
        CHECK (estado IN ('Activa','Expirada','Suspendida')),
    PRIMARY KEY (id_cliente, id_membresia, fecha_inicio)
);

CREATE TABLE Cliente_Entrenador (
    id_cliente INT REFERENCES Cliente(id_cliente) ON DELETE CASCADE,
    id_entrenador INT NULL REFERENCES Empleado(id_empleado) ON DELETE SET NULL,
    fecha_asignacion DATE DEFAULT CURRENT_DATE,
    fecha_fin DATE,
    PRIMARY KEY (id_cliente, id_entrenador, fecha_asignacion)
);

CREATE TABLE TipoPago (
    id_tipo_pago SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Pago (
    id_pago SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL REFERENCES Cliente(id_cliente) ON DELETE CASCADE,
    id_tipo_pago INT NOT NULL REFERENCES TipoPago(id_tipo_pago),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    monto NUMERIC(10,2) NOT NULL,
    concepto VARCHAR(20) CHECK (concepto IN ('Membresia','Servicio')),
    estado VARCHAR(20) DEFAULT 'Pagado'
        CHECK (estado IN ('Pendiente','Pagado','Vencido','Cancelado')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Asistencia (
    id_asistencia SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL REFERENCES Cliente(id_cliente) ON DELETE CASCADE,
    id_sucursal INT NOT NULL REFERENCES Sucursal(id_sucursal) ON DELETE CASCADE,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Rutina (
    id_rutina SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL REFERENCES Cliente(id_cliente) ON DELETE CASCADE,
    id_entrenador INT REFERENCES Empleado(id_empleado),
    fecha_inicio DATE DEFAULT CURRENT_DATE,
    descripcion TEXT
);

CREATE TABLE Ejercicio (
    id_ejercicio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE Rutina_Ejercicio (
    id_rutina INT REFERENCES Rutina(id_rutina) ON DELETE CASCADE,
    id_ejercicio INT REFERENCES Ejercicio(id_ejercicio) ON DELETE CASCADE,
    series INT CHECK (series >= 0),
    repeticiones INT CHECK (repeticiones >= 0),
    duracion INTERVAL,
    PRIMARY KEY (id_rutina, id_ejercicio)
);

CREATE TABLE Equipo (
    id_equipo SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE Ejercicio_Equipo (
    id_rutina INT REFERENCES Rutina(id_rutina) ON DELETE CASCADE,
    id_ejercicio INT REFERENCES Ejercicio(id_ejercicio) ON DELETE CASCADE,
    id_equipo INT REFERENCES Equipo(id_equipo) ON DELETE CASCADE,
    PRIMARY KEY (id_rutina, id_ejercicio, id_equipo)
);

CREATE TABLE Inventario (
    id_inventario SERIAL PRIMARY KEY,
    id_sucursal INT NOT NULL REFERENCES Sucursal(id_sucursal) ON DELETE CASCADE,
    id_equipo INT NOT NULL REFERENCES Equipo(id_equipo) ON DELETE CASCADE,
    cantidad INT CHECK (cantidad >= 0) DEFAULT 0,
    estado VARCHAR(50) DEFAULT 'Disponible' 
           CHECK (estado IN ('Disponible','En uso','Mantenimiento')),
    requiere_mantenimiento BOOLEAN DEFAULT FALSE
);

CREATE TABLE Servicio (
    id_servicio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    costo NUMERIC(10,2) NOT NULL
);
CREATE TABLE Cliente_Servicio (
    id_cliente INT REFERENCES Cliente(id_cliente) ON DELETE CASCADE,
    id_servicio INT REFERENCES Servicio(id_servicio) ON DELETE CASCADE,
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (id_cliente, id_servicio, fecha)
);