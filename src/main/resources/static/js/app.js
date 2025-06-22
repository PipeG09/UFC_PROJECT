/**
 * Aplicación principal UFC Live Tracker
 */
const App = {
    /**
     * Inicializar aplicación
     */
    init() {
        console.log('🥊 Iniciando UFC Live Tracker...');

        // Inicializar componentes
        AuthComponent.init();

        // Verificar autenticación
        if (AuthService.checkAuthStatus()) {
            this.showApp();
        } else {
            AuthComponent.show();
        }

        // Event listener para limpiar al cerrar
        window.addEventListener('beforeunload', () => {
            this.cleanup();
        });
    },

    /**
     * Mostrar aplicación principal
     */
    showApp() {
        AuthComponent.hide();
        this.updateUserInfo();
        this.showDashboard();

        // Inicializar actualizaciones periódicas
        this.startPeriodicUpdates();
    },

    /**
     * Actualizar información del usuario
     */
    updateUserInfo() {
        if (AppState.currentUser) {
            DomUtils.setText('userName', AppState.currentUser.nombre);
            DomUtils.setText('userRole', AppState.currentUser.rol.toUpperCase());
            DomUtils.showElement('navMenu');
            DomUtils.showElement('userInfo');
        }
    },

    /**
     * Mostrar dashboard
     */
    async showDashboard() {
        this.cleanup();
        DomUtils.setHTML('dashboard', DashboardComponent.render());
        await DashboardComponent.show();
    },

    /**
     * Mostrar eventos
     */
    showEvents() {
        // Por ahora, igual que dashboard
        this.showDashboard();
    },

    /**
     * Mostrar peleas en vivo
     */
    async showFights() {
        this.cleanup();
        await LiveFightComponent.show();
    },

    /**
     * Actualizar datos
     */
    async refreshData() {
        ToastUtils.show('Actualizando datos...', 'success');

        if (AppState.currentView === ViewNames.DASHBOARD) {
            await DashboardComponent.loadData();
        }
    },

    /**
     * Iniciar actualizaciones periódicas
     */
    startPeriodicUpdates() {
        // Actualizar estadísticas cada 30 segundos
        setInterval(async () => {
            if (AppState.currentView === ViewNames.DASHBOARD) {
                await DashboardComponent.loadStats();
            }
        }, Config.STATS_UPDATE_INTERVAL);
    },

    /**
     * Limpiar recursos
     */
    cleanup() {
        // Detener polling
        LiveFightComponent.stopPolling();

        // Desconectar WebSocket
        if (AppState.liveWebSocket) {
            AppState.liveWebSocket.disconnect();
        }
    }
};

// Inicializar aplicación cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});