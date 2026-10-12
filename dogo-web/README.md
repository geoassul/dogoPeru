# Do and Go — Web pública

Página informativa de la tienda de ropa **Do and Go** (Mercado Carapongo, Lurigancho-Chosica, Lima).
Hecha solo con **HTML, CSS y JavaScript** (sin frameworks ni compilación). Los productos se leen del backend
(`dogo-project`) por su API pública.

## Páginas

| Página | Contenido |
|---|---|
| `index.html` | Slider, beneficios, categorías y productos destacados (API), historia breve, ubicación con mapa, llamado a WhatsApp |
| `productos.html` | Catálogo con filtros (categoría, talla, color, precio, disponibilidad, búsqueda, orden) y detalle con pedido por WhatsApp |
| `nosotros.html` | Historia, misión, visión, valores, línea de tiempo y ubicación |
| `contacto.html` | Datos de contacto, formulario (WhatsApp o correo), mapa, preguntas frecuentes y Libro de Reclamaciones |
| `404.html` | Página no encontrada |

## Dónde cambiar cada cosa

| Qué | Dónde |
|---|---|
| Teléfono, WhatsApp, correo, horario, redes, dirección, medios de pago | `js/config.js` (valores de ejemplo marcados `TODO`) **y** su copia escrita en los `.html` (bloques HEADER/FOOTER y sección Visítanos/Contacto) |
| URL del backend | `js/config.js` → `API_BASE_URL` |
| Envío del formulario por correo | `js/config.js` → `FORMSPREE_ID` (crear formulario gratis en formspree.io) |
| Historia, misión y visión | `nosotros.html` (texto de **ejemplo**: revísalo) |
| Slider del inicio | `index.html`, sección `SLIDER` |
| Menú y footer | Bloques `HEADER` y `FOOTER` al inicio y final de cada `.html` (son idénticos en las 5 páginas: si cambias uno, cópialo en las demás) |
| Colores y estilos | `css/styles.css` (variables al inicio) |
| Fotos de productos | carpeta `img/` — ver `img/README.md` |

## Probar en tu PC

1. Levanta el backend (`dogo-project`) en el puerto 8080 (los productos salen de ahí).
2. Abre `index.html` con doble clic, o con la extensión **Live Server** de VS Code. Las dos formas funcionan.

> Sin el backend encendido, la web se ve completa (menú, slider, datos de contacto, mapa, footer); solo las
> secciones de productos muestran un aviso con botón de WhatsApp.

Si el backend corre en otro puerto, sin tocar el código: en la consola del navegador ejecuta
`localStorage.setItem('dogoApiUrl', 'http://localhost:8081')` y recarga.

## Publicar

1. Sube esta carpeta a **Netlify**, **Vercel**, **Cloudflare Pages** o **GitHub Pages** (todos gratis para sitios estáticos).
   La carpeta `img/` con tus fotos va incluida.
2. Publica el backend en un servidor con HTTPS y pon su URL en `API_BASE_URL`.
   El catálogo público acepta peticiones desde cualquier dominio, no hay que configurar CORS.
3. Cambia el dominio en `sitemap.xml` y `robots.txt`.

## Notas técnicas

- **Funciona sin servidor ni JavaScript:** header, footer, datos de contacto, slider y mapa están escritos en el HTML.
  El JavaScript (scripts clásicos con `defer`, sin módulos) solo añade el catálogo, filtros, menú móvil y actualiza
  los datos desde `js/config.js`. Si cambias un dato en `config.js`, cámbialo también en el HTML para quien no tenga JS.
- **Seguridad:** los datos de la API se insertan como texto (`textContent`), nunca como HTML, para evitar inyección de código.
- **Rendimiento:** la primera imagen del slider se precarga; el resto de imágenes y el mapa se cargan de forma diferida.
  El catálogo se guarda 60 s en `sessionStorage` para no descargarlo en cada página.
- **Si el backend no responde**, las secciones de productos muestran un mensaje con botón de WhatsApp; el resto de la web funciona igual.
- **SEO:** título y descripción por página, Open Graph para compartir y datos estructurados `ClothingStore` (dirección y coordenadas).
- **Accesibilidad:** navegación por teclado, textos alternativos, foco visible y respeto por "reducir movimiento".
