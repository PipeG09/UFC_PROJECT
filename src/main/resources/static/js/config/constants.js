
/**
 * Configuración global de la aplicación
 */
const Config = {
    API_BASE: 'http://localhost:8080/api',
    WS_URL: 'ws://localhost:8080/live-fight',

    // Intervalos de actualización
    POLLING_INTERVAL: 15000, // 15 segundos
    STATS_UPDATE_INTERVAL: 30000, // 30 segundos

    // WebSocket
    WS_RECONNECT_ATTEMPTS: 3,
    WS_RECONNECT_DELAY: 3000,

    // Timeouts
    REQUEST_TIMEOUT: 8000,

    // UI
    TOAST_DURATION: 3000
};

// Estado global de la aplicación
const AppState = {
    currentUser: null,
    currentView: 'dashboard',
    pollingTimer: null,
    isPollingActive: false,
    currentFightData: null,
    liveWebSocket: null
};

// Constantes de la aplicación
const ViewNames = {
    DASHBOARD: 'dashboard',
    LIVE_FIGHT: 'liveFight',
    AUTH: 'auth'
};

const UserRoles = {
    ADMIN: 'admin',
    USER: 'usuario'
};

const FightStatus = {
    LIVE: 'EN_VIVO',
    UPCOMING: 'FUTURA',
    FINISHED: 'FINALIZADA'
};