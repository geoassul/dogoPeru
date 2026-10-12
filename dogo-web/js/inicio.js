// Página de inicio: slider, categorías y productos destacados (desde la API).
(function () {
    'use strict';

    const { getCatalogo, getCategorias } = DoGo.api;
    const { productCard } = DoGo.producto;
    const { initSlider } = DoGo.slider;
    const { h, productImage, whatsappUrl } = DoGo.ui;

    const DESTACADOS = 8;

    initSlider(document.querySelector('.slider'));

    const irAProducto = (p) => { window.location.href = `productos.html?producto=${p.id}`; };

    async function cargarCategorias() {
        const box = document.getElementById('categorias-grid');
        try {
            const categorias = await getCategorias();
            box.replaceChildren(...categorias.map((c) => h('a', {
                className: 'category-card', href: `productos.html?categoria=${encodeURIComponent(c.id)}`,
            },
            productImage(c.imagen, c.nombre, '', { width: 400, height: 500 }),
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
            const productos = (await getCatalogo()).slice(0, DESTACADOS);
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
