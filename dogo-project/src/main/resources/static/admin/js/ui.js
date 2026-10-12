// Utilidades de interfaz. Todo el texto se inserta con textContent (nunca innerHTML con datos),
// así los nombres de productos u otros datos no se interpretan como HTML (evita XSS).

/**
 * Crea un elemento: h('button', { className: 'btn', onclick: fn }, 'Texto', otroNodo)
 * Los hijos de tipo string/number se agregan como texto.
 */
export function h(tag, attrs = {}, ...children) {
    const el = document.createElement(tag);
    for (const [key, value] of Object.entries(attrs || {})) {
        if (value === undefined || value === null || value === false) continue;
        if (key.startsWith('on') && typeof value === 'function') {
            el.addEventListener(key.slice(2).toLowerCase(), value);
        } else if (key === 'className') {
            el.className = value;
        } else if (key === 'dataset') {
            Object.assign(el.dataset, value);
        } else if (key in el && typeof value !== 'string') {
            el[key] = value;
        } else {
            el.setAttribute(key, value === true ? '' : value);
        }
    }
    append(el, children);
    return el;
}

function append(el, children) {
    for (const child of children.flat(Infinity)) {
        if (child === undefined || child === null || child === false) continue;
        el.append(child instanceof Node ? child : document.createTextNode(String(child)));
    }
}

export function clear(el) {
    el.replaceChildren();
    return el;
}

// ---------- Formato ----------

const moneyFormat = new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' });

export function money(value) {
    return value === null || value === undefined ? '—' : moneyFormat.format(value);
}

export function dateTime(value) {
    if (!value) return '—';
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? value : date.toLocaleString('es-PE', { dateStyle: 'short', timeStyle: 'short' });
}

// ---------- Toasts ----------

export function toast(message, type = 'success') {
    const box = document.getElementById('toasts');
    const item = h('div', { className: `toast toast-${type}`, role: 'status' }, message);
    box.append(item);
    setTimeout(() => item.remove(), type === 'error' ? 6000 : 3500);
}

export function toastError(error) {
    toast(error?.message || 'Ocurrió un error', 'error');
}

// ---------- Modal ----------

/**
 * Abre un modal. `content` es un nodo; `footer` una lista de botones.
 * Devuelve { close, body }.
 */
export function openModal(title, content, footer = []) {
    const backdrop = h('div', { className: 'modal-backdrop' });
    const close = () => {
        backdrop.remove();
        document.removeEventListener('keydown', onKey);
    };
    const onKey = (e) => { if (e.key === 'Escape') close(); };
    const body = h('div', { className: 'modal-body' }, content);
    const modal = h('div', { className: 'modal', role: 'dialog', 'aria-modal': 'true', 'aria-label': title },
        h('div', { className: 'modal-header' },
            h('h2', {}, title),
            h('button', { className: 'modal-close', 'aria-label': 'Cerrar', onclick: close }, '×')),
        body,
        footer.length ? h('div', { className: 'modal-footer' }, footer) : null);
    backdrop.append(modal);
    backdrop.addEventListener('mousedown', (e) => { if (e.target === backdrop) close(); });
    document.addEventListener('keydown', onKey);
    document.body.append(backdrop);
    const firstInput = modal.querySelector('input, select, textarea');
    (firstInput || modal.querySelector('button')).focus();
    return { close, body };
}

/** Confirmación simple. Devuelve una promesa que resuelve true/false. */
export function confirmDialog(message, { confirmText = 'Eliminar', danger = true } = {}) {
    return new Promise((resolve) => {
        let modal;
        const done = (value) => { modal.close(); resolve(value); };
        modal = openModal('Confirmar', h('p', {}, message), [
            h('button', { className: 'btn btn-secondary', onclick: () => done(false) }, 'Cancelar'),
            h('button', { className: `btn ${danger ? 'btn-danger' : ''}`, onclick: () => done(true) }, confirmText),
        ]);
    });
}

// ---------- Tablas ----------

/**
 * Tabla simple. columns: [{ label, value: (row) => texto|nodo, className }]
 */
export function table(columns, rows, emptyText = 'Sin registros') {
    const thead = h('thead', {}, h('tr', {}, columns.map((c) => h('th', { className: c.className || '' }, c.label))));
    const tbody = h('tbody');
    if (!rows.length) {
        tbody.append(h('tr', {}, h('td', { className: 'empty', colSpan: columns.length }, emptyText)));
    }
    for (const row of rows) {
        tbody.append(h('tr', {}, columns.map((c) => h('td', { className: c.className || '' }, c.value(row)))));
    }
    return h('div', { className: 'table-wrap' }, h('table', {}, thead, tbody));
}

export function loading(container) {
    clear(container).append(h('p', { className: 'muted' }, 'Cargando...'));
}

export function pageHeader(title, ...actions) {
    return h('div', { className: 'page-header' }, h('h1', {}, title), h('div', { className: 'toolbar' }, actions));
}
