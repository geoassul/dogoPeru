// Historial de ventas con filtro por fechas y detalle desplegable.
import { api, isAdmin } from '../api.js';
import { h, clear, money, dateTime, confirmDialog, toast, toastError, pageHeader } from '../ui.js';

export async function render(container) {
    const desde = h('input', { type: 'date', 'aria-label': 'Desde' });
    const hasta = h('input', { type: 'date', 'aria-label': 'Hasta' });
    const resumen = h('p', { className: 'muted', style: 'margin-top:8px' });
    const tableBox = h('div');
    const abiertas = new Set();
    let ventas = [];

    clear(container).append(
        pageHeader('Historial de ventas',
            h('label', { className: 'muted' }, 'Desde'), desde,
            h('label', { className: 'muted' }, 'Hasta'), hasta,
            h('button', { className: 'btn', onclick: load }, 'Filtrar'),
            h('button', { className: 'btn btn-secondary', onclick: () => { desde.value = ''; hasta.value = ''; load(); } }, 'Todas')),
        h('div', { className: 'card' }, tableBox, resumen));

    await load();

    async function load() {
        clear(tableBox).append(h('p', { className: 'muted' }, 'Cargando...'));
        try {
            if (desde.value || hasta.value) {
                const inicio = `${desde.value || '2000-01-01'}T00:00:00`;
                const fin = `${hasta.value || '2100-12-31'}T23:59:59`;
                ventas = await api(`/api/ventas/fecha?fechaInicio=${inicio}&fechaFin=${fin}`);
            } else {
                ventas = await api('/api/ventas');
            }
            ventas.sort((a, b) => (b.fechaVenta || '').localeCompare(a.fechaVenta || '') || b.id - a.id);
            draw();
        } catch (error) {
            clear(tableBox).append(h('p', { className: 'muted' }, error.message));
        }
    }

    function draw() {
        const tbody = h('tbody');
        if (!ventas.length) {
            tbody.append(h('tr', {}, h('td', { className: 'empty', colSpan: 6 }, 'No hay ventas en el periodo')));
        }
        for (const v of ventas) {
            const unidades = v.detalles.reduce((s, d) => s + d.cantidad, 0);
            const abierta = abiertas.has(v.id);
            tbody.append(h('tr', {},
                h('td', {}, `#${v.id}`),
                h('td', {}, dateTime(v.fechaVenta)),
                h('td', { className: 'num' }, v.detalles.length),
                h('td', { className: 'num' }, unidades),
                h('td', { className: 'num' }, h('strong', {}, money(v.totalVenta))),
                h('td', { className: 'actions' },
                    h('button', {
                        className: 'btn btn-secondary btn-sm', disabled: !v.detalles.length,
                        onclick: () => { abierta ? abiertas.delete(v.id) : abiertas.add(v.id); draw(); },
                    }, abierta ? 'Ocultar' : 'Detalle'),
                    isAdmin() ? h('button', { className: 'btn btn-danger btn-sm', onclick: () => remove(v) }, 'Eliminar') : null)));
            if (abierta) {
                tbody.append(h('tr', { className: 'detail-row' }, h('td', { colSpan: 6 },
                    h('table', {},
                        h('thead', {}, h('tr', {}, h('th', {}, 'SKU'), h('th', { className: 'num' }, 'Cantidad'),
                            h('th', { className: 'num' }, 'Precio unit.'), h('th', { className: 'num' }, 'Subtotal'))),
                        h('tbody', {}, v.detalles.map((d) => h('tr', {},
                            h('td', {}, d.varianteSku),
                            h('td', { className: 'num' }, d.cantidad),
                            h('td', { className: 'num' }, money(d.precioUnitarioVenta)),
                            h('td', { className: 'num' }, money((d.precioUnitarioVenta || 0) * d.cantidad)))))))));
            }
        }
        clear(tableBox).append(h('div', { className: 'table-wrap' }, h('table', {},
            h('thead', {}, h('tr', {},
                h('th', {}, 'Venta'), h('th', {}, 'Fecha'), h('th', { className: 'num' }, 'Líneas'),
                h('th', { className: 'num' }, 'Unidades'), h('th', { className: 'num' }, 'Total'), h('th'))),
            tbody)));
        const total = ventas.reduce((s, v) => s + (v.totalVenta || 0), 0);
        resumen.textContent = `${ventas.length} ventas · Total ${money(total)}`;
    }

    async function remove(v) {
        if (!await confirmDialog(`¿Eliminar la venta #${v.id}? Se borran también sus detalles. El stock no se repone.`)) return;
        try {
            await api(`/api/ventas/${v.id}`, { method: 'DELETE' });
            toast(`Venta #${v.id} eliminada`);
            load();
        } catch (error) {
            toastError(error);
        }
    }
}
