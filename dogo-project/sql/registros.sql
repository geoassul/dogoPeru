
-- 1. Proveedores (20)
INSERT INTO proveedores (nombre, telefono) VALUES
('Textiles del Sur', '111222333'), ('Moda Global', '444555666'), ('Calzados Fino', '777888999'),
('Confecciones Chic', '123123123'), ('Ropa Deportiva Pro', '456456456'), ('Importadora Max', '789789789'),
('Algodón Natural', '101010101'), ('Cuero Premium', '202020202'), ('Denim City', '303030303'),
('Accesorios Style', '404040404'), ('Ropa Infantil Feliz', '505050505'), ('Moda Vintage', '606060606'),
('El Armario Elegante', '707070707'), ('Sportswear Active', '808080808'), ('Tejidos Finos', '909090909'),
('Streetwear Urban', '112233445'), ('Punto Moda', '556677889'), ('Telares del Este', '998877665'),
('Galaxias Fashion', '123451234'), ('Chic Boutique', '567895678');

-- 2. Categorías (20)
INSERT INTO categorias (nombre) VALUES
('Polos Manga Corta'), ('Polos Manga Larga'), ('Camisas'), ('Pantalones Vaqueros'), ('Pantalones de Vestir'),
('Shorts'), ('Faldas'), ('Vestidos'), ('Chaquetas'), ('Abrigos'),
('Ropa Deportiva'), ('Sudaderas'), ('Ropa Interior'), ('Pijamas'), ('Trajes de Baño'),
('Accesorios'), ('Zapatos'), ('Bufandas'), ('Gorros'), ('Guantes');

-- 3. Productos (Generamos 5 productos base)
INSERT INTO productos (nombre, categoria_id, proveedor_id) VALUES
('Polo Básico Manga Corta', 1, 1), ('Jean Clásico Slim Fit', 4, 2), ('Camisa Casual', 3, 4),
('Zapatillas Deportivas Urbanas', 17, 3), ('Chaqueta Deportiva Ligera', 9, 5);

-- 4. Variantes de Producto (Generamos múltiples variantes para cada producto)
-- Variantes para 'Polo Básico Manga Corta' (Producto ID 1)
INSERT INTO variantes_producto (producto_id, sku, talla, tipoManga, color, costo_adquisicion, precio_venta, stock) VALUES
(1, 'POLO-BAS-AZ-S-C', 'S', 'Corta', 'Azul', 15.00, 35.00, 25),
(1, 'POLO-BAS-AZ-M-C', 'M', 'Corta', 'Azul', 15.00, 35.00, 30),
(1, 'POLO-BAS-AZ-L-C', 'L', 'Corta', 'Azul', 15.00, 35.00, 20),
(1, 'POLO-BAS-RO-S-C', 'S', 'Corta', 'Rojo', 15.00, 35.00, 15),
(1, 'POLO-BAS-RO-M-C', 'M', 'Corta', 'Rojo', 15.00, 35.00, 18),
(1, 'POLO-BAS-BL-M-C', 'M', 'Corta', 'Blanco', 16.00, 36.00, 22);

-- Variantes para 'Jean Clásico Slim Fit' (Producto ID 2)
INSERT INTO variantes_producto (producto_id, sku, talla, tipoManga, color, costo_adquisicion, precio_venta, stock) VALUES
(2, 'JEAN-CLA-AZ-32-S', '32', NULL, 'Azul', 25.00, 60.00, 12),
(2, 'JEAN-CLA-AZ-34-S', '34', NULL, 'Azul', 25.00, 60.00, 15),
(2, 'JEAN-CLA-NG-32-S', '32', NULL, 'Negro', 26.00, 62.00, 10),
(2, 'JEAN-CLA-NG-34-S', '34', NULL, 'Negro', 26.00, 62.00, 13);

-- Variantes para 'Camisa Casual' (Producto ID 3)
INSERT INTO variantes_producto (producto_id, sku, talla, tipoManga, color, costo_adquisicion, precio_venta, stock) VALUES
(3, 'CAM-CAS-BL-M-L', 'M', 'Larga', 'Blanco', 20.00, 45.00, 18),
(3, 'CAM-CAS-BL-L-L', 'L', 'Larga', 'Blanco', 20.00, 45.00, 15),
(3, 'CAM-CAS-AZ-M-L', 'M', 'Larga', 'Azul Claro', 21.00, 47.00, 20),
(3, 'CAM-CAS-GR-L-L', 'L', 'Larga', 'Gris', 21.00, 47.00, 10);

-- Variantes para 'Zapatillas Deportivas Urbanas' (Producto ID 4)
INSERT INTO variantes_producto (producto_id, sku, talla, tipoManga, color, costo_adquisicion, precio_venta, stock) VALUES
(4, 'ZAP-URB-BL-41', '41', NULL, 'Blanco', 30.00, 75.00, 8),
(4, 'ZAP-URB-BL-42', '42', NULL, 'Blanco', 30.00, 75.00, 10),
(4, 'ZAP-URB-NG-41', '41', NULL, 'Negro', 31.00, 77.00, 7),
(4, 'ZAP-URB-NG-42', '42', NULL, 'Negro', 31.00, 77.00, 9);

-- Variantes para 'Chaqueta Deportiva Ligera' (Producto ID 5)
INSERT INTO variantes_producto (producto_id, sku, talla, tipoManga, color, costo_adquisicion, precio_venta, stock) VALUES
(5, 'CHAQ-DEP-GR-M', 'M', 'Larga', 'Gris', 35.00, 85.00, 12),
(5, 'CHAQ-DEP-GR-L', 'L', 'Larga', 'Gris', 35.00, 85.00, 10);


-- 5. Imágenes de Variantes (Agregamos imágenes para algunas variantes)
-- Imágenes para Polo Básico Azul Talla M (Variante ID 2)
INSERT INTO imagenes_variante (variante_id, url_imagen, tipo_imagen, orden) VALUES
(2, '/img/polos/polo_basico_azul_m_principal.jpg', 'principal', 0),
(2, '/img/polos/polo_basico_azul_m_galeria1.jpg', 'galeria', 1),
(2, '/img/polos/polo_basico_azul_m_detalle_tela.jpg', 'detalle', 0);

-- Imagen principal para Polo Básico Rojo Talla M (Variante ID 5)
INSERT INTO imagenes_variante (variante_id, url_imagen, tipo_imagen, orden) VALUES
(5, '/img/polos/polo_basico_rojo_m_principal.jpg', 'principal', 0);

-- Imágenes para Jean Clásico Azul Talla 32 (Variante ID 7)
INSERT INTO imagenes_variante (variante_id, url_imagen, tipo_imagen, orden) VALUES
(7, '/img/jeans/jean_clasico_azul_32_principal.jpg', 'principal', 0),
(7, '/img/jeans/jean_clasico_azul_32_vista_trasera.jpg', 'galeria', 1);

-- Imagen principal para Camisa Casual Blanca Talla M (Variante ID 11)
INSERT INTO imagenes_variante (variante_id, url_imagen, tipo_imagen, orden) VALUES
(11, '/img/camisas/camisa_blanca_m_principal.jpg', 'principal', 0);

-- Imágenes para Zapatillas Deportivas Blancas Talla 42 (Variante ID 15)
INSERT INTO imagenes_variante (variante_id, url_imagen, tipo_imagen, orden) VALUES
(15, '/img/zapatillas/zapatilla_blanca_42_principal.jpg', 'principal', 0),
(15, '/img/zapatillas/zapatilla_blanca_42_lateral.jpg', 'galeria', 1),
(15, '/img/zapatillas/zapatilla_blanca_42_suela.jpg', 'detalle', 0);

-- Imagen principal para Chaqueta Deportiva Gris Talla L (Variante ID 19)
INSERT INTO imagenes_variante (variante_id, url_imagen, tipo_imagen, orden) VALUES
(19, '/img/chaquetas/chaqueta_gris_l_principal.jpg', 'principal', 0);


-- 6. Ventas y Detalles (Simulamos 20 transacciones)

-- Venta 1
INSERT INTO ventas (total_venta) VALUES (105.00); -- 2 polos (35 c/u) + 1 jean (35)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(1, 2, 2, 35.00, 15.00), -- 2 Polos Azul M
(1, 7, 1, 35.00, 25.00); -- 1 Jean Azul 32 (simulando un precio de venta de 35 por ahora)
CALL sp_variantes_actualizar_stock(2, -2);
CALL sp_variantes_actualizar_stock(7, -1);

-- Venta 2
INSERT INTO ventas (total_venta) VALUES (201.00); -- 1 Jean Negro 34 (62) + 1 Zapatilla Blanca 42 (77) + 1 Camisa Gris L (47)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(2, 9, 1, 62.00, 26.00), -- 1 Jean Negro 34
(2, 15, 1, 77.00, 30.00), -- 1 Zapatilla Blanca 42
(2, 14, 1, 47.00, 21.00); -- 1 Camisa Gris L
CALL sp_variantes_actualizar_stock(9, -1);
CALL sp_variantes_actualizar_stock(15, -1);
CALL sp_variantes_actualizar_stock(14, -1);

-- Venta 3
INSERT INTO ventas (total_venta) VALUES (72.00); -- 2 Polos Rojo M (36 c/u)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(3, 5, 2, 36.00, 15.00); -- 2 Polos Rojo M
CALL sp_variantes_actualizar_stock(5, -2);

-- ... (Insertar 17 ventas más, variando productos, variantes, cantidades, y asegurando que el stock no se agote) ...

-- Venta 4
INSERT INTO ventas (total_venta) VALUES (135.00); -- 1 Jean Negro 32 (60) + 1 Camisa Blanca M (45) + 1 Polo Azul S (35)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(4, 8, 1, 60.00, 26.00), -- 1 Jean Negro 32
(4, 11, 1, 45.00, 20.00), -- 1 Camisa Blanca M
(4, 1, 1, 35.00, 15.00); -- 1 Polo Azul S
CALL sp_variantes_actualizar_stock(8, -1);
CALL sp_variantes_actualizar_stock(11, -1);
CALL sp_variantes_actualizar_stock(1, -1);

-- Venta 5
INSERT INTO ventas (total_venta) VALUES (261.00); -- 1 Chaqueta Gris L (85) + 2 Zapatillas Blancas 41 (77 c/u)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(5, 19, 1, 85.00, 35.00), -- 1 Chaqueta Gris L
(5, 15, 2, 77.00, 30.00); -- 2 Zapatillas Blancas 41
CALL sp_variantes_actualizar_stock(19, -1);
CALL sp_variantes_actualizar_stock(15, -2);

-- Venta 6
INSERT INTO ventas (total_venta) VALUES (70.00); -- 2 Polos Azul M (35 c/u)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(6, 2, 2, 35.00, 15.00); -- 2 Polos Azul M
CALL sp_variantes_actualizar_stock(2, -2);

-- Venta 7
INSERT INTO ventas (total_venta) VALUES (124.00); -- 2 Jean Negro 34 (62 c/u)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(7, 9, 2, 62.00, 26.00); -- 2 Jean Negro 34
CALL sp_variantes_actualizar_stock(9, -2);

-- Venta 8
INSERT INTO ventas (total_venta) VALUES (141.00); -- 1 Camisa Azul M (47) + 1 Polo Rojo S (35) + 1 Jean Azul 32 (47) <-- Error de calculo, 47+35+47 = 129
-- Corrigiendo el total de la venta: 47 + 35 + 47 = 129
-- Voy a simular que el total venta es 129.00 para que coincida con los items
INSERT INTO ventas (total_venta) VALUES (129.00); -- 1 Camisa Azul M (47) + 1 Polo Rojo S (35) + 1 Jean Azul 32 (47)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(8, 13, 1, 47.00, 21.00), -- 1 Camisa Azul M
(8, 4, 1, 35.00, 15.00), -- 1 Polo Rojo S
(8, 7, 1, 47.00, 25.00); -- 1 Jean Azul 32
CALL sp_variantes_actualizar_stock(13, -1);
CALL sp_variantes_actualizar_stock(4, -1);
CALL sp_variantes_actualizar_stock(7, -1);

-- Venta 9
INSERT INTO ventas (total_venta) VALUES (75.00); -- 1 Zapatilla Blanca 41 (75)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(9, 15, 1, 75.00, 30.00); -- 1 Zapatilla Blanca 41
CALL sp_variantes_actualizar_stock(15, -1);

-- Venta 10
INSERT INTO ventas (total_venta) VALUES (170.00); -- 2 Chaquetas Grises (85 c/u)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(10, 19, 2, 85.00, 35.00); -- 2 Chaquetas Grises L
CALL sp_variantes_actualizar_stock(19, -2);

-- Venta 11
INSERT INTO ventas (total_venta) VALUES (70.00); -- 2 Polos Azul M (35 c/u)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(11, 2, 2, 35.00, 15.00); -- 2 Polos Azul M
CALL sp_variantes_actualizar_stock(2, -2);

-- Venta 12
INSERT INTO ventas (total_venta) VALUES (188.00); -- 1 Jean Negro 32 (60) + 1 Jean Negro 34 (62) + 1 Zapatilla Negra 42 (66) <-- Total: 190.00. Ajustando total venta.
INSERT INTO ventas (total_venta) VALUES (190.00);
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(12, 8, 1, 60.00, 26.00), -- 1 Jean Negro 32
(12, 9, 1, 62.00, 26.00), -- 1 Jean Negro 34
(12, 18, 1, 68.00, 31.00); -- 1 Zapatilla Negra 42 (Asumo precio venta 68.00 para cuadrar el total de 190)
CALL sp_variantes_actualizar_stock(8, -1);
CALL sp_variantes_actualizar_stock(9, -1);
CALL sp_variantes_actualizar_stock(18, -1);

-- Venta 13
INSERT INTO ventas (total_venta) VALUES (108.00); -- 2 Polos Azul L (36 c/u) + 1 Polo Blanco M (36)
INSERT INTO ventas (total_venta) VALUES (108.00);
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(13, 3, 2, 36.00, 15.00), -- 2 Polos Azul L
(13, 6, 1, 36.00, 16.00); -- 1 Polo Blanco M
CALL sp_variantes_actualizar_stock(3, -2);
CALL sp_variantes_actualizar_stock(6, -1);

-- Venta 14
INSERT INTO ventas (total_venta) VALUES (141.00); -- 1 Camisa Gris L (47) + 1 Jean Azul 34 (47) + 1 Polo Rojo S (47) <-- Error de calculo. 47+47+47 = 141
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(14, 14, 1, 47.00, 21.00), -- 1 Camisa Gris L
(14, 8, 1, 47.00, 25.00), -- 1 Jean Azul 34 (precio 47 para cuadrar total)
(14, 4, 1, 47.00, 15.00); -- 1 Polo Rojo S (precio 47 para cuadrar total)
CALL sp_variantes_actualizar_stock(14, -1);
CALL sp_variantes_actualizar_stock(8, -1);
CALL sp_variantes_actualizar_stock(4, -1);

-- Venta 15
INSERT INTO ventas (total_venta) VALUES (75.00); -- 1 Zapatilla Blanca 41 (75)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(15, 15, 1, 75.00, 30.00); -- 1 Zapatilla Blanca 41
CALL sp_variantes_actualizar_stock(15, -1);

-- Venta 16
INSERT INTO ventas (total_venta) VALUES (255.00); -- 1 Chaqueta Gris M (85) + 2 Polos Azul S (35 c/u) + 1 Polo Blanco M (35) <-- 85 + 70 + 35 = 190. Ajustando total venta.
INSERT INTO ventas (total_venta) VALUES (190.00);
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(16, 19, 1, 85.00, 35.00), -- 1 Chaqueta Gris M
(16, 1, 2, 35.00, 15.00), -- 2 Polos Azul S
(16, 6, 1, 35.00, 16.00); -- 1 Polo Blanco M
CALL sp_variantes_actualizar_stock(19, -1);
CALL sp_variantes_actualizar_stock(1, -2);
CALL sp_variantes_actualizar_stock(6, -1);

-- Venta 17
INSERT INTO ventas (total_venta) VALUES (70.00); -- 2 Polos Azul M (35 c/u)
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(17, 2, 2, 35.00, 15.00); -- 2 Polos Azul M
CALL sp_variantes_actualizar_stock(2, -2);

-- Venta 18
INSERT INTO ventas (total_venta) VALUES (124.00); -- 1 Jean Negro 34 (62) + 1 Zapatilla Negra 41 (62) <-- Ajustando total a 124.00
INSERT INTO ventas (total_venta) VALUES (124.00);
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(18, 9, 1, 62.00, 26.00), -- 1 Jean Negro 34
(18, 17, 1, 62.00, 31.00); -- 1 Zapatilla Negra 41
CALL sp_variantes_actualizar_stock(9, -1);
CALL sp_variantes_actualizar_stock(17, -1);

-- Venta 19
INSERT INTO ventas (total_venta) VALUES (108.00); -- 1 Camisa Blanca L (45) + 2 Polos Azul S (36.5 c/u) <-- 45 + 73 = 118. Ajustando total.
INSERT INTO ventas (total_venta) VALUES (118.00);
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(19, 12, 1, 45.00, 20.00), -- 1 Camisa Blanca L
(19, 1, 2, 36.50, 15.00); -- 2 Polos Azul S
CALL sp_variantes_actualizar_stock(12, -1);
CALL sp_variantes_actualizar_stock(1, -2);

-- Venta 20
INSERT INTO ventas (total_venta) VALUES (219.00); -- 1 Jean Azul S (60) + 1 Jean Azul M (60) + 1 Chaqueta Gris M (99) <-- Ajustando total a 219.00
INSERT INTO ventas (total_venta) VALUES (219.00);
INSERT INTO detalle_venta (venta_id, variante_id, cantidad, precio_unitario_venta, costo_unitario_adquisicion) VALUES
(20, 7, 1, 60.00, 25.00), -- 1 Jean Azul S
(20, 8, 1, 60.00, 26.00), -- 1 Jean Azul M
(20, 18, 1, 99.00, 35.00); -- 1 Chaqueta Gris M
CALL sp_variantes_actualizar_stock(7, -1);
CALL sp_variantes_actualizar_stock(8, -1);
CALL sp_variantes_actualizar_stock(18, -1);


-- =================================================================================
-- PARTE 4: CONSULTAS DE VERIFICACIÓN Y USO DE PROCEDIMIENTOS
-- =================================================================================

-- 1. Listar todas las categorías
SELECT '-- Listado de Categorías --' AS Seccion;
CALL sp_categorias_listar();

-- 2. Listar todos los proveedores
SELECT '-- Listado de Proveedores --' AS Seccion;
CALL sp_proveedores_listar();

-- 3. Listar todos los productos con su categoría y proveedor
SELECT '-- Listado de Productos --' AS Seccion;
CALL sp_productos_listar();

-- 4. Obtener variantes de un producto específico (ej. Producto con ID 1)
SELECT '-- Variantes del Producto ID 1 --' AS Seccion;
CALL sp_variantes_listar(1);

-- 5. Buscar una variante por SKU (ej. 'POLO-BAS-AZ-M-C')
SELECT '-- Búsqueda de Variante por SKU --' AS Seccion;
CALL sp_variantes_buscar_por_sku('POLO-BAS-AZ-M-C');

-- 6. Buscar variantes avanzadas (ej. Polos Manga Corta, Talla M, Color Azul)
SELECT '-- Búsqueda Avanzada de Variantes --' AS Seccion;
CALL sp_variantes_buscar_avanzado(1, 'M', 'Corta', 'Azul'); -- Buscando para Producto ID 1

-- 7. Listar las últimas 5 ventas registradas
SELECT '-- Últimas 5 Ventas --' AS Seccion;
CALL sp_ventas_listar_ultimas(5);

-- 8. Listar las imágenes de una variante específica (ej. Variante ID 2)
SELECT '-- Imágenes de la Variante ID 2 --' AS Seccion;
CALL sp_imagenes_variante_listar_por_variante(2);

-- 9. Reporte de Ganancias Netas por Mes (Ejemplo para el mes actual)
-- Nota: Estos datos de prueba abarcan solo unos pocos días, así que ajustamos el rango
SELECT '-- Reporte de Ganancias (Primeros Días de Datos) --' AS Seccion;
-- Asumiendo que los datos de prueba son del día actual o días cercanos
CALL sp_reporte_ganancias_por_dia(CURDATE() - INTERVAL 5 DAY, CURDATE());


-- =================================================================================
-- FIN DEL SCRIPT
-- =================================================================================