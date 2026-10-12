// Cliente de la API: añade el token JWT, desempaqueta ApiResponse y gestiona la sesión.
// Usa las mismas claves de localStorage que login.html y js/auth.js.

const TOKEN_KEY = 'dogoToken';
const USER_KEY = 'dogoUser';
const LOGIN_TIME_KEY = 'dogoLoginTime';
const LOGIN_URL = '/login.html';

/** Error de la API con el status HTTP, el mensaje del backend y sus datos (p. ej. errores por campo). */
export class ApiError extends Error {
    constructor(status, message, data) {
        super(message);
        this.status = status;
        this.data = data;
    }
}

export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

export function getUser() {
    try {
        return JSON.parse(localStorage.getItem(USER_KEY) || 'null');
    } catch {
        return null;
    }
}

export function isAdmin() {
    return getUser()?.role === 'ADMIN';
}

export function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(LOGIN_TIME_KEY);
    window.location.replace(LOGIN_URL);
}

/**
 * Llama a la API y devuelve `data` de la respuesta.
 * - 401: la sesión expiró -> vuelve al login.
 * - Otros errores: lanza ApiError con el mensaje del backend.
 */
export async function api(path, { method = 'GET', body, formData } = {}) {
    const headers = { 'Authorization': `Bearer ${getToken()}` };
    let payload;
    if (formData) {
        payload = formData; // el navegador pone el Content-Type multipart con su boundary
    } else if (body !== undefined) {
        headers['Content-Type'] = 'application/json';
        payload = JSON.stringify(body);
    }

    let response;
    try {
        response = await fetch(path, { method, headers, body: payload });
    } catch {
        throw new ApiError(0, 'No se puede conectar con el servidor');
    }

    if (response.status === 401) {
        logout();
        throw new ApiError(401, 'Sesión expirada');
    }

    let json = null;
    const text = await response.text();
    if (text) {
        try {
            json = JSON.parse(text);
        } catch {
            json = null;
        }
    }

    if (!response.ok || (json && json.success === false)) {
        const message = json?.message
            || (response.status === 403 ? 'No tienes permiso para esta acción' : `Error ${response.status}`);
        throw new ApiError(response.status, message, json?.data);
    }

    // /api/ventas/recent devuelve la lista sin envolver; el resto usa ApiResponse
    return json && Object.prototype.hasOwnProperty.call(json, 'success') ? json.data : json;
}
