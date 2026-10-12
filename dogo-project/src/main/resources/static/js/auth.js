/**
 * DoGo Authentication Handler
 * Maneja la autenticación y persistencia de sesión en toda la aplicación
 */

class DoGoAuth {
    constructor() {
        // Mismo origen que la página: funciona en localhost y desde el celular por la IP de la red local
        this.API_BASE_URL = window.location.origin;
        this.TOKEN_KEY = 'dogoToken';
        this.USER_KEY = 'dogoUser';
        this.LOGIN_TIME_KEY = 'dogoLoginTime';
        this.currentToken = null;
        this.currentUser = null;
        
        // Inicializar al cargar
        this.init();
    }

    /**
     * Inicializar el sistema de autenticación
     */
    init() {
        this.loadStoredSession();
        this.setupTokenValidation();
    }

    /**
     * Cargar sesión almacenada en localStorage
     */
    loadStoredSession() {
        const token = localStorage.getItem(this.TOKEN_KEY);
        const userStr = localStorage.getItem(this.USER_KEY);
        
        if (token && userStr) {
            try {
                this.currentToken = token;
                this.currentUser = JSON.parse(userStr);
                
                // Validar que el token sigue siendo válido
                this.validateStoredToken();
            } catch (error) {
                console.error('Error loading stored session:', error);
                this.clearSession();
            }
        }
    }

    /**
     * Validar token almacenado
     */
    async validateStoredToken() {
        if (!this.currentToken) return false;

        try {
            // /auth/validate sirve para cualquier rol (antes se usaba /api/categorias, que es solo ADMIN,
            // y a los usuarios USER se les cerraba la sesión al recibir 403)
            const response = await fetch(`${this.API_BASE_URL}/auth/validate`, {
                headers: {
                    'Authorization': `Bearer ${this.currentToken}`
                }
            });

            if (response.ok) {
                console.log('✅ Sesión válida restaurada');
                return true;
            }
            if (response.status === 401) {
                console.log('⚠️ Token expirado, limpiando sesión');
                this.clearSession();
            }
            return false;
        } catch (error) {
            // Error de red: no se cierra la sesión, el token puede seguir siendo válido
            console.error('Error validating token:', error);
            return false;
        }
    }

    /**
     * Realizar login
     */
    async login(username, password) {
        try {
            const response = await fetch(`${this.API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (data.success) {
                // Guardar sesión
                this.currentToken = data.data.token;
                this.currentUser = data.data.user;
                
                localStorage.setItem(this.TOKEN_KEY, data.data.token);
                localStorage.setItem(this.USER_KEY, JSON.stringify(data.data.user));
                localStorage.setItem(this.LOGIN_TIME_KEY, new Date().toISOString());
                
                console.log('✅ Login exitoso, sesión guardada');
                return { success: true, data: data.data };
            } else {
                return { success: false, message: data.message || 'Error en el login' };
            }
        } catch (error) {
            console.error('Login error:', error);
            return { success: false, message: 'Error de conexión' };
        }
    }

    /**
     * Cerrar sesión
     */
    logout() {
        this.clearSession();
        console.log('✅ Sesión cerrada');
        
        // Opcional: redirigir al login
        if (window.location.pathname !== '/login.html') {
            window.location.href = '/login.html';
        }
    }

    /**
     * Limpiar sesión
     */
    clearSession() {
        this.currentToken = null;
        this.currentUser = null;
        
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        localStorage.removeItem(this.LOGIN_TIME_KEY);
    }

    /**
     * Verificar si el usuario está autenticado
     */
    isAuthenticated() {
        return !!(this.currentToken && this.currentUser);
    }

    /**
     * Obtener token actual
     */
    getToken() {
        return this.currentToken || localStorage.getItem(this.TOKEN_KEY);
    }

    /**
     * Obtener usuario actual
     */
    getUser() {
        if (this.currentUser) {
            return this.currentUser;
        }
        
        const userStr = localStorage.getItem(this.USER_KEY);
        if (userStr) {
            try {
                return JSON.parse(userStr);
            } catch (error) {
                console.error('Error parsing stored user:', error);
                return null;
            }
        }
        
        return null;
    }

    /**
     * Hacer petición autenticada a la API
     */
    async apiCall(endpoint, options = {}) {
        const token = this.getToken();
        
        if (!token) {
            throw new Error('No authentication token available');
        }

        const defaultHeaders = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };

        const config = {
            ...options,
            headers: {
                ...defaultHeaders,
                ...options.headers
            }
        };

        try {
            const response = await fetch(`${this.API_BASE_URL}${endpoint}`, config);
            
            if (response.status === 401) {
                console.log('⚠️ Token inválido o expirado, limpiando sesión');
                this.clearSession();
                throw new Error('Authentication failed - token expired or invalid');
            }
            
            if (response.status === 403) {
                console.log('🚫 Acceso denegado - sin permisos para este recurso');
                throw new Error('Access denied - insufficient permissions');
            }

            return response;
        } catch (error) {
            if (error.message.includes('Authentication failed')) {
                // Solo redirigir al login si es un error de autenticación (401)
                window.location.href = '/login.html';
            }
            // Para errores 403, no redirigir, solo mostrar el error
            throw error;
        }
    }

    /**
     * Verificar si el usuario tiene un rol específico
     */
    hasRole(role) {
        const user = this.getUser();
        return user && user.role === role;
    }

    /**
     * Verificar si el usuario es admin
     */
    isAdmin() {
        return this.hasRole('ADMIN');
    }

    /**
     * Obtener información de la sesión
     */
    getSessionInfo() {
        const loginTime = localStorage.getItem(this.LOGIN_TIME_KEY);
        const user = this.getUser();
        
        if (!loginTime || !user) return null;

        const loginDate = new Date(loginTime);
        const now = new Date();
        const sessionDuration = Math.floor((now - loginDate) / 1000 / 60); // minutos

        return {
            user: user,
            loginTime: loginDate,
            sessionDuration: sessionDuration,
            isAuthenticated: this.isAuthenticated()
        };
    }

    /**
     * Configurar validación automática del token
     */
    setupTokenValidation() {
        // Validar token cada 5 minutos
        setInterval(() => {
            if (this.isAuthenticated()) {
                this.validateStoredToken();
            }
        }, 5 * 60 * 1000);

        // Validar token cuando la ventana recupera el foco
        window.addEventListener('focus', () => {
            if (this.isAuthenticated()) {
                this.validateStoredToken();
            }
        });
    }

    /**
     * Proteger una página (redirigir al login si no está autenticado)
     */
    protectPage() {
        if (!this.isAuthenticated()) {
            console.log('⚠️ Página protegida - redirigiendo al login');
            window.location.href = '/login.html';
            return false;
        }
        return true;
    }

    /**
     * Mostrar información del usuario en la interfaz
     */
    displayUserInfo(containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        const sessionInfo = this.getSessionInfo();
        if (!sessionInfo) {
            container.innerHTML = '<p>No hay sesión activa</p>';
            return;
        }

        container.innerHTML = `
            <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 10px 0;">
                <h4>👤 Sesión Activa</h4>
                <p><strong>Usuario:</strong> ${sessionInfo.user.username}</p>
                <p><strong>Rol:</strong> ${sessionInfo.user.role}</p>
                <p><strong>Login:</strong> ${sessionInfo.loginTime.toLocaleString()}</p>
                <p><strong>Duración:</strong> ${sessionInfo.sessionDuration} minutos</p>
                <button onclick="dogoAuth.logout()" style="
                    background: #dc3545; color: white; border: none; 
                    padding: 8px 16px; border-radius: 4px; cursor: pointer;
                    margin-top: 10px;
                ">
                    Cerrar Sesión
                </button>
            </div>
        `;
    }
}

// Crear instancia global
const dogoAuth = new DoGoAuth();

// Hacer disponible globalmente
window.dogoAuth = dogoAuth;
