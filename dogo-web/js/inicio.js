// Página de inicio: slider, categorías y productos destacados (desde la API).
(function () {
    'use strict';

    const { getCatalogo, getCategorias } = DoGo.api;
    const { productCard } = DoGo.producto;
    const { initSlider } = DoGo.slider;
    const { h, productImage, whatsappUrl } = DoGo.ui;

    // Las grillas del inicio van en filas completas de 3 (ver .product-grid / .category-grid en styles.css)
    const COLUMNAS = 3;
    const DESTACADOS = 2 * COLUMNAS;

    /** Recorta a un múltiplo de COLUMNAS para no dejar huecos en la última fila (mínimo una fila). */
    const filasCompletas = (lista) => lista.slice(0, Math.max(COLUMNAS, lista.length - (lista.length % COLUMNAS)));

    initSlider(document.querySelector('.slider'));

    const irAProducto = (p) => { window.location.href = `productos.html?producto=${p.id}`; };

    async function cargarCategorias() {
        const box = document.getElementById('categorias-grid');
        try {
            // Si sobran, se quedan fuera las que tienen menos productos (siguen en "Ver todo el catálogo")
            const categorias = filasCompletas([...await getCategorias()].sort((a, b) => b.productos - a.productos));
            box.replaceChildren(...categorias.map((c) => h('a', {
                className: 'category-card', href: `productos.html?categoria=${encodeURIComponent(c.id)}`,
            },
            productImage(c.imagen, c.nombre, '', { width: 500, height: 500 }),
            h('span', { className: 'category-info' },
                h('span', { className: 'category-name' }, c.nombre),
                h('span', { className: 'category-count' }, `${c.productos} ${c.productos === 1 ? 'producto' : 'productos'}`)))));
        } catch (error) {
            box.replaceChildren(errorBox(error));
        }
    }

    async function cargarDestacados() {
        const box = document.getElementById('destacados-grid');
        try {
            const productos = filasCompletas((await getCatalogo()).slice(0, DESTACADOS));
            box.replaceChildren(...(productos.length
                ? productos.map((p) => productCard(p, irAProducto))
                : [h('p', { className: 'empty-state' }, 'Pronto publicaremos nuestros productos.')]));
        } catch (error) {
            box.replaceChildren(errorBox(error));
        }
    }

    function errorBox(error) {
        console.warn('Catálogo no disponible:', error.message);
        return h('div', { className: 'error-state' },
            h('p', {}, 'No pudimos cargar el catálogo en este momento.'),
            h('a', { className: 'btn btn-whatsapp btn-sm', href: whatsappUrl('Hola Do and Go, quisiera ver su catálogo.'), target: '_blank', rel: 'noopener' },
                'Consúltanos por WhatsApp'));
    }

    cargarCategorias();
    cargarDestacados();
})();
