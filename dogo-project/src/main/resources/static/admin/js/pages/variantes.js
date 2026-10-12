import { api, isAdmin } from '../api.js';
import { crudPage } from '../crud.js';
import { h, clear, money, openModal, confirmDialog, toast, toastError } from '../ui.js';

const STOCK_BAJO = 5;
let productoFiltro = '';

export const render = crudPage({
    title: 'Variantes y stock',
    entityName: 'variante',
    article: 'la',
    endpoint: '/api/variantes-producto',
    load: () => api(productoFiltro
        ? `/api/variantes-producto/producto/${productoFiltro}`
        : '/api/variantes-producto'),
    toolbar: (reload) => {
        const select = h('select', {
            'aria-label': 'Filtrar por producto',
            onchange: (e) => { productoFiltro = e.target.value; reload(); },
        }, h('option', { value: '' }, 'Todos los productos'));
        api('/api/productos').then((productos) => {
            for (const p of productos) select.append(h('option', { value: String(p.id) }, p.nombre));
            select.value = productoFiltro;
        }).catch(toastError);
        return select;
    },
    columns: [
        { label: 'SKU', className: 'nowrap', value: (v) => v.sku },
        { label: 'Producto', value: (v) => v.productoNombre },
        { label: 'Talla', value: (v) => v.talla || '—' },
        { label: 'Manga', value: (v) => v.tipoManga || '—' },
        { label: 'Color', value: (v) => v.color || '—' },
        { label: 'Costo', className: 'num', value: (v) => money(v.costoAdquisicion) },
        { label: 'Precio', className: 'num', value: (v) => money(v.precioVenta) },
        { label: 'Stock', className: 'num', value: stockBadge },
    ],
    fields: [
        {
            name: 'productoId', label: 'Producto', type: 'select', required: true, numeric: true,
            options: async () => (await api('/api/productos')).map((p) => ({ value: p.id, label: p.nombre })),
        },
        { name: 'sku', label: 'SKU', required: true, maxlength: 100, help: 'Código único, p. ej. POLO-BAS-AZ-M-C' },
        { name: 'talla', label: 'Talla', maxlength: 20 },
        { name: 'tipoManga', label: 'Tipo de manga', maxlength: 20 },
        { name: 'color', label: 'Color', maxlength: 20 },
        { name: 'costoAdquisicion', label: 'Costo de adquisición (S/)', type: 'number', min: 0, step: '0.01' },
        { name: 'precioVenta', label: 'Precio de venta (S/)', type: 'number', min: 0, step: '0.01', required: true },
        { name: 'stock', label: 'Stock', type: 'number', min: 0, step: 1, required: true },
    ],
    searchText: (v) => `${v.sku} ${v.productoNombre} ${v.talla} ${v.tipoManga} ${v.color}`,
    describe: (v) => v.sku,
    rowActions: (v) => h('button', { className: 'btn btn-secondary btn-sm', onclick: () => openImagenes(v) }, 'Imágenes'),
});

function stockBadge(v) {
    const css = v.stock <= 0 ? 'badge-danger' : v.stock <= STOCK_BAJO ? 'badge-warning' : 'badge-success';
    return h('span', { className: `badge ${css}` }, v.stock);
}

// ---------- Galería de imágenes de una variante ----------

function openImagenes(variante) {
    const gallery = h('div', { className: 'gallery' });
    const content = h('div', {}, gallery, isAdmin() ? uploadForm() : null);
    openModal(`Imágenes de ${variante.sku}`, content);
    loadGallery();

    async function loadGallery() {
        clear(gallery).append(h('p', { className: 'muted' }, 'Cargando...'));
        try {
            const imagenes = await api(`/api/imagenes-variante/variante/${variante.id}`);
            clear(gallery);
            if (!imagenes.length) gallery.append(h('p', { className: 'muted' }, 'Esta variante no tiene imágenes.'));
            for (const img of imagenes) {
                gallery.append(h('figure', {},
                    h('img', { src: img.urlImagen, alt: img.altText || variante.sku, loading: 'lazy' }),
                    h('figcaption', {},
                        h('span', {}, `${img.tipoImagen || 'imagen'} · ${img.orden ?? 0}`),
                        isAdmin() ? h('button', { className: 'btn btn-danger btn-sm', onclick: () => remove(img) }, '✕') : null)));
            }
        } catch (error) {
            clear(gallery).append(h('p', { className: 'muted' }, error.message));
        }
    }

    async function remove(img) {
        if (!await confirmDialog('¿Eliminar esta imagen?')) return;
        try {
            await api(`/api/imagenes-variante/${img.id}`, { method: 'DELETE' });
            toast('Imagen eliminada');
            loadGallery();
        } catch (error) {
            toastError(error);
        }
    }

    function uploadForm() {
        const file = h('input', { type: 'file', accept: 'image/jpeg,image/png,image/gif,image/webp', required: true });
        const tipo = h('select', {},
            ['principal', 'galeria', 'detalle'].map((t) => h('option', { value: t }, t)));
        const orden = h('input', { type: 'number', min: 0, max: 255, value: '0' });
        const button = h('button', { className: 'btn', type: 'submit' }, 'Subir imagen');

        return h('form', {
            className: 'card',
            onsubmit: async (e) => {
                e.preventDefault();
                if (!file.files.length) {
                    toast('Selecciona un archivo', 'error');
                    return;
                }
                const data = new FormData();
                data.append('file', file.files[0]);
                data.append('varianteId', variante.id);
                data.append('tipoImagen', tipo.value);
                data.append('orden', orden.value || '0');
                button.disabled = true;
                try {
                    await api('/api/imagenes-variante/upload', { method: 'POST', formData: data });
                    toast('Imagen subida');
                    file.value = '';
                    loadGallery();
                } catch (error) {
                    toastError(error);
                } finally {
                    button.disabled = false;
                }
            },
        },
        h('h2', {}, 'Subir nueva imagen'),
        h('div', { className: 'form-grid' },
            h('div', { className: 'field' }, h('label', {}, 'Archivo (JPG, PNG, GIF, WEBP · máx. 10 MB)'), file),
            h('div', { className: 'field' }, h('label', {}, 'Tipo'), tipo),
            h('div', { className: 'field' }, h('label', {}, 'Orden'), orden)),
        h('div', { style: 'margin-top:12px' }, button));
    }
}
