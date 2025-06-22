/**
 * Servicio para manejar todas las llamadas a la API
 */
const ApiService = {
    /**
     * Realiza una petición autenticada a la API
     */
    async authenticatedFetch(url, options = {}) {
        if (!AppState.currentUser || !AppState.currentUser.credentials) {
            throw new Error('No hay credenciales válidas');
        }

        const defaultOptions = {
            headers: {
                'Authorization': `Basic ${AppState.currentUser.credentials}`,
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        try {
            const response = await fetch(url, defaultOptions);

            if (response.status === 401) {
                console.error('❌ Error 401 - Credenciales inválidas');
                ToastUtils.show('Sesión expirada, por favor inicia sesión nuevamente', 'error');
                AuthService.logout();
                throw new Error('No autorizado');
            }

            return response;
        } catch (error) {
            console.error(`❌ Error en petición a ${url}:`, error);
            throw error;
        }
    },

    /**
     * GET request
     */
    async get(endpoint) {
        const response = await this.authenticatedFetch(`${Config.API_BASE}${endpoint}`);
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        return response.json();
    },

    /**
     * POST request
     */
    async post(endpoint, data) {
        const response = await this.authenticatedFetch(`${Config.API_BASE}${endpoint}`, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || `Error ${response.status}`);
        }
        return response.json();
    },

    /**
     * PUT request
     */
    async put(endpoint, data) {
        const response = await this.authenticatedFetch(`${Config.API_BASE}${endpoint}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || `Error ${response.status}`);
        }
        return response.json();
    },

    /**
     * DELETE request
     */
    async delete(endpoint) {
        const response = await this.authenticatedFetch(`${Config.API_BASE}${endpoint}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error(`Error ${response.status}`);
        }
    }
};