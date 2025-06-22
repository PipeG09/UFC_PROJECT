/**
 * Servicio para manejar peleas y estadísticas
 */
const FightService = {
    /**
     * Obtener todas las peleas
     */
    async getAllFights() {
        return ApiService.get('/peleas');
    },

    /**
     * Obtener peleas en vivo
     */
    async getLiveFights() {
        try {
            const todasLasPeleas = await this.getAllFights();
            const ahora = new Date();

            const peleasEnVivo = todasLasPeleas.filter(pelea => {
                if (pelea.finalizada) return false;

                const fechaPelea = DateUtils.parseFecha(pelea.fecha);
                if (!fechaPelea) return false;

                return fechaPelea <= ahora;
            });

            console.log(`📊 ${peleasEnVivo.length} peleas en vivo detectadas`);
            return peleasEnVivo;
        } catch (error) {
            console.error('❌ Error obteniendo peleas en vivo:', error);
            return [];
        }
    },

    /**
     * Obtener peleas por evento
     */
    async getFightsByEvent(eventoId) {
        try {
            const todasLasPeleas = await this.getAllFights();
            return todasLasPeleas.filter(pelea => pelea.eventoId === eventoId);
        } catch (error) {
            console.error(`❌ Error obteniendo peleas del evento ${eventoId}:`, error);
            return [];
        }
    },

    /**
     * Obtener estadísticas de una pelea
     */
    async getFightStatistics() {
        return ApiService.get('/estadisticas');
    },

    /**
     * Obtener probabilidades
     */
    async getProbabilities() {
        return ApiService.get('/probabilidades');
    },

    /**
     * Crear nueva pelea
     */
    async createFight(fightData) {
        return ApiService.post('/peleas', fightData);
    },

    /**
     * Actualizar estadísticas
     */
    async updateStatistics(statsData) {
        return ApiService.post('/estadisticas', statsData);
    },

    /**
     * Actualizar probabilidades
     */
    async updateProbabilities(probData) {
        return ApiService.post('/probabilidades', probData);
    },

    /**
     * Clasificar peleas por estado
     */
    clasificarPeleas(peleas) {
        const ahora = new Date();
        const clasificacion = {
            enVivo: [],
            futuras: [],
            finalizadas: []
        };

        peleas.forEach(pelea => {
            if (pelea.finalizada) {
                clasificacion.finalizadas.push(pelea);
            } else {
                const fechaPelea = DateUtils.parseFecha(pelea.fecha);
                if (fechaPelea && fechaPelea <= ahora) {
                    clasificacion.enVivo.push(pelea);
                } else {
                    clasificacion.futuras.push(pelea);
                }
            }
        });

        return clasificacion;
    },

    /**
     * Determinar estado de una pelea
     */
    determinarEstadoPelea(pelea) {
        if (pelea.finalizada) {
            return FightStatus.FINISHED;
        }

        const fechaPelea = DateUtils.parseFecha(pelea.fecha);
        if (!fechaPelea) {
            return 'ERROR';
        }

        const ahora = new Date();
        return fechaPelea <= ahora ? FightStatus.LIVE : FightStatus.UPCOMING;
    },

    /**
     * Calcular totales de estadísticas
     */
    calcularTotales(estadisticas) {
        if (!estadisticas || estadisticas.length === 0) {
            return {
                golpes: 0,
                derribos: 0,
                controlJaula: 0,
                round: 1
            };
        }

        return {
            golpes: estadisticas.reduce((sum, s) => sum + (parseInt(s.golpesConectados) || 0), 0),
            derribos: estadisticas.reduce((sum, s) => sum + (parseInt(s.derribos) || 0), 0),
            controlJaula: estadisticas.reduce((sum, s) => sum + (parseInt(s.controlJaulaSegundos) || 0), 0),
            round: Math.max(...estadisticas.map(s => parseInt(s.round) || 1))
        };
    }
};