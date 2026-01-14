-- Script para crear la base de datos (ejecutar antes de las migraciones Flyway)
-- Este script debe ejecutarse manualmente con privilegios de administrador

CREATE DATABASE IF NOT EXISTS bankdb
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Opcional: Crear usuario específico para la aplicación
-- CREATE USER IF NOT EXISTS 'bank_user'@'localhost' IDENTIFIED BY 'bank_password';
-- GRANT ALL PRIVILEGES ON bankdb.* TO 'bank_user'@'localhost';
-- FLUSH PRIVILEGES;

