// Catálogo con filtros. Los filtros se guardan en la URL (?categoria=3&talla=M...) para poder
// compartir el enlace o volver atrás sin perderlos.
(function () {
    'use strict';

    const { getCatalogo } = DoGo.api;
    const { ICONS } = DoGo;
    const { openProductModal, productCard } = DoGo.producto;
    const { h, svg, whatsappUrl } = DoGo.ui;

    const grid = document.getElementById('productos-grid');
    const count = document.getElementById('productos-count');
    const form = document.getElementById('filtros');
    const toggle = document.getElementById('filtros-toggle');

    const ORDENES = {
        destacados: (a, b) => b.vendidos - a.vendidos,
        'precio-asc': (a, b) => (a.precioDesde ?? Infinity) - (b.precioDesde ?? Infinity),
        'precio-desc': (a, b) => (b.precioDesde ?? -Infinity) - (a.precioDesde ?? -Infinity),
        nombre: (a, b) => a.nombre.localeCompare(b.nombre, 'es'),
    };

    let catalogo = [];

    toggle.addEventListener('click', () => {
        const open = form.classList.toggle('open');
        toggle.setAttribute('aria-expanded', String(open));
    });

    async function init() {
        grid.replaceChildren(...Array.from({ length: 6 }, () => h('div', { className: 'product-card skeleton', 'aria-hidden': 'true' })));
        try {
            catalogo = await getCatalogo();
        } catch (error) {
            console.warn('Catálogo no disponible:', error.message);
            count.textContent = '';
            grid.replaceChildren(h('div', { className: 'error-state' },
                h('p', {}, 'No pudimos cargar el catálogo en este momento. Inténtalo más tarde o consúltanos directamente.'),
                h('a', { className: 'btn btn-whatsapp', href: whatsappUrl('Hola Do and Go, quisiera ver su catálogo.'), target: '_blank', rel: 'noopener' },
                    svg(ICONS.whatsapp), 'Consultar por WhatsApp')));
            return;
        }
        construirFiltros();
        leerUrl();
        aplicar();

        // Enlace directo a un producto: productos.html?producto=5 (lo usan los destacados del inicio)
        const id = Number(new URLSearchParams(window.location.search).get('producto'));
        const producto = catalogo.find((p) => p.id === id);
        if (producto) openProductModal(producto);
    }

    function construirFiltros() {
        const categorias = new Map();
        const tallas = new Set();
        const colores = new Set();
        for (const p of catalogo) {
            if (p.categoriaId) categorias.set(String(p.categoriaId), p.categoria);
            p.tallas.forEach((t) => tallas.add(t));
            p.colores.forEach((c) => colores.add(c));
        }
        fillSelect(form.categoria, [...categorias].sort((a, b) => a[1].localeCompare(b[1], 'es')), 'Todas');
        fillSelect(form.talla, ordenarTallas([...tallas]).map((t) => [t, t]), 'Todas');
        fillSelect(form.color, [...colores].sort((a, b) => a.localeCompare(b, 'es')).map((c) => [c, c]), 'Todos');

        form.addEventListener('input', () => { aplicar(); escribirUrl(); });
        form.addEventListener('submit', (e) => e.preventDefault());
        form.addEventListener('reset', () => setTimeout(() => { aplicar(); escribirUrl(); }));
    }

    function fillSelect(select, options, todos) {
        select.replaceChildren(h('option', { value: '' }, todos), ...options.map(([value, label]) => h('option', { value }, label)));
    }

    /** Tallas de ropa en orden lógico (XS, S, M, L...) y numéricas de menor a mayor. */
    function ordenarTallas(tallas) {
        const orden = ['XXS', 'XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL'];
        return tallas.sort((a, b) => {
            const ia = orden.indexOf(a.toUpperCase());
            const ib = orden.indexOf(b.toUpperCase());
            if (ia >= 0 && ib >= 0) return ia - ib;
            if (!Number.isNaN(Number(a)) && !Number.isNaN(Number(b))) return Number(a) - Number(b);
            if (ia >= 0) return -1;
            if (ib >= 0) return 1;
            return a.localeCompare(b, 'es');
        });
    }

    function filtros() {
        const f = new FormData(form);
        return {
            q: (f.get('q') || '').toString().trim().toLowerCase(),
            categoria: f.get('categoria') || '',
            talla: f.get('talla') || '',
            color: f.get('color') || '',
            min: f.get('min') ? Number(f.get('min')) : null,
            max: f.get('max') ? Number(f.get('max')) : null,
            disponibles: f.get('disponibles') === '1',
            orden: f.get('orden') || 'destacados',
        };
    }

    function aplicar() {
        const f = filtros();
        const resultado = catalogo.filter((p) => {
            if (f.categoria && String(p.categoriaId) !== f.categoria) return false;
            if (f.q && !`${p.nombre} ${p.categoria} ${p.colores.join(' ')}`.toLowerCase().includes(f.q)) return false;
            // Talla y color deben existir en la MISMA variante (y con stock si se pide "solo disponibles")
            const variantes = p.variantes.filter((v) => (!f.talla || v.talla === f.talla)
                && (!f.color || v.color === f.color) && (!f.disponibles || v.disponible));
            if ((f.talla || f.color || f.disponibles) && !variantes.length) return false;
            if (f.min !== null && (p.precioDesde ?? 0) < f.min) return false;
            if (f.max !== null && (p.precioDesde ?? Infinity) > f.max) return false;
            return true;
        }).sort(ORDENES[f.orden] || ORDENES.destacados);

        count.textContent = `${resultado.length} ${resultado.length === 1 ? 'producto' : 'productos'}`;
        grid.replaceChildren(...(resultado.length
            ? resultado.map((p) => productCard(p, abrir))
            : [h('div', { className: 'empty-state' },
                h('p', {}, 'No encontramos productos con esos filtros.'),
                h('button', { type: 'button', className: 'btn btn-outline btn-sm', onclick: () => form.reset() }, 'Limpiar filtros'))]));
    }

    function abrir(p) {
        const url = new URL(window.location.href);
        url.searchParams.set('producto', p.id);
        history.replaceState(null, '', url);
        openProductModal(p);
    }

    function leerUrl() {
        const params = new URLSearchParams(window.location.search);
        for (const name of ['q', 'categoria', 'talla', 'color', 'min', 'max', 'orden']) {
            if (params.has(name) && form.elements[name]) form.elements[name].value = params.get(name);
        }
        form.elements.disponibles.checked = params.get('disponibles') === '1';
    }

    function escribirUrl() {
        const params = new URLSearchParams();
        for (const [key, value] of new FormData(form)) {
            if (value && !(key === 'orden' && value === 'destacados')) params.set(key, value);
        }
        const query = params.toString();
        history.replaceState(null, '', query ? `?${query}` : window.location.pathname);
    }

    init();
})();
