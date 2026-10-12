// Utilidades de interfaz. Todo dato externo (API) se inserta como texto con textContent,
// nunca con innerHTML, para que no pueda inyectar HTML ni scripts (XSS).
window.DoGo = window.DoGo || {};

(function () {
    'use strict';

    const { CONFIG } = DoGo;

    /** Crea un elemento: h('a', { href: '#', className: 'btn' }, 'Texto', otroNodo) */
    function h(tag, attrs = {}, ...children) {
        const el = document.createElement(tag);
        for (const [key, value] of Object.entries(attrs || {})) {
            if (value === undefined || value === null || value === false) continue;
            if (key.startsWith('on') && typeof value === 'function') {
                el.addEventListener(key.slice(2).toLowerCase(), value);
            } else if (key === 'className') {
                el.className = value;
            } else if (key === 'dataset') {
                Object.assign(el.dataset, value);
            } else if (key in el && typeof value !== 'string') {
                el[key] = value;
            } else {
                el.setAttribute(key, value === true ? '' : value);
            }
        }
        for (const child of children.flat(Infinity)) {
            if (child === undefined || child === null || child === false) continue;
            el.append(child instanceof Node ? child : document.createTextNode(String(child)));
        }
        return el;
    }

    /** Inserta SVG de confianza (iconos propios de icons.js, nunca datos de la API). */
    function svg(markup) {
        const template = document.createElement('template');
        template.innerHTML = markup.trim();
        return template.content.firstElementChild;
    }

    const moneyFormat = new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' });

    function money(value) {
        return typeof value === 'number' ? moneyFormat.format(value) : 'Consultar';
    }

    // ---------- Imágenes ----------

    /** Tamaño optimizado de una imagen de Unsplash (formato automático WebP/AVIF). */
    function unsplash(url, width, height) {
        const params = new URLSearchParams({ auto: 'format', fit: 'crop', q: '70', w: String(width) });
        if (height) params.set('h', String(height));
        return `${url}?${params}`;
    }

    /** Imagen provisional según el nombre de la categoría. */
    function imagenRespaldo(categoria = '') {
        const c = categoria.toLowerCase();
        const r = CONFIG.imagenesRespaldo;
        if (c.includes('polo')) return r.polo;
        if (c.includes('camisa')) return r.camisa;
        if (c.includes('vaquer') || c.includes('jean')) return r.jean;
        if (c.includes('pantal') || c.includes('short')) return r.pantalon;
        if (c.includes('chaquet') || c.includes('casaca') || c.includes('abrigo') || c.includes('sudader')) return r.chaqueta;
        if (c.includes('zapat') || c.includes('calzad')) return r.zapato;
        return r.general;
    }

    /**
     * <img> de producto. La BD guarda rutas relativas al hosting de la web (/img/...).
     * Si el archivo todavía no existe se cambia a la imagen provisional de la categoría.
     */
    function productImage(url, categoria, alt, { width = 600, height = 600, eager = false } = {}) {
        const respaldo = unsplash(imagenRespaldo(categoria), width, height);
        const img = h('img', {
            src: url ? resolverUrl(url) : respaldo,
            alt: alt || '',
            width: String(width),
            height: String(height),
            loading: eager ? 'eager' : 'lazy',
            decoding: 'async',
        });
        img.addEventListener('error', () => {
            if (img.dataset.respaldo) return; // evita un bucle si también falla la de respaldo
            img.dataset.respaldo = '1';
            img.src = respaldo;
        });
        return img;
    }

    function resolverUrl(url) {
        if (/^https?:\/\//i.test(url)) return url;
        // Rutas /img/... relativas a la raíz de esta web; se quita la barra para que funcione también en subcarpetas
        return url.replace(/^\/+/, '');
    }

    // ---------- WhatsApp ----------

    function whatsappUrl(mensaje) {
        const texto = mensaje ? `?text=${encodeURIComponent(mensaje)}` : '';
        return `https://wa.me/${CONFIG.tienda.whatsapp}${texto}`;
    }

    // ---------- Mapa ----------

    function mapaEmbedUrl() {
        if (CONFIG.tienda.googleMapsEmbedUrl) return CONFIG.tienda.googleMapsEmbedUrl;
        const { lat, lng } = CONFIG.tienda.coordenadas;
        // Formato final de Google (sin redirección intermedia) con un marcador en las coordenadas
        return `https://www.google.com/maps/embed?origin=mfe&pb=!1m3!2m1!1s${lat},${lng}!6i17!3m1!1ses!5m1!1ses`;
    }

    function comoLlegarUrl() {
        const { lat, lng } = CONFIG.tienda.coordenadas;
        return `https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`;
    }

    /** Iframe del mapa que se carga cuando el usuario se acerca (no frena la carga inicial). */
    function mapa(titulo = 'Ubicación de Do and Go en Google Maps') {
        return h('iframe', {
            className: 'map-frame',
            src: mapaEmbedUrl(),
            title: titulo,
            loading: 'lazy',
            referrerpolicy: 'no-referrer-when-downgrade',
            allowfullscreen: true,
        });
    }

    DoGo.ui = { h, svg, money, unsplash, imagenRespaldo, productImage, whatsappUrl, mapaEmbedUrl, comoLlegarUrl, mapa };
})();
