import { crudPage } from '../crud.js';

export const render = crudPage({
    title: 'Categorías',
    entityName: 'categoría',
    article: 'la',
    endpoint: '/api/categorias',
    columns: [
        { label: 'ID', value: (c) => c.id },
        { label: 'Nombre', value: (c) => c.nombre },
    ],
    fields: [
        { name: 'nombre', label: 'Nombre', required: true, maxlength: 30 },
    ],
    searchText: (c) => c.nombre,
    describe: (c) => c.nombre,
});
