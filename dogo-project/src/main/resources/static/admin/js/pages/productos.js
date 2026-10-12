import { api } from '../api.js';
import { crudPage } from '../crud.js';

export const render = crudPage({
    title: 'Productos',
    entityName: 'producto',
    article: 'el',
    endpoint: '/api/productos',
    columns: [
        { label: 'ID', value: (p) => p.id },
        { label: 'Nombre', value: (p) => p.nombre },
        { label: 'Categoría', value: (p) => p.categoriaNombre || '—' },
        { label: 'Proveedor', value: (p) => p.proveedorNombre || '—' },
    ],
    fields: [
        { name: 'nombre', label: 'Nombre', required: true, maxlength: 100 },
        {
            name: 'categoriaId', label: 'Categoría', type: 'select', required: true, numeric: true,
            options: async () => (await api('/api/categorias')).map((c) => ({ value: c.id, label: c.nombre })),
        },
        {
            name: 'proveedorId', label: 'Proveedor', type: 'select', required: true, numeric: true,
            options: async () => (await api('/api/proveedores')).map((p) => ({ value: p.id, label: p.nombre })),
        },
    ],
    searchText: (p) => `${p.nombre} ${p.categoriaNombre} ${p.proveedorNombre}`,
    describe: (p) => p.nombre,
});
