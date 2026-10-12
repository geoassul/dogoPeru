-- ==========================================
-- STORED PROCEDURE ESPECÍFICO PARA RESOLVER PROBLEMA N+1 EN VENTAS
-- Este procedure obtiene todas las ventas con sus detalles en una sola consulta optimizada
-- ==========================================

DELIMITER //

-- Procedure para obtener ventas con detalles completos (resuelve N+1)
DROP PROCEDURE IF EXISTS sp_obtener_ventas_sin_n1//
CREATE PROCEDURE sp_obtener_ventas_sin_n1()
BEGIN
    SELECT 
        v.id as venta_id,
        v.fecha_venta,
        v.total_venta,
        dv.venta_id as detalle_venta_id,
        dv.variante_id as detalle_variante_id,
        dv.cantidad as detalle_cantidad,
        dv.precio_unitario_venta as detalle_precio_unitario,
        dv.costo_unitario_adquisicion as detalle_costo_unitario,
        vp.id as variante_id,
        vp.nombre_variante,
        vp.sku,
        vp.precio as variante_precio,
        vp.stock as variante_stock,
        p.id as producto_id,
        p.nombre as producto_nombre,
        p.descripcion as producto_descripcion,
        c.id as categoria_id,
        c.nombre as categoria_nombre,
        c.descripcion as categoria_descripcion,
        pr.id as proveedor_id,
        pr.nombre as proveedor_nombre,
        pr.contacto as proveedor_contacto,
        pr.telefono as proveedor_telefono,
        pr.email as proveedor_email,
        pr.direccion as proveedor_direccion
    FROM ventas v
    LEFT JOIN detalle_venta dv ON v.id = dv.venta_id
    LEFT JOIN variantes_producto vp ON dv.variante_id = vp.id
    LEFT JOIN productos p ON vp.producto_id = p.id
    LEFT JOIN categorias c ON p.categoria_id = c.id
    LEFT JOIN proveedores pr ON p.proveedor_id = pr.id
    ORDER BY v.fecha_venta DESC, v.id, dv.variante_id;
END//

-- Procedure para obtener una venta específica con detalles completos
DROP PROCEDURE IF EXISTS sp_obtener_venta_sin_n1//
CREATE PROCEDURE sp_obtener_venta_sin_n1(IN p_venta_id INT)
BEGIN
    SELECT 
        v.id as venta_id,
        v.fecha_venta,
        v.total_venta,
        dv.venta_id as detalle_venta_id,
        dv.variante_id as detalle_variante_id,
        dv.cantidad as detalle_cantidad,
        dv.precio_unitario_venta as detalle_precio_unitario,
        dv.costo_unitario_adquisicion as detalle_costo_unitario,
        vp.id as variante_id,
        vp.nombre_variante,
        vp.sku,
        vp.precio as variante_precio,
        vp.stock as variante_stock,
        p.id as producto_id,
        p.nombre as producto_nombre,
        p.descripcion as producto_descripcion,
        c.id as categoria_id,
        c.nombre as categoria_nombre,
        c.descripcion as categoria_descripcion,
        pr.id as proveedor_id,
        pr.nombre as proveedor_nombre,
        pr.contacto as proveedor_contacto,
        pr.telefono as proveedor_telefono,
        pr.email as proveedor_email,
        pr.direccion as proveedor_direccion
    FROM ventas v
    LEFT JOIN detalle_venta dv ON v.id = dv.venta_id
    LEFT JOIN variantes_producto vp ON dv.variante_id = vp.id
    LEFT JOIN productos p ON vp.producto_id = p.id
    LEFT JOIN categorias c ON p.categoria_id = c.id
    LEFT JOIN proveedores pr ON p.proveedor_id = pr.id
    WHERE v.id = p_venta_id
    ORDER BY dv.variante_id;
END//

-- Procedure para obtener ventas recientes sin N+1 (últimas 10)
DROP PROCEDURE IF EXISTS sp_obtener_ventas_recientes_sin_n1//
CREATE PROCEDURE sp_obtener_ventas_recientes_sin_n1()
BEGIN
    SELECT 
        v.id as venta_id,
        v.fecha_venta,
        v.total_venta,
        dv.venta_id as detalle_venta_id,
        dv.variante_id as detalle_variante_id,
        dv.cantidad as detalle_cantidad,
        dv.precio_unitario_venta as detalle_precio_unitario,
        dv.costo_unitario_adquisicion as detalle_costo_unitario,
        vp.id as variante_id,
        vp.nombre_variante,
        vp.sku,
        vp.precio as variante_precio,
        vp.stock as variante_stock,
        p.id as producto_id,
        p.nombre as producto_nombre,
        p.descripcion as producto_descripcion,
        c.id as categoria_id,
        c.nombre as categoria_nombre,
        c.descripcion as categoria_descripcion,
        pr.id as proveedor_id,
        pr.nombre as proveedor_nombre,
        pr.contacto as proveedor_contacto,
        pr.telefono as proveedor_telefono,
        pr.email as proveedor_email,
        pr.direccion as proveedor_direccion
    FROM ventas v
    LEFT JOIN detalle_venta dv ON v.id = dv.venta_id
    LEFT JOIN variantes_producto vp ON dv.variante_id = vp.id
    LEFT JOIN productos p ON vp.producto_id = p.id
    LEFT JOIN categorias c ON p.categoria_id = c.id
    LEFT JOIN proveedores pr ON p.proveedor_id = pr.id
    WHERE v.id IN (
        SELECT id FROM ventas 
        ORDER BY fecha_venta DESC 
        LIMIT 10
    )
    ORDER BY v.fecha_venta DESC, v.id, dv.variante_id;
END//

DELIMITER ;

-- Ejemplos de uso:
-- CALL sp_obtener_ventas_sin_n1();
-- CALL sp_obtener_venta_sin_n1(1);
-- CALL sp_obtener_ventas_recientes_sin_n1();
