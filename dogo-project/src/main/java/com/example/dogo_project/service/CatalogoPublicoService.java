package com.example.dogo_project.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dogo_project.dto.publico.CategoriaPublicaDTO;
import com.example.dogo_project.dto.publico.ImagenPublicaDTO;
import com.example.dogo_project.dto.publico.ProductoPublicoDTO;
import com.example.dogo_project.dto.publico.VariantePublicaDTO;
import com.example.dogo_project.model.ImagenVariante;
import com.example.dogo_project.model.Producto;
import com.example.dogo_project.model.VarianteProducto;
import com.example.dogo_project.repository.DetalleVentaRepository;
import com.example.dogo_project.repository.ImagenVarianteRepository;
import com.example.dogo_project.repository.ProductoRepository;
import com.example.dogo_project.repository.VarianteProductoRepository;

/**
 * Catálogo para la web pública. Se arma con cuatro consultas fijas (productos, variantes,
 * imágenes y unidades vendidas) y se agrupa en memoria: el número de consultas no crece con el
 * catálogo. No expone costo, stock exacto ni proveedor.
 */
@Service
@Transactional(readOnly = true)
public class CatalogoPublicoService {

    /** Con este stock o menos la variante se muestra como "últimas unidades". */
    private static final int ULTIMAS_UNIDADES = 3;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VarianteProductoRepository varianteProductoRepository;

    @Autowired
    private ImagenVarianteRepository imagenVarianteRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    /** Productos ordenados por más vendidos (destacados primero) y luego por nombre. */
    public List<ProductoPublicoDTO> catalogo() {
        List<Producto> productos = productoRepository.findAllConRelaciones();

        Map<Integer, List<VarianteProducto>> variantesPorProducto = new HashMap<>();
        Map<Integer, Integer> productoDeVariante = new HashMap<>();
        for (VarianteProducto v : varianteProductoRepository.findAllConProducto()) {
            variantesPorProducto.computeIfAbsent(v.getProducto().getId(), k -> new ArrayList<>()).add(v);
            productoDeVariante.put(v.getId(), v.getProducto().getId());
        }

        // Las imágenes se guardan por variante; en la web se muestran agrupadas por producto
        Map<Integer, List<ImagenPublicaDTO>> imagenesPorProducto = new HashMap<>();
        for (ImagenVariante img : imagenVarianteRepository.findAllOrdenadas()) {
            Integer varianteId = img.getVariante().getId();
            Integer productoId = productoDeVariante.get(varianteId);
            if (productoId != null && img.getUrlImagen() != null) {
                imagenesPorProducto.computeIfAbsent(productoId, k -> new ArrayList<>())
                        .add(new ImagenPublicaDTO(img.getUrlImagen(), img.getTipoImagen(), img.getAltText(), varianteId));
            }
        }

        Map<Integer, Long> vendidos = new HashMap<>();
        for (Object[] fila : detalleVentaRepository.unidadesVendidasPorProducto()) {
            vendidos.put((Integer) fila[0], ((Number) fila[1]).longValue());
        }

        List<ProductoPublicoDTO> resultado = new ArrayList<>();
        for (Producto p : productos) {
            List<VarianteProducto> variantes = variantesPorProducto.getOrDefault(p.getId(), List.of());
            List<ImagenPublicaDTO> imagenes = new ArrayList<>(imagenesPorProducto.getOrDefault(p.getId(), List.of()));
            // La imagen "principal" va primero
            imagenes.sort(Comparator.comparing((ImagenPublicaDTO i) -> !"principal".equalsIgnoreCase(i.tipo())));
            resultado.add(toDto(p, variantes, imagenes, vendidos.getOrDefault(p.getId(), 0L)));
        }
        resultado.sort(Comparator.comparingLong(ProductoPublicoDTO::vendidos).reversed()
                .thenComparing(ProductoPublicoDTO::nombre, String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }

    /** Categorías que tienen al menos un producto, con una imagen representativa. */
    public List<CategoriaPublicaDTO> categorias() {
        return categoriasDe(catalogo());
    }

    public static List<CategoriaPublicaDTO> categoriasDe(List<ProductoPublicoDTO> catalogo) {
        Map<Integer, String> nombres = new LinkedHashMap<>();
        Map<Integer, Long> conteo = new HashMap<>();
        Map<Integer, String> imagen = new HashMap<>();
        for (ProductoPublicoDTO p : catalogo) {
            if (p.categoriaId() == null) {
                continue;
            }
            nombres.putIfAbsent(p.categoriaId(), p.categoria());
            conteo.merge(p.categoriaId(), 1L, Long::sum);
            if (!p.imagenes().isEmpty()) {
                imagen.putIfAbsent(p.categoriaId(), p.imagenes().get(0).url());
            }
        }
        return nombres.entrySet().stream()
                .map(e -> new CategoriaPublicaDTO(e.getKey(), e.getValue(), conteo.get(e.getKey()), imagen.get(e.getKey())))
                .sorted(Comparator.comparing(CategoriaPublicaDTO::nombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private static ProductoPublicoDTO toDto(Producto p, List<VarianteProducto> variantes,
                                            List<ImagenPublicaDTO> imagenes, long vendidos) {
        Set<String> tallas = new LinkedHashSet<>();
        Set<String> colores = new LinkedHashSet<>();
        List<VariantePublicaDTO> variantesDto = new ArrayList<>();
        Double precioDesde = null;
        boolean disponible = false;

        for (VarianteProducto v : variantes) {
            int stock = v.getStock() != null ? v.getStock() : 0;
            boolean hayStock = stock > 0;
            disponible |= hayStock;
            if (v.getTalla() != null && !v.getTalla().isBlank()) tallas.add(v.getTalla());
            if (v.getColor() != null && !v.getColor().isBlank()) colores.add(v.getColor());
            if (v.getPrecioVenta() != null && (precioDesde == null || v.getPrecioVenta() < precioDesde)) {
                precioDesde = v.getPrecioVenta();
            }
            variantesDto.add(new VariantePublicaDTO(v.getId(), v.getSku(), v.getTalla(), v.getColor(), v.getTipoManga(),
                    v.getPrecioVenta(), hayStock, hayStock && stock <= ULTIMAS_UNIDADES));
        }

        return new ProductoPublicoDTO(
                p.getId(),
                p.getNombre(),
                p.getCategoria() != null ? p.getCategoria().getId() : null,
                p.getCategoria() != null ? p.getCategoria().getNombre() : null,
                precioDesde,
                disponible,
                List.copyOf(tallas),
                List.copyOf(colores),
                vendidos,
                variantesDto,
                imagenes);
    }
}
