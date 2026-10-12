// Registrar venta (punto de venta): elegir variantes, armar el carrito y enviarlo a /api/ventas/completa.
// El servidor valida el stock y calcula precios y total; aquí el total es solo una estimación.
import { api } from '../api.js';
import { h, clear, money, toast, toastError, pageHeader, openModal } from '../ui.js';

export async function render(container) {
    let variantes = [];
    const cart = new Map(); // varianteId -> { variante, cantidad }
    let filter = '';

    const list = h('div', { className: 'product-list' });
    const cartBox = h('div');
    const search = h('input', {
        type: 'search', className: 'search', placeholder: 'Buscar por SKU, producto, color o talla...',
        oninput: (e) => { filter = e.target.value.trim().toLowerCase(); drawList(); },
    });

    clear(container).append(
        pageHeader('Registrar venta', search),
        h('div', { className: 'pos' },
            h('div', { className: 'card' }, h('h2', {}, 'Productos disponibles'), list),
            h('div', { className: 'card' }, h('h2', {}, 'Carrito'), cartBox)));

    await loadVariantes();
    drawCart();

    async function loadVariantes() {
        clear(list).append(h('p', { className: 'muted' }, 'Cargando...'));
        try {
            variantes = await api('/api/variantes-producto');
            drawList();
        } catch (error) {
            clear(list).append(h('p', { className: 'muted' }, error.message));
        }
    }

    function drawList() {
        const visible = variantes.filter((v) => !filter
            || `${v.sku} ${v.productoNombre} ${v.color} ${v.talla} ${v.tipoManga}`.toLowerCase().includes(filter));
        clear(list);
        if (!visible.length) list.append(h('p', { className: 'muted' }, 'Sin resultados'));
        for (const v of visible) {
            const enCarrito = cart.get(v.id)?.cantidad || 0;
            const disponible = v.stock - enCarrito;
            list.append(h('button', {
                className: 'product-item', type: 'button', disabled: disponible <= 0,
                title: disponible <= 0 ? 'Sin stock disponible' : 'Agregar al carrito',
                onclick: () => add(v),
            },
            h('div', { className: 'pi-name' }, v.productoNombre),
            h('div', { className: 'pi-meta' }, [v.sku, v.talla, v.tipoManga, v.color].filter(Boolean).join(' · ')),
            h('div', { className: 'pi-foot' },
                h('strong', {}, money(v.precioVenta)),
                h('span', { className: disponible <= 0 ? 'badge badge-danger' : 'badge' }, `Stock ${disponible}`))));
        }
    }

    function add(v) {
        const item = cart.get(v.id) || { variante: v, cantidad: 0 };
        if (item.cantidad >= v.stock) {
            toast(`No hay más stock de ${v.sku}`, 'error');
            return;
        }
        item.cantidad += 1;
        cart.set(v.id, item);
        drawCart();
        drawList();
    }

    function setCantidad(id, value) {
        const item = cart.get(id);
        const cantidad = Math.floor(Number(value));
        if (!cantidad || cantidad < 1) {
            cart.delete(id);
        } else {
            item.cantidad = Math.min(cantidad, item.variante.stock);
            if (cantidad > item.variante.stock) toast(`Stock máximo de ${item.variante.sku}: ${item.variante.stock}`, 'error');
        }
        drawCart();
        drawList();
    }

    function drawCart() {
        clear(cartBox);
        if (!cart.size) {
            cartBox.append(h('p', { className: 'muted' }, 'Toca un producto para agregarlo.'));
            return;
        }
        const rows = [...cart.values()];
        const total = rows.reduce((sum, i) => sum + (i.variante.precioVenta || 0) * i.cantidad, 0);
        const confirmButton = h('button', { className: 'btn', style: 'width:100%', onclick: () => registrar(confirmButton) },
            'Registrar venta');

        cartBox.append(
            h('div', { className: 'table-wrap' }, h('table', {},
                h('thead', {}, h('tr', {}, h('th', {}, 'Producto'), h('th', {}, 'Cant.'), h('th', { className: 'num' }, 'Subtotal'), h('th'))),
                h('tbody', {}, rows.map((i) => h('tr', {},
                    h('td', {}, h('div', {}, i.variante.productoNombre), h('div', { className: 'muted' }, i.variante.sku)),
                    h('td', {}, h('input', {
                        type: 'number', className: 'cart-qty', min: 1, max: i.variante.stock, value: String(i.cantidad),
                        'aria-label': `Cantidad de ${i.variante.sku}`,
                        onchange: (e) => setCantidad(i.variante.id, e.target.value),
                    })),
                    h('td', { className: 'num' }, money((i.variante.precioVenta || 0) * i.cantidad)),
                    h('td', { className: 'actions' }, h('button', {
                        className: 'btn btn-secondary btn-sm', 'aria-label': 'Quitar', onclick: () => setCantidad(i.variante.id, 0),
                    }, '✕'))))))),
            h('div', { className: 'cart-total' }, h('span', {}, 'Total estimado'), h('span', {}, money(total))),
            confirmButton,
            h('p', { className: 'muted', style: 'margin-top:8px' }, 'El precio final y el stock los confirma el servidor.'));
    }

    async function registrar(button) {
        button.disabled = true;
        try {
            const venta = await api('/api/ventas/completa', {
                method: 'POST',
                body: {
                    detalles: [...cart.values()].map((i) => ({ variante_producto_id: i.variante.id, cantidad: i.cantidad })),
                },
            });
            cart.clear();
            drawCart();
            await loadVariantes();
            showRecibo(venta);
        } catch (error) {
            toastError(error);
            // Puede que otra venta haya consumido stock: refrescar disponibilidad
            if (error.status === 409) await loadVariantes();
            button.disabled = false;
        }
    }
}

function showRecibo(venta) {
    toast(`Venta #${venta.id} registrada`);
    openModal(`Venta #${venta.id} registrada`, h('div', {},
        h('div', { className: 'table-wrap' }, h('table', {},
            h('thead', {}, h('tr', {}, h('th', {}, 'SKU'), h('th', { className: 'num' }, 'Cant.'),
                h('th', { className: 'num' }, 'Precio'), h('th', { className: 'num' }, 'Subtotal'))),
            h('tbody', {}, venta.detalles.map((d) => h('tr', {},
                h('td', {}, d.varianteSku),
                h('td', { className: 'num' }, d.cantidad),
                h('td', { className: 'num' }, money(d.precioUnitarioVenta)),
                h('td', { className: 'num' }, money(d.precioUnitarioVenta * d.cantidad))))))),
        h('div', { className: 'cart-total' }, h('span', {}, 'Total'), h('span', {}, money(venta.totalVenta)))));
}
