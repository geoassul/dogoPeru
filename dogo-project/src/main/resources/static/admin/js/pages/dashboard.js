import { api } from '../api.js';
import { h, clear, money, dateTime, table, pageHeader } from '../ui.js';

const UMBRAL_STOCK = 5;

export async function render(container) {
    const body = h('div', {}, h('p', { className: 'muted' }, 'Cargando...'));
    clear(container).append(
        pageHeader('Dashboard', h('button', { className: 'btn btn-secondary', onclick: load }, '↻ Actualizar')),
        body);
    await load();

    async function load() {
        try {
            const d = await api(`/api/dashboard?umbralStock=${UMBRAL_STOCK}`);
            clear(body).append(
                h('div', { className: 'kpis' },
                    kpi('Ventas de hoy', money(d.hoy.monto), `${d.hoy.cantidad} ventas`),
                    kpi('Ventas del mes', money(d.mes.monto), `${d.mes.cantidad} ventas`),
                    kpi('Ganancia del mes', money(d.gananciaMes), 'precio − costo'),
                    kpi('Ventas históricas', money(d.historico.monto), `${d.historico.cantidad} ventas`),
                    kpi('Ganancia histórica', money(d.gananciaHistorica), 'precio − costo')),
                h('div', { className: 'grid-2' },
                    h('div', { className: 'card' },
                        h('h2', {}, `Stock bajo (≤ ${d.umbralStockBajo})`),
                        table([
                            { label: 'SKU', value: (v) => v.sku },
                            { label: 'Producto', value: (v) => v.productoNombre },
                            {
                                label: 'Stock', className: 'num',
                                value: (v) => h('span', { className: `badge ${v.stock <= 0 ? 'badge-danger' : 'badge-warning'}` }, v.stock),
                            },
                        ], d.stockBajo, 'Todo el stock está por encima del umbral')),
                    h('div', { className: 'card' },
                        h('h2', {}, 'Más vendidas'),
                        table([
                            { label: 'SKU', value: (t) => t.sku },
                            { label: 'Producto', value: (t) => t.productoNombre },
                            { label: 'Unidades', className: 'num', value: (t) => t.unidades },
                        ], d.masVendidas, 'Aún no hay ventas'))),
                h('div', { className: 'card' },
                    h('h2', {}, 'Ventas recientes'),
                    table([
                        { label: 'Venta', value: (v) => `#${v.id}` },
                        { label: 'Fecha', value: (v) => dateTime(v.fechaVenta) },
                        { label: 'Productos', value: (v) => v.detalles.map((x) => `${x.varianteSku} ×${x.cantidad}`).join(', ') || '—' },
                        { label: 'Total', className: 'num', value: (v) => money(v.totalVenta) },
                    ], d.ventasRecientes, 'Aún no hay ventas'),
                    h('p', { style: 'margin-top:10px' }, h('a', { href: '#/ventas' }, 'Ver historial completo →'))));
        } catch (error) {
            clear(body).append(h('div', { className: 'card' }, error.message));
        }
    }
}

function kpi(label, value, sub) {
    return h('div', { className: 'kpi' },
        h('div', { className: 'kpi-label' }, label),
        h('div', { className: 'kpi-value' }, value),
        h('div', { className: 'kpi-sub' }, sub));
}
