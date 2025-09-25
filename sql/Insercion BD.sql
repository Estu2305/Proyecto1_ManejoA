INSERT INTO Sucursal (nombre, ubicacion) VALUES
('Sucursal Central', 'Zona 1, Ciudad'),
('Sucursal Norte', 'Zona 5, Ciudad'),
('Sucursal Sur', 'Zona 10, Ciudad'),
('Bodega Central', 'Zona 3, Ciudad');

INSERT INTO Rol (nombre) VALUES
('Administrador'),
('Recepcionista'),
('Entrenador'),
('Inventario');

-- Administrador único (Sucursal Central)
INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
VALUES (1, 1, 'Carlos', 'Gómez', 'admin1', 'admin123');

-- Sucursal Central: 25 entrenadores, 3 recepcionistas, 5 inventario
DO $$
DECLARE
    i INT;
BEGIN
    -- Entrenadores
    FOR i IN 1..25 LOOP
        INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
        VALUES (1, 3, 'EntrenadorC'||i, 'ApellidoC'||i, 'entC'||i, 'pass'||i);
    END LOOP;

    -- Recepcionistas
    FOR i IN 1..3 LOOP
        INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
        VALUES (1, 2, 'RecepcionistaC'||i, 'ApellidoRC'||i, 'recC'||i, 'pass'||i);
    END LOOP;

    -- Inventario
    FOR i IN 1..5 LOOP
        INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
        VALUES (1, 4, 'InvC'||i, 'ApellidoIC'||i, 'invC'||i, 'pass'||i);
    END LOOP;
END$$;

-- Sucursal Norte: 15 entrenadores, 2 recepcionistas, 3 inventario
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 1..15 LOOP
        INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
        VALUES (2, 3, 'EntrenadorN'||i, 'ApellidoN'||i, 'entN'||i, 'pass'||i);
    END LOOP;

    FOR i IN 1..2 LOOP
        INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
        VALUES (2, 2, 'RecepcionistaN'||i, 'ApellidoRN'||i, 'recN'||i, 'pass'||i);
    END LOOP;

    FOR i IN 1..3 LOOP
        INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
        VALUES (2, 4, 'InvN'||i, 'ApellidoIN'||i, 'invN'||i, 'pass'||i);
    END LOOP;
END$$;

-- Sucursal Sur: 10 entrenadores, 1 recepcionista, 2 inventario
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 1..10 LOOP
        INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
        VALUES (3, 3, 'EntrenadorS'||i, 'ApellidoS'||i, 'entS'||i, 'pass'||i);
    END LOOP;

    INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
    VALUES (3, 2, 'RecepcionistaS1', 'ApellidoRS1', 'recS1', 'pass1');

    FOR i IN 1..2 LOOP
        INSERT INTO Empleado (id_sucursal, id_rol, nombre, apellido, usuario, contrasenia)
        VALUES (3, 4, 'InvS'||i, 'ApellidoIS'||i, 'invS'||i, 'pass'||i);
    END LOOP;
END$$;


INSERT INTO Membresia (tipo, descuento, precio, duracion_meses) VALUES
('Básica', 0.00, 300.00, 1),
('Premium', 10.00, 500.00, 6),
('VIP', 20.00, 1000.00, 12);

DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 1..75 LOOP
        INSERT INTO Cliente (nombre, apellido, correo, telefono)
        VALUES ('ClienteB'||i, 'ApellidoB'||i, 'clienteB'||i||'@mail.com', '555-100'||i);
        INSERT INTO Cliente_Membresia (id_cliente, id_membresia, fecha_inicio, fecha_fin, estado)
        VALUES (currval('cliente_id_cliente_seq'), 1, '2025-01-01', '2025-01-31', 'Activa');
    END LOOP;

    FOR i IN 1..50 LOOP
        INSERT INTO Cliente (nombre, apellido, correo, telefono)
        VALUES ('ClienteP'||i, 'ApellidoP'||i, 'clienteP'||i||'@mail.com', '555-200'||i);
        INSERT INTO Cliente_Membresia (id_cliente, id_membresia, fecha_inicio, fecha_fin, estado)
        VALUES (currval('cliente_id_cliente_seq'), 2, '2025-01-01', '2025-06-30', 'Activa');
    END LOOP;

    FOR i IN 1..25 LOOP
        INSERT INTO Cliente (nombre, apellido, correo, telefono)
        VALUES ('ClienteV'||i, 'ApellidoV'||i, 'clienteV'||i||'@mail.com', '555-300'||i);
        INSERT INTO Cliente_Membresia (id_cliente, id_membresia, fecha_inicio, fecha_fin, estado)
        VALUES (currval('cliente_id_cliente_seq'), 3, '2025-01-01', '2025-12-31', 'Activa');
    END LOOP;
END$$;

DO $$
DECLARE
    cliente_id INT;
    entrenador_id INT;
    entrenadores INT[];
    total_entrenadores INT;
BEGIN
    SELECT array_agg(id_empleado) INTO entrenadores
    FROM Empleado
    WHERE id_rol = 3;

    total_entrenadores := array_length(entrenadores,1);

    FOR cliente_id IN 1..150 LOOP
        entrenador_id := entrenadores[(cliente_id - 1) % total_entrenadores + 1];
        INSERT INTO Cliente_Entrenador (id_cliente, id_entrenador, fecha_asignacion, fecha_fin)
        VALUES (cliente_id, entrenador_id, '2025-01-01', '2025-12-31');
    END LOOP;
END$$;

INSERT INTO Equipo (nombre, descripcion) VALUES
('Cinta de correr', 'Máquina para correr en interiores'),
('Bicicleta estática', 'Equipo para ejercicios de cardio'),
('Máquina de pesas', 'Equipo para entrenamiento de fuerza');

INSERT INTO Inventario (id_sucursal, id_equipo, cantidad, estado, requiere_mantenimiento) VALUES
(1, 1, 20, 'Disponible', false),
(1, 2, 15, 'Disponible', false),
(1, 3, 15, 'Disponible', false),
(2, 1, 10, 'Disponible', false),
(2, 2, 10, 'Disponible', false),
(2, 3, 10, 'Disponible', false),
(3, 1, 7, 'Disponible', false),
(3, 2, 6, 'Disponible', false),
(3, 3, 7, 'Disponible', false),
(4, 1, 20, 'Disponible', false),
(4, 2, 15, 'Disponible', false),
(4, 3, 15, 'Disponible', false);

INSERT INTO Servicio (nombre, descripcion, costo) VALUES
('Clase de Yoga', 'Sesión de yoga grupal', 100.00),
('Nutrición', 'Asesoría personalizada de nutrición', 200.00),
('Masaje deportivo', 'Sesión de recuperación muscular', 150.00);

INSERT INTO Cliente_Servicio (id_cliente, id_servicio, fecha) VALUES
(1, 1, '2025-02-01'),
(2, 2, '2025-02-10'),
(3, 3, '2025-02-15');

INSERT INTO TipoPago (nombre) VALUES
('Efectivo'),
('Tarjeta'),
('Transferencia');

INSERT INTO Pago (id_cliente, id_tipo_pago, fecha_inicio, fecha_fin, monto, concepto, estado) VALUES
(1, 1, '2025-01-01', '2025-01-31', 300.00, 'Membresia', 'Pagado'),
(2, 2, '2025-01-01', '2025-06-30', 450.00, 'Membresia', 'Pagado'),
(3, 3, '2025-01-01', '2025-12-31', 800.00, 'Membresia', 'Pendiente');

DO $$
DECLARE
    i INT;
    entrenador_id INT;
    entrenadores INT[];
BEGIN
    SELECT array_agg(id_empleado ORDER BY id_empleado)
    INTO entrenadores
    FROM Empleado
    WHERE id_rol = 3;

    FOR i IN 1..20 LOOP
        entrenador_id := entrenadores[(i - 1) % array_length(entrenadores, 1) + 1];

        INSERT INTO Rutina (id_cliente, id_entrenador, fecha_inicio, descripcion)
        VALUES (
            i,
            entrenador_id,
            '2025-01-01',
            'Rutina personalizada ' || i
        );
    END LOOP;
END$$;

INSERT INTO Ejercicio (nombre) VALUES
('Sentadilla'),
('Press de pecho'),
('Curl de bíceps');

INSERT INTO Rutina_Ejercicio (id_rutina, id_ejercicio, series, repeticiones, duracion) VALUES
(1,1,3,12,'00:20:00'),
(1,2,4,10,'00:15:00'),
(2,3,3,15,'00:10:00');

INSERT INTO Rutina_Ejercicio_Equipo (id_rutina, id_ejercicio, id_equipo) VALUES
(22, 1, 3),
(22, 2, 3),
(23, 2, 3),
(23, 3, 2);

DO $$
DECLARE
    i INT;
    sucursal_id INT;
    fecha_hora TIMESTAMP;
BEGIN
    FOR i IN 1..150 LOOP
        sucursal_id := (i % 4) + 1;
        fecha_hora := TIMESTAMP '2025-01-01 08:00:00' + (random() * 30)::INT * INTERVAL '1 day' 
                      + (random() * 8)::INT * INTERVAL '1 hour';
        INSERT INTO Asistencia (id_cliente, id_sucursal, fecha_hora)
        VALUES (i, sucursal_id, fecha_hora);
    END LOOP;
END$$;

