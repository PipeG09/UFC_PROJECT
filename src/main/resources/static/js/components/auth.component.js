/**
 * Componente de autenticación
 */
const AuthComponent = {
    isLoginMode: true,

    /**
     * Inicializar componente
     */
    init() {
        this.setupEventListeners();
    },

    /**
     * Configurar event listeners
     */
    setupEventListeners() {
        const loginForm = DomUtils.getElement('loginForm');
        const registerForm = DomUtils.getElement('registerForm');

        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        if (registerForm) {
            registerForm.addEventListener('submit', (e) => this.handleRegister(e));
        }
    },

    /**
     * Mostrar modal de autenticación
     */
    show() {
        const modal = DomUtils.getElement('authModal');
        if (modal) {
            modal.style.display = 'flex';
            modal.classList.add('show');
        }
    },

    /**
     * Ocultar modal de autenticación
     */
    hide() {
        const modal = DomUtils.getElement('authModal');
        if (modal) {
            modal.style.display = 'none';
            modal.classList.remove('show');
        }
    },

    /**
     * Toggle entre login y registro
     */
    toggleAuthMode() {
        this.isLoginMode = !this.isLoginMode;

        if (this.isLoginMode) {
            DomUtils.removeClass('loginForm', 'hidden');
            DomUtils.addClass('registerForm', 'hidden');
            DomUtils.setText('authSwitchText', '¿No tienes cuenta?');
            DomUtils.setText('authSwitchLink', 'Regístrate aquí');
        } else {
            DomUtils.addClass('loginForm', 'hidden');
            DomUtils.removeClass('registerForm', 'hidden');
            DomUtils.setText('authSwitchText', '¿Ya tienes cuenta?');
            DomUtils.setText('authSwitchLink', 'Inicia sesión aquí');
        }
    },

    /**
     * Manejar login
     */
    async handleLogin(e) {
        e.preventDefault();

        const email = DomUtils.getElement('loginEmail').value;
        const password = DomUtils.getElement('loginPassword').value;

        DomUtils.setLoadingState('login', true);

        try {
            await AuthService.login(email, password);
            ToastUtils.show('¡Inicio de sesión exitoso!', 'success');
            App.showApp();
        } catch (error) {
            ToastUtils.show(error.message || 'Error de inicio de sesión', 'error');
        } finally {
            DomUtils.setLoadingState('login', false);
        }
    },

    /**
     * Manejar registro
     */
    async handleRegister(e) {
        e.preventDefault();

        const name = DomUtils.getElement('registerName').value;
        const email = DomUtils.getElement('registerEmail').value;
        const password = DomUtils.getElement('registerPassword').value;

        DomUtils.setLoadingState('register', true);

        try {
            await AuthService.register(name, email, password);
            ToastUtils.show('¡Registro exitoso! Revisa tu email para la confirmación.', 'success');

            // Cambiar a modo login
            this.toggleAuthMode();

            // Pre-llenar email
            DomUtils.getElement('loginEmail').value = email;
        } catch (error) {
            ToastUtils.show(error.message || 'Error de registro', 'error');
        } finally {
            DomUtils.setLoadingState('register', false);
        }
    }
};