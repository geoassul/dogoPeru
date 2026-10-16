// Tarjeta de producto y modal de detalle (compartidos por Inicio y Productos).
window.DoGo = window.DoGo || {};

(function () {
    'use strict';

    const { ICONS } = DoGo;
    const { h, svg, money, productImage, whatsappUrl } = DoGo.ui;

    /** Estado de stock para mostrar al cliente (nunca el número exacto). */
    function estado(p) {
        if (!p.disponible) return { texto: 'Agotado', clase: 'badge-out' };
        const todasPocas = p.variantes.filter((v) => v.disponible).every((v) => v.ultimasUnidades);
        return todasPocas ? { texto: 'Últimas unidades', clase: 'badge-low' } : { texto: 'Disponible', clase: 'badge-ok' };
    }

    function productCard(p, onOpen) {
        const e = estado(p);
        const principal = p.imagenes[0];
        return h('article', { className: 'product-card' },
            h('button', {
                type: 'button', className: 'product-media', onclick: () => onOpen(p),
                'aria-label': `Ver detalle de ${p.nombre}`,
            },
            productImage(principal?.url, p.categoria, principal?.alt || p.nombre, { width: 500, height: 500 }),
            h('span', { className: `badge ${e.clase}` }, e.texto)),
            h('div', { className: 'product-body' },
                h('p', { className: 'product-cat' }, p.categoria || ''),
                h('h3', { className: 'product-name' }, h('button', { type: 'button', onclick: () => onOpen(p) }, p.nombre)),
                h('p', { className: 'product-price' }, p.variantes.length > 1 ? 'Desde ' : '', h('strong', {}, money(p.precioDesde))),
                p.colores.length ? h('p', { className: 'product-meta' }, p.colores.join(' · ')) : null,
                p.tallas.length ? h('p', { className: 'product-meta' }, 'Tallas: ', p.tallas.join(', ')) : null,
                h('div', { className: 'product-actions' },
                    h('button', { type: 'button', className: 'btn btn-outline btn-sm', onclick: () => onOpen(p) }, 'Ver detalle'),
                    h('a', {
                        className: 'btn btn-whatsapp btn-sm', href: whatsappUrl(mensajeProducto(p)), target: '_blank', rel: 'noopener',
                        'aria-label': `Consultar ${p.nombre} por WhatsApp`,
                    }, svg(ICONS.whatsapp), 'Pedir'))));
    }

    function mensajeProducto(p, variante) {
        if (!variante) return `Hola Do and Go, me interesa el producto "${p.nombre}". ¿Qué tallas y colores tienen disponibles?`;
        const detalle = [variante.color && `Color: ${variante.color}`, variante.talla && `Talla: ${variante.talla}`]
            .filter(Boolean).join(', ');
        return `Hola Do and Go, quiero pedir: "${p.nombre}" (${detalle}) - ${money(variante.precio)} - Código ${variante.sku}. `
            + '¿Está disponible?';
    }

    // ---------- Modal de detalle ----------

    let dialog;

    function openProductModal(p) {
        dialog?.remove();
        dialog = h('dialog', { className: 'product-dialog', 'aria-labelledby': 'dialog-title' });
        document.body.append(dialog);

        const colores = [...new Set(p.variantes.map((v) => v.color).filter(Boolean))];
        // Por defecto: el primer color con stock
        let color = (p.variantes.find((v) => v.disponible) || p.variantes[0])?.color ?? null;
        let talla = null;

        const mainImage = h('div', { className: 'gallery-main' });
        const thumbs = h('div', { className: 'gallery-thumbs' });
        const colorBox = h('div', { className: 'option-list', role: 'group', 'aria-label': 'Color' });
        const tallaBox = h('div', { className: 'option-list', role: 'group', 'aria-label': 'Talla' });
        const priceBox = h('p', { className: 'dialog-price' });
        const statusBox = h('p', { className: 'dialog-status', 'aria-live': 'polite' });
        const orderBtn = h('a', { className: 'btn btn-whatsapp btn-block', target: '_blank', rel: 'noopener' },
            svg(ICONS.whatsapp), 'Pedir por WhatsApp');

        const close = () => dialog.close();
        dialog.append(h('div', { className: 'dialog-inner' },
            h('button', { type: 'button', className: 'dialog-close', 'aria-label': 'Cerrar', onclick: close }, svg(ICONS.close)),
            h('div', { className: 'dialog-gallery' }, mainImage, thumbs),
            h('div', { className: 'dialog-info' },
                h('p', { className: 'product-cat' }, p.categoria || ''),
                h('h2', { id: 'dialog-title' }, p.nombre),
                priceBox,
                colores.length ? h('div', { className: 'option-group' }, h('p', { className: 'option-label' }, 'Color'), colorBox) : null,
                p.tallas.length ? h('div', { className: 'option-group' }, h('p', { className: 'option-label' }, 'Talla'), tallaBox) : null,
                statusBox,
                orderBtn,
                h('p', { className: 'dialog-note' },
                    'Te confirmamos stock y separamos tu prenda por WhatsApp. También puedes visitarnos en la tienda.'))));

        dialog.addEventListener('click', (e) => { if (e.target === dialog) close(); }); // clic fuera
        dialog.addEventListener('close', () => {
            dialog.remove();
            const url = new URL(window.location.href);
            if (url.searchParams.has('producto')) {
                url.searchParams.delete('producto');
                history.replaceState(null, '', url);
            }
        });

        function variantesDelColor() {
            return p.variantes.filter((v) => !color || v.color === color);
        }

        function render() {
            // Colores
            colorBox.replaceChildren(...colores.map((c) => {
                const hayStock = p.variantes.some((v) => v.color === c && v.disponible);
                return h('button', {
                    type: 'button', className: 'option', 'aria-pressed': String(c === color),
                    title: hayStock ? c : `${c} (agotado)`, dataset: { agotado: hayStock ? '' : '1' },
                    onclick: () => { color = c; talla = null; render(); },
                }, c);
            }));

            // Tallas del color elegido
            const delColor = variantesDelColor();
            const tallas = [...new Set(delColor.map((v) => v.talla).filter(Boolean))];
            if (!talla || !tallas.includes(talla)) talla = (delColor.find((v) => v.disponible) || delColor[0])?.talla ?? null;
            tallaBox.replaceChildren(...tallas.map((t) => {
                const v = delColor.find((x) => x.talla === t);
                return h('button', {
                    type: 'button', className: 'option', 'aria-pressed': String(t === talla), disabled: !v?.disponible,
                    title: v?.disponible ? `Talla ${t}` : `Talla ${t} agotada`, onclick: () => { talla = t; render(); },
                }, t);
            }));

            const variante = delColor.find((v) => !talla || v.talla === talla) || delColor[0];
            priceBox.replaceChildren(h('strong', {}, money(variante?.precio ?? p.precioDesde)));
            if (!variante || !variante.disponible) {
                statusBox.className = 'dialog-status status-out';
                statusBox.textContent = 'Agotado por ahora. Escríbenos y te avisamos cuando llegue.';
            } else if (variante.ultimasUnidades) {
                statusBox.className = 'dialog-status status-low';
                statusBox.textContent = '¡Últimas unidades disponibles!';
            } else {
                statusBox.className = 'dialog-status status-ok';
                statusBox.textContent = 'Disponible en tienda';
            }
            orderBtn.href = whatsappUrl(mensajeProducto(p, variante));

            renderGallery();
        }

        function renderGallery() {
            const idsColor = new Set(variantesDelColor().map((v) => v.id));
            const delColor = p.imagenes.filter((img) => idsColor.has(img.varianteId));
            const imagenes = delColor.length ? delColor : p.imagenes;
            const show = (img) => mainImage.replaceChildren(
                productImage(img?.url, p.categoria, img?.alt || p.nombre, { width: 900, height: 900, eager: true }));
            show(imagenes[0]);
            thumbs.replaceChildren(...(imagenes.length > 1 ? imagenes : []).map((img, i) => h('button', {
                type: 'button', className: 'thumb', 'aria-label': `Ver imagen ${i + 1}`,
                onclick: () => show(img),
            }, productImage(img.url, p.categoria, '', { width: 120, height: 120 }))));
        }

        render();
        dialog.showModal();
    }

    DoGo.producto = { estado, productCard, openProductModal };
})();
