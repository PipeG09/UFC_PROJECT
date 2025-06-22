/**
 * Servicio WebSocket para actualizaciones en tiempo real
 */
class WebSocketService {
    constructor() {
        this.ws = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = Config.WS_RECONNECT_ATTEMPTS;
        this.reconnectDelay = Config.WS_RECONNECT_DELAY;
        this.isConnected = false;
        this.shouldReconnect = true;
    }

    connect() {
        if (!this.shouldReconnect) {
            console.log('🚫 WebSocket: Conexión deshabilitada');
            return;
        }

        try {
            console.log('🔌 Intentando conectar WebSocket...');
            this.ws = new WebSocket(Config.WS_URL);

            this.ws.onopen = (event) => {
                console.log('✅ WebSocket conectado');
                this.isConnected = true;
                this.reconnectAttempts = 0;
            };

            this.ws.onmessage = (event) => {
                try {
                    const message = JSON.parse(event.data);
                    this.handleMessage(message);
                } catch (error) {
                    console.error('❌ Error parseando mensaje WebSocket:', error);
                }
            };

            this.ws.onclose = (event) => {
                console.log('🔌 WebSocket desconectado:', event.code, event.reason);
                this.isConnected = false;

                if (this.shouldReconnect && this.reconnectAttempts < this.maxReconnectAttempts) {
                    this.attemptReconnect();
                }
            };

            this.ws.onerror = (error) => {
                console.error('❌ Error WebSocket:', error);
            };

        } catch (error) {
            console.error('❌ Error creando WebSocket:', error);
            this.attemptReconnect();
        }
    }

    handleMessage(message) {
        console.log('📨 Mensaje WebSocket recibido:', message);

        switch (message.type) {
            case 'welcome':
                console.log('👋 WebSocket: Bienvenida recibida');
                break;

            case 'fight-stats':
                console.log('📊 WebSocket: Estadísticas de pelea recibidas');
                // Actualizar UI con datos del WebSocket
                if (AppState.currentView === ViewNames.LIVE_FIGHT && AppState.currentFightData) {
                    LiveFightComponent.updateStatsFromWebSocket(message.data);
                }
                break;

            case 'no-fights':
                console.log('📭 WebSocket: No hay peleas en vivo');
                break;

            default:
                console.log('❓ WebSocket: Mensaje no reconocido:', message);
        }
    }

    attemptReconnect() {
        if (!this.shouldReconnect || this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.log('🛑 WebSocket: Máximo de reintentos alcanzado');
            return;
        }

        this.reconnectAttempts++;
        console.log(`🔄 WebSocket: Reintento ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);

        setTimeout(() => {
            if (this.shouldReconnect) {
                this.connect();
            }
        }, this.reconnectDelay);

        this.reconnectDelay = Math.min(this.reconnectDelay * 1.5, 10000);
    }

    disconnect() {
        console.log('🔌 Desconectando WebSocket...');
        this.shouldReconnect = false;

        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }

        this.isConnected = false;
        this.reconnectAttempts = 0;
    }

    enable() {
        this.shouldReconnect = true;
        this.reconnectAttempts = 0;
        this.reconnectDelay = Config.WS_RECONNECT_DELAY;
    }
}