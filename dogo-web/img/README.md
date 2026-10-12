# Imágenes de productos

La base de datos guarda la **ruta** de cada imagen (tabla `imagenes_variante`, columna `url_imagen`), por ejemplo
`/img/polos/polo_basico_azul_m_principal.jpg`. La web busca ese archivo **dentro de esta carpeta `img/`**.

Mientras un archivo no exista, la web muestra una imagen provisional según la categoría (configurada en
`js/config.js` → `imagenesRespaldo`). En cuanto subas el archivo con el nombre exacto, aparece la foto real.

## Archivos que ya espera la base de datos

| Carpeta | Archivo |
|---|---|
| `img/polos/` | `polo_basico_azul_m_principal.jpg`, `polo_basico_azul_m_galeria1.jpg`, `polo_basico_azul_m_detalle_tela.jpg`, `polo_basico_rojo_m_principal.jpg` |
| `img/jeans/` | `jean_clasico_azul_32_principal.jpg`, `jean_clasico_azul_32_vista_trasera.jpg` |
| `img/camisas/` | `camisa_blanca_m_principal.jpg` |
| `img/zapatillas/` | `zapatilla_blanca_42_principal.jpg`, `zapatilla_blanca_42_lateral.jpg`, `zapatilla_blanca_42_suela.jpg` |
| `img/chaquetas/` | `chaqueta_gris_l_principal.jpg` |

Para productos nuevos: sube la foto aquí y registra su ruta en el panel de administración
(**Variantes y stock → Imágenes**) o con `POST /api/imagenes-variante`.

## Recomendaciones para que la web cargue rápido

- **Proporción 4:5** (vertical), por ejemplo **1200 × 1500 px**. Las tarjetas recortan a ese formato.
- **Peso menor a 200 KB** por foto. Formato **WebP** o JPG con calidad 75–80.
  Herramientas gratis: [Squoosh](https://squoosh.app) o [TinyPNG](https://tinypng.com).
- Fondo claro y uniforme, la prenda centrada, sin marcas de agua.
- Nombres en minúsculas, sin espacios ni tildes: `producto_color_talla_tipo.jpg`.
- Si cambias a `.webp`, actualiza también la ruta guardada en la base de datos.
