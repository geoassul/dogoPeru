package com.example.dogo_project.dto;


import com.example.dogo_project.model.Categoria;
import com.example.dogo_project.model.DetalleVenta;
import com.example.dogo_project.model.ImagenVariante;
import com.example.dogo_project.model.Producto;
import com.example.dogo_project.model.Proveedor;
import com.example.dogo_project.model.VarianteProducto;
import com.example.dogo_project.model.Venta;

/**
 * Conversión explícita entidad -> DTO.
 *
 * Reemplaza a ModelMapper en las respuestas: ModelMapper recorre las relaciones por reflexión
 * y, con asociaciones LAZY, provoca consultas extra (N+1). Aquí solo se leen los campos que
 * las consultas de los repositorios ya traen con JOIN FETCH. Leer el id de una relación LAZY
 * (p. ej. variante.getId()) no dispara consultas: Hibernate lo toma de la FK.
 */
public final class DtoMapper {

    private DtoMapper() {}

    public static CategoriaDTO toDto(Categoria c) {
        return new CategoriaDTO(c.getId(), c.getNombre());
    }

    public static ProveedorDTO toDto(Proveedor p) {
        return new ProveedorDTO(p.getId(), p.getNombre(), p.getTelefono());
    }

    public static ProductoDTO toDto(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        if (p.getCategoria() != null) {
            dto.setCategoriaId(p.getCategoria().getId());
            dto.setCategoriaNombre(p.getCategoria().getNombre());
        }
        if (p.getProveedor() != null) {
            dto.setProveedorId(p.getProveedor().getId());
            dto.setProveedorNombre(p.getProveedor().getNombre());
        }
        return dto;
    }

    public static VarianteProductoDTO toDto(VarianteProducto v) {
        VarianteProductoDTO dto = new VarianteProductoDTO();
        dto.setId(v.getId());
        if (v.getProducto() != null) {
            dto.setProductoId(v.getProducto().getId());
            dto.setProductoNombre(v.getProducto().getNombre());
        }
        dto.setSku(v.getSku());
        dto.setTalla(v.getTalla());
        dto.setTipoManga(v.getTipoManga());
        dto.setColor(v.getColor());
        dto.setCostoAdquisicion(v.getCostoAdquisicion());
        dto.setPrecioVenta(v.getPrecioVenta());
        dto.setStock(v.getStock());
        return dto;
    }

    public static ImagenVarianteDTO toDto(ImagenVariante i) {
        return new ImagenVarianteDTO(
                i.getId(),
                i.getVariante() != null ? i.getVariante().getId() : null,
                i.getUrlImagen(),
                i.getNombreArchivo(),
                i.getTipoImagen(),
                i.getOrden(),
                i.getAltText());
    }

    public static DetalleVentaDTO toDto(DetalleVenta d) {
        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setVentaId(d.getId().getVentaId());
        dto.setVarianteId(d.getId().getVarianteId());
        if (d.getVariante() != null) {
            dto.setVarianteSku(d.getVariante().getSku());
        }
        dto.setCantidad(d.getCantidad());
        dto.setPrecioUnitarioVenta(d.getPrecioUnitarioVenta());
        dto.setCostoUnitarioAdquisicion(d.getCostoUnitarioAdquisicion());
        return dto;
    }

    public static VentaDTO toDto(Venta v) {
        VentaDTO dto = new VentaDTO();
        dto.setId(v.getId());
        dto.setFechaVenta(v.getFechaVenta());
        dto.setTotalVenta(v.getTotalVenta());
        dto.setDetalles(v.getDetalles().stream().map(DtoMapper::toDto).toList());
        return dto;
    }
}
