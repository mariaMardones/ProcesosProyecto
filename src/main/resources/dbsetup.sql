-- Crear usuario y base de datos si no existen
DROP USER IF EXISTS 'spq'@'%';
CREATE USER IF NOT EXISTS 'spq'@'%' IDENTIFIED BY 'spq';

DROP SCHEMA IF EXISTS restapidb;
CREATE SCHEMA restapidb;

GRANT ALL ON restapidb.* TO 'spq'@'%';
FLUSH PRIVILEGES;

-- Usar la base de datos
USE restapidb;

-- Crear tabla usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    fecha_nacimiento VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tlf VARCHAR(20) NOT NULL,
    rol ENUM('ADMIN', 'CLIENTE') NOT NULL
);

-- Crear tabla coches
CREATE TABLE IF NOT EXISTS coches (
    matricula VARCHAR(20) PRIMARY KEY,
    marca VARCHAR(255) NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    anio INT NOT NULL,
    color VARCHAR(50) NOT NULL,
    precio DOUBLE NOT NULL,
    disponible BOOLEAN NOT NULL
);

-- Crear tabla reservas
CREATE TABLE IF NOT EXISTS reservas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    coche_matricula VARCHAR(20) NOT NULL,
    fecha VARCHAR(255) NOT NULL,
    precio_total DOUBLE NOT NULL,
    estado ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA') NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (coche_matricula) REFERENCES coches(matricula) ON DELETE CASCADE
);
