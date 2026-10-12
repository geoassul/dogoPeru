-- ==========================================
-- DoGo Project - Complete Database Setup
-- ==========================================

-- Create database
CREATE DATABASE IF NOT EXISTS tienda_ropa_db;
USE tienda_ropa_db;

-- ==========================================
-- TABLES
-- ==========================================

DROP TABLE IF EXISTS detalle_venta;
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS imagenes_variante;
DROP TABLE IF EXISTS variantes_producto;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS user;

CREATE TABLE proveedores (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(30) NOT NULL,
    telefono VARCHAR(10) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE categorias (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_nombre_categoria(nombre)
) ENGINE=InnoDB;

CREATE TABLE productos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    categoria_id INT UNSIGNED NOT NULL,
    proveedor_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_productos_categorias FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    CONSTRAINT fk_productos_proveedores FOREIGN KEY (proveedor_id) REFERENCES proveedores(id)
) ENGINE=InnoDB;

CREATE TABLE variantes_producto (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    producto_id INT UNSIGNED NOT NULL,
    sku VARCHAR(100) NOT NULL,
    talla VARCHAR(20) NULL,
    tipoManga VARCHAR(20) NULL,
    color VARCHAR(20) NULL,
    costo_adquisicion DECIMAL(6, 2) NOT NULL,
    precio_venta DECIMAL(6, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uq_sku (sku),
    CONSTRAINT fk_variantes_productos FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE imagenes_variante (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    variante_id INT UNSIGNED NOT NULL,
    url_imagen VARCHAR(500) NOT NULL,
    tipo_imagen ENUM('principal', 'galeria', 'detalle') NOT NULL DEFAULT 'principal',
    orden TINYINT UNSIGNED DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_imagenes_variante_variante FOREIGN KEY (variante_id) REFERENCES variantes_producto(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE ventas (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fecha_venta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_venta DECIMAL(6, 2) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
    venta_id INT UNSIGNED NOT NULL,
    variante_id INT UNSIGNED NOT NULL,
    cantidad INT UNSIGNED NOT NULL,
    precio_unitario_venta DECIMAL(6, 2) NOT NULL,
    costo_unitario_adquisicion DECIMAL(6, 2) NOT NULL,
    PRIMARY KEY (venta_id, variante_id),
    CONSTRAINT fk_detalle_ventas FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_variantes FOREIGN KEY (variante_id) REFERENCES variantes_producto(id)
) ENGINE=InnoDB;

-- User table for authentication
CREATE TABLE user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR(25) NOT NULL,
    password VARCHAR(150) NOT NULL,
    role VARCHAR(25) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_username (username)
) ENGINE=InnoDB;

-- ==========================================
-- TEST USERS (password: admin / user)
-- ==========================================
INSERT INTO user (username, password, role) VALUES 
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye72VLIzxKyK7kVKtF5JrJ0B8NW5n9LzK', 'ADMIN'),
('user', '$2a$10$XRb2AxKf0UgLZVLO7C8PSuz8OzOV.cD6K8LBvCz0yzKHjHtbB8t8G', 'USER');

-- ==========================================
-- SAMPLE DATA
-- ==========================================

-- Proveedores
INSERT INTO proveedores (nombre, telefono) VALUES
('Textiles del Sur', '111222333'), ('Moda Global', '444555666'), ('Calzados Fino', '777888999'),
('Confecciones Chic', '123123123'), ('Ropa Deportiva Pro', '456456456');

-- Categorías
INSERT INTO categorias (nombre) VALUES
('Polos Manga Corta'), ('Polos Manga Larga'), ('Camisas'), ('Pantalones Vaqueros'),
('Pantalones de Vestir'), ('Shorts'), ('Faldas'), ('Vestidos'), ('Chaquetas'),
('Ropa Deportiva'), ('Sudaderas'), ('Ropa Interior'), ('Pijamas'), ('Trajes de Baño'),
('Accesorios'), ('Zapatos'), ('Bufandas'), ('Gorros'), ('Guantes');

-- Productos
INSERT INTO productos (nombre, categoria_id, proveedor_id) VALUES
('Polo Básico Manga Corta', 1, 1), ('Jean Clásico Slim Fit', 4, 2), ('Camisa Casual', 3, 4),
('Zapatillas Deportivas Urbanas', 17, 3), ('Chaqueta Deportiva Ligera', 9, 5);

-- Variantes
INSERT INTO variantes_producto (producto_id, sku, talla, tipoManga, color, costo_adquisicion, precio_venta, stock) VALUES
(1, 'POLO-BAS-AZ-S-C', 'S', 'Corta', 'Azul', 15.00, 35.00, 25),
(1, 'POLO-BAS-AZ-M-C', 'M', 'Corta', 'Azul', 15.00, 35.00, 30),
(1, 'POLO-BAS-AZ-L-C', 'L', 'Corta', 'Azul', 15.00, 35.00, 20),
(1, 'POLO-BAS-RO-S-C', 'S', 'Corta', 'Rojo', 15.00, 35.00, 15),
(1, 'POLO-BAS-RO-M-C', 'M', 'Corta', 'Rojo', 15.00, 35.00, 18),
(1, 'POLO-BAS-BL-M-C', 'M', 'Corta', 'Blanco', 16.00, 36.00, 22),
(2, 'JEAN-CLA-AZ-32', '32', NULL, 'Azul', 25.00, 60.00, 12),
(2, 'JEAN-CLA-AZ-34', '34', NULL, 'Azul', 25.00, 60.00, 15),
(2, 'JEAN-CLA-NG-32', '32', NULL, 'Negro', 26.00, 62.00, 10),
(2, 'JEAN-CLA-NG-34', '34', NULL, 'Negro', 26.00, 62.00, 13),
(3, 'CAM-CAS-BL-M-L', 'M', 'Larga', 'Blanco', 20.00, 45.00, 18),
(3, 'CAM-CAS-BL-L-L', 'L', 'Larga', 'Blanco', 20.00, 45.00, 15),
(3, 'CAM-CAS-AZ-M-L', 'M', 'Larga', 'Azul Claro', 21.00, 47.00, 20),
(3, 'CAM-CAS-GR-L-L', 'L', 'Larga', 'Gris', 21.00, 47.00, 10),
(4, 'ZAP-URB-BL-41', '41', NULL, 'Blanco', 30.00, 75.00, 8),
(4, 'ZAP-URB-BL-42', '42', NULL, 'Blanco', 30.00, 75.00, 10),
(4, 'ZAP-URB-NG-41', '41', NULL, 'Negro', 31.00, 77.00, 7),
(4, 'ZAP-URB-NG-42', '42', NULL, 'Negro', 31.00, 77.00, 9),
(5, 'CHAQ-DEP-GR-M', 'M', 'Larga', 'Gris', 35.00, 85.00, 12),
(5, 'CHAQ-DEP-GR-L', 'L', 'Larga', 'Gris', 35.00, 85.00, 10);

-- Imágenes
INSERT INTO imagenes_variante (variante_id, url_imagen, tipo_imagen, orden) VALUES
(2, '/img/polos/polo_basico_azul_m_principal.jpg', 'principal', 0),
(5, '/img/polos/polo_basico_rojo_m_principal.jpg', 'principal', 0),
(7, '/img/jeans/jean_clasico_azul_32_principal.jpg', 'principal', 0),
(11, '/img/camisas/camisa_blanca_m_principal.jpg', 'principal', 0),
(15, '/img/zapatillas/zapatilla_blanca_42_principal.jpg', 'principal', 0),
(19, '/img/chaquetas/chaqueta_gris_l_principal.jpg', 'principal', 0);

-- Ventas de prueba
INSERT INTO ventas (total_venta) VALUES (105.00), (201.00), (72.00), (135.00), (261.00);

INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(1, 2, 2, 35.00, 15.00),
(1, 7, 1, 35.00, 25.00),
(2, 9, 1, 62.00, 26.00),
(2, 15, 1, 77.00, 30.00),
(2, 14, 1, 47.00, 21.00),
(3, 5, 2, 36.00, 15.00),
(4, 8, 1, 60.00, 26.00),
(4, 11, 1, 45.00, 20.00),
(4, 1, 1, 35.00, 15.00),
(5, 19, 1, 85.00, 35.00),
(5, 15, 2, 77.00, 30.00);

-- ==========================================
-- STORED PROCEDURES
-- ==========================================
DELIMITER $$

DROP PROCEDURE IF EXISTS sp_variantes_actualizar_stock$$
CREATE PROCEDURE sp_variantes_actualizar_stock(IN p_variante_id INT, IN p_cantidad_cambio INT)
BEGIN
    UPDATE variantes_producto
    SET stock = stock + p_cantidad_cambio
    WHERE id = p_variante_id AND stock + p_cantidad_cambio >= 0;
END$$

DROP PROCEDURE IF EXISTS sp_categorias_listar$$
CREATE PROCEDURE sp_categorias_listar()
BEGIN
    SELECT id, nombre FROM categorias ORDER BY nombre;
END$$

DROP PROCEDURE IF EXISTS sp_proveedores_listar$$
CREATE PROCEDURE sp_proveedores_listar()
BEGIN
    SELECT id, nombre, telefono FROM proveedores ORDER BY nombre;
END$$

DROP PROCEDURE IF EXISTS sp_productos_listar$$
CREATE PROCEDURE sp_productos_listar()
BEGIN
    SELECT p.id, p.nombre, c.nombre AS categoria, pr.nombre AS proveedor
    FROM productos p
    JOIN categorias c ON p.categoria_id = c.id
    JOIN proveedores pr ON p.proveedor_id = pr.id
    ORDER BY p.nombre;
END$$

DROP PROCEDURE IF EXISTS sp_variantes_listar$$
CREATE PROCEDURE sp_variantes_listar(IN p_producto_id INT)
BEGIN
    SELECT v.id, v.producto_id, v.sku, v.talla, v.tipoManga, v.color,
           v.costo_adquisicion, v.precio_venta, v.stock
    FROM variantes_producto v
    WHERE v.producto_id = p_producto_id
    ORDER BY v.sku;
END$$

DROP PROCEDURE IF EXISTS sp_ventas_listar_ultimas$$
CREATE PROCEDURE sp_ventas_listar_ultimas(IN p_limite INT)
BEGIN
    SELECT v.id, v.fecha_venta, v.total_venta
    FROM ventas v
    ORDER BY v.fecha_venta DESC
    LIMIT p_limite;
END$$

DELIMITER ;

-- ==========================================
-- VERIFICATION
-- ==========================================
SELECT 'Database setup complete!' AS Status;
SELECT * FROM user;
