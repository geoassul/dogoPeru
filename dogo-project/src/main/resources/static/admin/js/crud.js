// Página CRUD genérica: tabla con búsqueda + formulario modal para crear/editar + eliminar.
import { api, isAdmin } from './api.js';
import { h, clear, table, openModal, confirmDialog, toast, toastError, loading, pageHeader } from './ui.js';

/**
 * config:
 *  - title, entityName, article     textos ("Categorías", "categoría", "la")
 *  - endpoint                       p. ej. '/api/categorias'
 *  - columns                        [{ label, value: (row) => texto|nodo, className }]
 *  - fields                         [{ name, label, type: text|number|password|select, required, numeric,
 *                                      options: async () => [{ value, label }], min, step, maxlength,
 *                                      placeholder, help, editHelp, requiredOnCreate }]
 *  - searchText(row)                texto en el que busca el filtro
 *  - load()                         opcional, por defecto GET endpoint
 *  - toBody(values, row)            opcional, arma el JSON a enviar
 *  - rowActions(row, reload)        opcional, botones extra por fila
 *  - toolbar(reload)                opcional, controles extra en la cabecera (filtros)
 *  - canWrite                       por defecto: solo ADMIN
 *  - describe(row)                  texto para el mensaje de eliminar
 */
export function crudPage(config) {
    const canWrite = config.canWrite ?? isAdmin();
    const load = config.load || (() => api(config.endpoint));

    return async function render(container) {
        let rows = [];
        let filter = '';

        const tableBox = h('div');
        const search = h('input', {
            type: 'search', className: 'search', placeholder: 'Buscar...',
            oninput: (e) => { filter = e.target.value.trim().toLowerCase(); drawTable(); },
        });
        const newButton = canWrite
            ? h('button', { className: 'btn', onclick: () => openForm(null) }, `+ Nuevo`)
            : null;

        clear(container).append(
            pageHeader(config.title, config.toolbar ? config.toolbar(reload) : null, search, newButton),
            h('div', { className: 'card' }, tableBox));

        async function reload() {
            loading(tableBox);
            try {
                rows = await load();
                drawTable();
            } catch (error) {
                clear(tableBox).append(h('p', { className: 'muted' }, error.message));
            }
        }

        function drawTable() {
            const visible = filter
                ? rows.filter((row) => (config.searchText ? config.searchText(row) : JSON.stringify(row))
                    .toLowerCase().includes(filter))
                : rows;
            const columns = [...config.columns];
            if (canWrite || config.rowActions) {
                columns.push({
                    label: '', className: 'actions', value: (row) => [
                        config.rowActions ? config.rowActions(row, reload) : null,
                        canWrite ? h('button', { className: 'btn btn-secondary btn-sm', onclick: () => openForm(row) }, 'Editar') : null,
                        canWrite ? h('button', { className: 'btn btn-danger btn-sm', onclick: () => remove(row) }, 'Eliminar') : null,
                    ],
                });
            }
            clear(tableBox).append(
                table(columns, visible, filter ? 'Sin resultados para la búsqueda' : 'Sin registros'),
                h('p', { className: 'muted', style: 'margin-top:8px' }, `${visible.length} de ${rows.length} registros`));
        }

        async function remove(row) {
            const name = config.describe ? config.describe(row) : `#${row.id}`;
            if (!await confirmDialog(`¿Eliminar ${config.entityName} "${name}"? Esta acción no se puede deshacer.`)) return;
            try {
                await api(`${config.endpoint}/${row.id}`, { method: 'DELETE' });
                toast(`Se eliminó ${config.article} ${config.entityName}`);
                reload();
            } catch (error) {
                toastError(error);
            }
        }

        async function openForm(row) {
            const isEdit = row !== null;
            const inputs = {};
            const errors = {};
            const grid = h('div', { className: 'form-grid' });

            for (const field of config.fields) {
                const input = await buildInput(field, row);
                inputs[field.name] = input;
                errors[field.name] = h('span', { className: 'error' });
                const help = isEdit && field.editHelp ? field.editHelp : field.help;
                grid.append(h('div', { className: 'field', dataset: { field: field.name } },
                    h('label', { for: `f-${field.name}` }, field.label + (isRequired(field, isEdit) ? ' *' : '')),
                    input,
                    help ? h('span', { className: 'help' }, help) : null,
                    errors[field.name]));
            }

            // Botón submit oculto para que Enter envíe el formulario; el visible está en el pie del modal
            const form = h('form', { onsubmit: submit, novalidate: true }, grid, h('button', { type: 'submit', hidden: true }));
            const modal = openModal(
                `${isEdit ? 'Editar' : (config.article === 'la' ? 'Nueva' : 'Nuevo')} ${config.entityName}`,
                form,
                [
                    h('button', { className: 'btn btn-secondary', type: 'button', onclick: () => modal.close() }, 'Cancelar'),
                    h('button', { className: 'btn', type: 'button', onclick: () => form.requestSubmit() },
                        isEdit ? 'Guardar cambios' : 'Crear'),
                ]);

            async function submit(e) {
                e.preventDefault();
                Object.values(errors).forEach((el) => { el.textContent = ''; el.parentElement.classList.remove('invalid'); });

                const values = {};
                let valid = true;
                for (const field of config.fields) {
                    const raw = inputs[field.name].value.trim();
                    if (!raw && isRequired(field, isEdit)) {
                        showError(field.name, 'Campo obligatorio');
                        valid = false;
                    }
                    values[field.name] = toValue(field, raw);
                }
                if (!valid) return;

                const body = config.toBody ? config.toBody(values, row) : values;
                try {
                    await api(isEdit ? `${config.endpoint}/${row.id}` : config.endpoint,
                        { method: isEdit ? 'PUT' : 'POST', body });
                    modal.close();
                    toast(isEdit ? 'Cambios guardados' : `Se creó ${config.article} ${config.entityName}`);
                    reload();
                } catch (error) {
                    // Errores de validación por campo devueltos por el backend
                    if (error.data && typeof error.data === 'object') {
                        for (const [name, message] of Object.entries(error.data)) {
                            const key = name.replace(/\[.*$/, '');
                            if (errors[key]) showError(key, message);
                        }
                    }
                    toastError(error);
                }
            }

            function showError(name, message) {
                errors[name].textContent = message;
                errors[name].parentElement.classList.add('invalid');
            }
        }

        reload();
    };
}

async function buildInput(field, row) {
    const current = row ? row[field.name] : undefined;
    const common = { id: `f-${field.name}`, name: field.name };

    if (field.type === 'select') {
        const select = h('select', common, h('option', { value: '' }, 'Seleccione...'));
        let options = [];
        try {
            options = await field.options();
        } catch (error) {
            toastError(error);
        }
        for (const option of options) {
            select.append(h('option', { value: String(option.value) }, option.label));
        }
        if (current !== undefined && current !== null) select.value = String(current);
        return select;
    }

    const input = h('input', {
        ...common,
        type: field.type || 'text',
        min: field.min,
        step: field.step,
        maxlength: field.maxlength,
        placeholder: field.placeholder,
        autocomplete: field.type === 'password' ? 'new-password' : 'off',
    });
    // Las contraseñas nunca se precargan
    if (field.type !== 'password' && current !== undefined && current !== null) input.value = String(current);
    return input;
}

function isRequired(field, isEdit) {
    return field.required || (field.requiredOnCreate && !isEdit);
}

function toValue(field, raw) {
    if (raw === '') return null;
    if (field.type === 'number' || field.numeric) return Number(raw);
    return raw;
}
