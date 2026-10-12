import { crudPage } from '../crud.js';

export const render = crudPage({
    title: 'Proveedores',
    entityName: 'proveedor',
    article: 'el',
    endpoint: '/api/proveedores',
    columns: [
        { label: 'ID', value: (p) => p.id },
        { label: 'Nombre', value: (p) => p.nombre },
        { label: 'Teléfono', value: (p) => p.telefono || '—' },
    ],
    fields: [
        { name: 'nombre', label: 'Nombre', required: true, maxlength: 30 },
        { name: 'telefono', label: 'Teléfono', required: true, maxlength: 10, help: 'De 7 a 10 caracteres' },
    ],
    searchText: (p) => `${p.nombre} ${p.telefono}`,
    describe: (p) => p.nombre,
});
