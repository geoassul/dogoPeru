import { getUser } from '../api.js';
import { crudPage } from '../crud.js';
import { h } from '../ui.js';

const ROLES = [
    { value: 'ADMIN', label: 'ADMIN — acceso total' },
    { value: 'USER', label: 'USER — vendedor (ventas y consulta de catálogo)' },
];

export const render = crudPage({
    title: 'Usuarios',
    entityName: 'usuario',
    article: 'el',
    endpoint: '/api/usuarios',
    columns: [
        { label: 'ID', value: (u) => u.id },
        {
            label: 'Usuario', value: (u) => [u.username,
                u.id === getUser()?.id ? h('span', { className: 'badge', style: 'margin-left:6px' }, 'tú') : null],
        },
        {
            label: 'Rol', value: (u) => h('span', { className: `badge ${u.role === 'ADMIN' ? '' : 'badge-success'}` }, u.role),
        },
    ],
    fields: [
        { name: 'username', label: 'Usuario', required: true, maxlength: 25, help: 'Letras, números, punto, guion y guion bajo' },
        { name: 'role', label: 'Rol', type: 'select', required: true, options: async () => ROLES },
        {
            name: 'password', label: 'Contraseña', type: 'password', requiredOnCreate: true, maxlength: 72,
            help: 'Mínimo 6 caracteres', editHelp: 'Déjala vacía para no cambiarla (mínimo 6 caracteres)',
        },
    ],
    searchText: (u) => `${u.username} ${u.role}`,
    describe: (u) => u.username,
});
