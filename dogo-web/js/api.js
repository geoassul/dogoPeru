// Acceso al catálogo público del backend (GET /api/public/...). No requiere login.
window.DoGo = window.DoGo || {};

(function () {
    'use strict';

    const { CONFIG } = DoGo;

    const TIMEOUT_MS = 8000;
    const CACHE_MS = 60_000; // misma vigencia que el Cache-Control del backend

    /** Error al consultar la API (servidor apagado, sin conexión o respuesta inválida). */
    class ApiError extends Error {}

    async function get(path) {
        const cacheKey = `dogo:${path}`;
        const cached = readCache(cacheKey);
        if (cached) return cached;

        const controller = new AbortController();
        const timer = setTimeout(() => controller.abort(), TIMEOUT_MS);
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${path}`, { signal: controller.signal });
            if (!response.ok) throw new ApiError(`El servidor respondió ${response.status}`);
            const json = await response.json();
            if (!json.success) throw new ApiError(json.message || 'Respuesta inválida del servidor');
            writeCache(cacheKey, json.data);
            return json.data;
        } catch (error) {
            if (error instanceof ApiError) throw error;
            throw new ApiError(error.name === 'AbortError'
                ? 'El servidor tardó demasiado en responder'
                : 'No se pudo conectar con el servidor');
        } finally {
            clearTimeout(timer);
        }
    }

    /** Productos con variantes e imágenes, ordenados por más vendidos. */
    const getCatalogo = () => get('/api/public/catalogo');

    /** Categorías que tienen productos. */
    const getCategorias = () => get('/api/public/categorias');

    // Caché corta en sessionStorage: navegar entre páginas no repite la descarga del catálogo
    function readCache(key) {
        try {
            const item = JSON.parse(sessionStorage.getItem(key) || 'null');
            return item && Date.now() - item.t < CACHE_MS ? item.data : null;
        } catch {
            return null;
        }
    }

    function writeCache(key, data) {
        try {
            sessionStorage.setItem(key, JSON.stringify({ t: Date.now(), data }));
        } catch {
            // almacenamiento lleno o bloqueado: se ignora, la caché es opcional
        }
    }

    DoGo.api = { ApiError, getCatalogo, getCategorias };
})();
