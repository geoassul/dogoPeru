// Comportamiento común a todas las páginas.
// El header, el footer y los datos de contacto están escritos en el HTML (se ven aunque el JavaScript
// no cargue). Este script los mejora: menú móvil, sombra al hacer scroll y datos tomados de config.js.
window.DoGo = window.DoGo || {};

(function () {
    'use strict';

    const { CONFIG, ICONS } = DoGo;
    const { h, svg, whatsappUrl, comoLlegarUrl, mapa, mapaEmbedUrl } = DoGo.ui;
    const T = CONFIG.tienda;

    function direccionCompleta() {
        const d = T.direccion;
        return `${d.linea1}, ${d.linea2}, ${d.ciudad} ${d.codigoPostal}`;
    }

    // ---------- Menú ----------

    function initHeader() {
        const header = document.querySelector('.site-header');
        const toggle = document.querySelector('.nav-toggle');
        const nav = document.getElementById('main-nav');
        if (!header || !toggle || !nav) return;

        // Marca la página actual (por si se copió el header sin actualizar aria-current)
        const actual = window.location.pathname.split('/').pop() || 'index.html';
        nav.querySelectorAll('ul a').forEach((a) => {
            if (a.getAttribute('href') === actual) a.setAttribute('aria-current', 'page');
            else a.removeAttribute('aria-current');
        });

        const setOpen = (open) => {
            nav.classList.toggle('open', open);
            toggle.setAttribute('aria-expanded', String(open));
            toggle.setAttribute('aria-label', open ? 'Cerrar menú' : 'Abrir menú');
            toggle.replaceChildren(svg(open ? ICONS.close : ICONS.menu));
            document.body.classList.toggle('nav-open', open);
        };
        toggle.addEventListener('click', () => setOpen(!nav.classList.contains('open')));
        document.addEventListener('keydown', (e) => { if (e.key === 'Escape') setOpen(false); });
        nav.addEventListener('click', (e) => { if (e.target.closest('a')) setOpen(false); });

        // Sombra del header al bajar más de 10 px. Un IntersectionObserver sobre un marcador de 10 px al inicio
        // de la página evita leer window.scrollY, que fuerza al navegador a recalcular el diseño al cargar.
        const marcador = h('div', { 'aria-hidden': 'true' });
        marcador.style.cssText = 'position:absolute;top:0;left:0;width:1px;height:10px;pointer-events:none;';
        document.body.prepend(marcador);
        new IntersectionObserver(([entry]) => header.classList.toggle('scrolled', !entry.isIntersecting))
            .observe(marcador);
    }

    // ---------- Datos de la tienda ----------

    /**
     * Actualiza los elementos con data-cfg="..." con los datos de config.js.
     * El HTML ya trae estos datos escritos; así, si cambias config.js, se actualizan en todas las páginas.
     */
    function fillConfig(root = document) {
        const link = (el, href) => {
            el.href = href;
            if (/^https?:/.test(href)) { el.target = '_blank'; el.rel = 'noopener'; }
        };
        const red = (key) => (el) => {
            if (T.redes[key]) link(el, T.redes[key]);
            else el.closest('li')?.remove(); // red sin enlace configurado: se oculta
        };
        const fillers = {
            nombre: (el) => { el.textContent = T.nombre; },
            descripcion: (el) => { el.textContent = T.descripcion; },
            direccion: (el) => { el.textContent = direccionCompleta(); },
            referencia: (el) => { el.textContent = T.direccion.referencia; },
            telefono: (el) => {
                el.textContent = T.telefono;
                if (el.tagName === 'A') el.href = `tel:${T.telefono.replace(/\s+/g, '')}`;
            },
            email: (el) => {
                el.textContent = T.email;
                if (el.tagName === 'A') el.href = `mailto:${T.email}`;
            },
            whatsapp: (el) => link(el, whatsappUrl(el.dataset.msg || '')),
            'como-llegar': (el) => link(el, comoLlegarUrl()),
            'google-maps': (el) => link(el, T.googleMapsUrl),
            horario: (el) => el.replaceChildren(...T.horario.map((x) => h('li', {}, h('strong', {}, x.dias), ' ', x.horas))),
            'horario-corto': (el) => { el.textContent = `${T.horario[0].dias}: ${T.horario[0].horas}`; },
            pagos: (el) => el.replaceChildren(...T.mediosPago.map((m) => h('li', {}, m))),
            'pagos-texto': (el) => { el.textContent = T.mediosPago.join(', '); },
            mapa: (el) => el.replaceChildren(mapa()),
            'mapa-iframe': (el) => { if (el.src !== mapaEmbedUrl()) el.src = mapaEmbedUrl(); },
            anios: (el) => { el.textContent = String(new Date().getFullYear() - T.anioFundacion); },
            fundacion: (el) => { el.textContent = String(T.anioFundacion); },
            'anio-actual': (el) => { el.textContent = String(new Date().getFullYear()); },
            'libro-reclamaciones': (el) => { el.href = T.libroReclamaciones; },
            'red-facebook': red('facebook'),
            'red-instagram': red('instagram'),
            'red-tiktok': red('tiktok'),
        };
        root.querySelectorAll('[data-cfg]').forEach((el) => fillers[el.dataset.cfg]?.(el));
    }

    /** Datos estructurados para Google (aparece como tienda local con dirección y coordenadas). */
    function addStructuredData() {
        const data = {
            '@context': 'https://schema.org',
            '@type': 'ClothingStore',
            name: T.nombreLegal,
            description: T.descripcion,
            telephone: T.telefono,
            email: T.email,
            address: {
                '@type': 'PostalAddress',
                streetAddress: T.direccion.linea1,
                addressLocality: T.direccion.linea2,
                addressRegion: T.direccion.ciudad,
                postalCode: T.direccion.codigoPostal,
                addressCountry: 'PE',
            },
            geo: { '@type': 'GeoCoordinates', latitude: T.coordenadas.lat, longitude: T.coordenadas.lng },
            hasMap: T.googleMapsUrl,
            paymentAccepted: T.mediosPago.join(', '),
            sameAs: Object.values(T.redes).filter(Boolean),
        };
        document.head.append(h('script', { type: 'application/ld+json' }, JSON.stringify(data)));
    }

    /** Enlaces como contacto.html#libro-reclamaciones abren la pregunta correspondiente. */
    function openDetailsFromHash() {
        const target = window.location.hash && document.getElementById(window.location.hash.slice(1));
        if (target instanceof HTMLDetailsElement) {
            target.open = true;
            target.scrollIntoView();
        }
    }

    initHeader();
    fillConfig();
    addStructuredData();
    openDetailsFromHash();
    window.addEventListener('hashchange', openDetailsFromHash);

    DoGo.layout = { direccionCompleta };
})();
