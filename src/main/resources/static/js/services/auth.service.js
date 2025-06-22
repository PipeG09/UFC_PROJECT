/**
 * Servicio de autenticación
 */
const AuthService = {
    /**
     * Iniciar sesión
     */
    async login(email, password) {
        try {
            const credentials = btoa(`${email}:${password}`);

            const response = await fetch(`${Config.API_BASE}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const userData = await response.json();

                AppState.currentUser = {
                    ...userData,
                    credentials: credentials,
                    email: email
                };

                localStorage.setItem('currentUser', JSON.stringify(AppState.currentUser));

                console.log('✅ Login exitoso');
                return true;
            } else {
                const error = await response.json();
                throw new Error(error.error || 'Credenciales inválidas');
            }
        } catch (error) {
            console.error('❌ Error en login:', error);
            throw error;
        }
    },

    /**
     * Registrar nuevo usuario
     */
    async register(name, email, password) {
        try {
            const response = await fetch(`${Config.API_BASE}/usuarios`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    nombre: name,
                    correo: email,
                    password: password
                })
            });

            if (response.ok) {
                const result = await response.json();
                return result;
            } else {
                const error = await response.json();
                throw new Error(error.error || 'Error en el registro');
            }
        } catch (error) {
            console.error('❌ Error en registro:', error);
            throw error;
        }
    },

    /**
     * Cerrar sesión
     */
    logout() {
        AppState.currentUser = null;
        localStorage.removeItem('currentUser');
        DomUtils.hideElement('navMenu');
        DomUtils.hideElement('userInfo');
        AuthComponent.show();
    },

    /**
     * Verificar estado de autenticación
     */
    checkAuthStatus() {
        const savedUser = localStorage.getItem('currentUser');
        if (savedUser) {
            try {
                AppState.currentUser = JSON.parse(savedUser);

                if (!AppState.currentUser.credentials) {
                    console.warn('⚠️ Usuario guardado sin credenciales');
                    this.logout();
                    return false;
                }

                console.log('✅ Usuario recuperado del localStorage');
                return true;
            } catch (error) {
                console.error('❌ Error parseando usuario guardado:', error);
                this.logout();
                return false;
            }
        }
        return false;
    },

    /**
     * Verificar si el usuario es admin
     */
    isAdmin() {
        return AppState.currentUser && AppState.currentUser.rol === UserRoles.ADMIN;
    }
};