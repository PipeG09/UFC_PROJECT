/**
 * Servicio para manejar eventos UFC
 */
const EventService = {
    /**
     * Obtener todos los eventos
     */
    async getAllEvents() {
        return ApiService.get('/eventos');
    },

    /**
     * Obtener evento por ID
     */
    async getEventById(eventId) {
        return ApiService.get(`/eventos/${eventId}`);
    },

    /**
     * Crear nuevo evento
     */
    async createEvent(eventData) {
        return ApiService.post('/eventos', eventData);
    },

    /**
     * Actualizar evento
     */
    async updateEvent(eventId, eventData) {
        return ApiService.put(`/eventos/${eventId}`, eventData);
    },

    /**
     * Eliminar evento
     */
    async deleteEvent(eventId) {
        return ApiService.delete(`/eventos/${eventId}`);
    },

    /**
     * Obtener todos los luchadores
     */
    async getAllFighters() {
        return ApiService.get('/luchadores');
    },

    /**
     * Obtener luchador por ID
     */
    async getFighterById(fighterId) {
        return ApiService.get(`/luchadores/${fighterId}`);
    }
};