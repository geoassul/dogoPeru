// Punto de entrada del panel: comprueba la sesión, arma el menú según el rol y enruta por hash (#/seccion).
import { api, getToken, getUser, isAdmin, logout } from './api.js';
import { h, clear } from './ui.js';

/** Secciones del panel. `admin: true` = solo para ADMIN. Cada página se carga al visitarla. */
const ROUTES = [
    { group: 'General' },
    { path: 'dashboard', label: '📊 Dashboard', admin: true, load: () => import('./pages/dashboard.js') },
    { group: 'Ventas' },
    { path: 'pos', label: '🛒 Registrar venta', load: () => import('./pages/pos.js') },
    { path: 'ventas', label: '🧾 Historial de ventas', load: () => import('./pages/ventas.js') },
    { group: 'Catálogo' },
    { path: 'productos', label: '👕 Productos', load: () => import('./pages/productos.js') },
    { path: 'variantes', label: '🏷️ Variantes y stock', load: () => import('./pages/variantes.js') },
    { path: 'categorias', label: '📁 Categorías', admin: true, load: () => import('./pages/categorias.js') },
    { path: 'proveedores', label: '🚚 Proveedores', admin: true, load: () => import('./pages/proveedores.js') },
    { group: 'Administración', admin: true },
    { path: 'usuarios', label: '👤 Usuarios', admin: true, load: () => import('./pages/usuarios.js') },
];

const app = document.getElementById('app');
const sidebar = document.getElementById('sidebar');

async function start() {
    if (!getToken() || !getUser()) {
        logout();
        return;
    }
    try {
        await api('/auth/validate'); // si el token expiró, api() redirige al login
    } catch {
        return;
    }

    const user = getUser();
    document.getElementById('sessionUser').textContent = `${user.username} (${user.role})`;
    document.getElementById('logoutBtn').addEventListener('click', logout);
    document.getElementById('menuToggle').addEventListener('click', () => sidebar.classList.toggle('open'));

    buildMenu();
    window.addEventListener('hashchange', route);
    route();
}

function allowedRoutes() {
    const visible = ROUTES.filter((r) => !r.admin || isAdmin());
    // Quitar títulos de grupo que se quedaron sin secciones para este rol
    return visible.filter((r, i) => !r.group || (visible[i + 1] && !visible[i + 1].group));
}

function buildMenu() {
    clear(sidebar);
    for (const item of allowedRoutes()) {
        sidebar.append(item.group
            ? h('div', { className: 'nav-group' }, item.group)
            : h('a', { href: `#/${item.path}`, dataset: { path: item.path } }, item.label));
    }
}

async function route() {
    const routes = allowedRoutes().filter((r) => r.path);
    const path = window.location.hash.replace(/^#\/?/, '');
    const current = routes.find((r) => r.path === path);
    if (!current) {
        // Ruta desconocida o sin permiso: ir a la sección inicial del rol
        window.location.replace(`#/${isAdmin() ? 'dashboard' : 'pos'}`);
        return;
    }

    sidebar.classList.remove('open');
    sidebar.querySelectorAll('a').forEach((a) => a.classList.toggle('active', a.dataset.path === current.path));
    document.title = `DoGo Store - ${current.label.replace(/^\S+\s/, '')}`;

    clear(app).append(h('p', { className: 'muted' }, 'Cargando...'));
    try {
        const page = await current.load();
        await page.render(app);
        app.focus();
    } catch (error) {
        clear(app).append(h('div', { className: 'card' }, `No se pudo cargar la sección: ${error.message}`));
    }
}

start();
