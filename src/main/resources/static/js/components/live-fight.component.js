/**
 * Componente de Peleas en Vivo
 */
const LiveFightComponent = {
    pollingTimer: null,
    isPollingActive: false,

    /**
     * Mostrar peleas en vivo
     */
    async show() {
        DomUtils.hideAllViews();
        DomUtils.addClass('liveFight', 'active');
        AppState.currentView = ViewNames.LIVE_FIGHT;

        try {
            const peleasEnVivo = await FightService.getLiveFights();

            if (peleasEnVivo.length === 0) {
                this.showNoLiveFightsMessage();
                return;
            }

            console.log(`✅ Encontradas ${peleasEnVivo.length} peleas EN VIVO`);
            await this.showFight(peleasEnVivo[0]);

        } catch (error) {
            console.error('❌ Error cargando peleas en vivo:', error);
            this.showNoLiveFightsMessage();
        }
    },

    /**
     * Mostrar una pelea específica
     */
    async showFight(pelea) {
        AppState.currentFightData = {
            peleaId: pelea.id,
            luchadorAzulId: pelea.luchadorAzulId,
            luchadorRojoId: pelea.luchadorRojoId
        };

        // Renderizar HTML de la pelea
        DomUtils.setHTML('liveFight', this.render());

        // Mostrar información de la pelea
        await this.displayFightInfo(pelea);

        // Iniciar polling para actualizaciones
        this.startPolling(pelea.id, pelea.luchadorAzulId, pelea.luchadorRojoId);

        // Conectar WebSocket si está disponible
        if (!AppState.liveWebSocket) {
            AppState.liveWebSocket = new WebSocketService();
        }
        AppState.liveWebSocket.enable();
        AppState.liveWebSocket.connect();
    },

    /**
     * Mostrar información de la pelea
     */
    async displayFightInfo(pelea) {
        try {
            // Cargar información del evento
            const evento = await EventService.getEventById(pelea.eventoId);

            // Cargar información de los luchadores
            const [luchadorAzul, luchadorRojo] = await Promise.all([
                EventService.getFighterById(pelea.luchadorAzulId),
                EventService.getFighterById(pelea.luchadorRojoId)
            ]);

            // Actualizar UI
            DomUtils.setText('fightTitle', `${evento.nombre} - Pelea Principal`);

            // Actualizar luchador azul
            DomUtils.setText('blueNameElement', luchadorAzul.nombre);
            DomUtils.setHTML('blueRecordElement', `
                <div class="fighter-nationality">${luchadorAzul.nacionalidad || 'Internacional'}</div>
                <div class="fighter-record">${luchadorAzul.historial || 'N/A'}</div>
                <div class="fighter-weight-class">${luchadorAzul.categoriaPeso || ''}</div>
            `);

            // Actualizar luchador rojo
            DomUtils.setText('redNameElement', luchadorRojo.nombre);
            DomUtils.setHTML('redRecordElement', `
                <div class="fighter-nationality">${luchadorRojo.nacionalidad || 'Internacional'}</div>
                <div class="fighter-record">${luchadorRojo.historial || 'N/A'}</div>
                <div class="fighter-weight-class">${luchadorRojo.categoriaPeso || ''}</div>
            `);

            // Cargar estadísticas
            await this.loadFightStatistics(pelea.id, pelea.luchadorAzulId, pelea.luchadorRojoId);

        } catch (error) {
            console.error('❌ Error mostrando información de la pelea:', error);
            ToastUtils.show('Error cargando información de la pelea', 'error');
        }
    },

    /**
     * Cargar estadísticas de la pelea
     */
    async loadFightStatistics(peleaId, luchadorAzulId, luchadorRojoId) {
        try {
            const todasEstadisticas = await FightService.getFightStatistics();

            // Filtrar estadísticas de esta pelea
            const statsAzul = todasEstadisticas.filter(s =>
                s.peleaId === peleaId && s.luchadorId === luchadorAzulId);
            const statsRojo = todasEstadisticas.filter(s =>
                s.peleaId === peleaId && s.luchadorId === luchadorRojoId);

            // Calcular totales
            const totalesAzul = FightService.calcularTotales(statsAzul);
            const totalesRojo = FightService.calcularTotales(statsRojo);

            // Actualizar UI
            this.updateStatsDisplay(totalesAzul, totalesRojo);

            // Cargar probabilidades
            await this.loadProbabilities(peleaId, luchadorAzulId, luchadorRojoId, totalesAzul, totalesRojo);

        } catch (error) {
            console.error('❌ Error cargando estadísticas:', error);
        }
    },

    /**
     * Actualizar display de estadísticas
     */
    updateStatsDisplay(totalesAzul, totalesRojo) {
        DomUtils.setText('blueStrikes', totalesAzul.golpes);
        DomUtils.setText('redStrikes', totalesRojo.golpes);
        DomUtils.setText('blueTakedowns', totalesAzul.derribos);
        DomUtils.setText('redTakedowns', totalesRojo.derribos);
        DomUtils.setText('blueCageControl', DateUtils.formatTime(totalesAzul.controlJaula));
        DomUtils.setText('redCageControl', DateUtils.formatTime(totalesRojo.controlJaula));

        const currentRound = Math.max(totalesAzul.round, totalesRojo.round);
        DomUtils.setText('fightStatus', `🔴 EN VIVO - Round ${currentRound}`);
    },

    /**
     * Cargar probabilidades
     */
    async loadProbabilities(peleaId, luchadorAzulId, luchadorRojoId, totalesAzul, totalesRojo) {
        try {
            const todasProbabilidades = await FightService.getProbabilities();

            // Buscar probabilidades más recientes
            const probAzul = todasProbabilidades
                .filter(p => p.peleaId === peleaId && p.luchadorId === luchadorAzulId)
                .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))[0];

            const probRojo = todasProbabilidades
                .filter(p => p.peleaId === peleaId && p.luchadorId === luchadorRojoId)
                .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))[0];

            let blueProbability = 50;
            let redProbability = 50;

            if (probAzul && probRojo) {
                blueProbability = Math.round(parseFloat(probAzul.probabilidad));
                redProbability = Math.round(parseFloat(probRojo.probabilidad));
            } else {
                // Calcular basándose en estadísticas
                const result = this.calculateProbabilities(totalesAzul, totalesRojo);
                blueProbability = result.blue;
                redProbability = result.red;
            }

            this.updateProbabilityDisplay(blueProbability, redProbability);

        } catch (error) {
            console.error('❌ Error cargando probabilidades:', error);
        }
    },

    /**
     * Calcular probabilidades basadas en estadísticas
     */
    calculateProbabilities(totalesAzul, totalesRojo) {
        const scoreAzul = totalesAzul.golpes + (totalesAzul.derribos * 5) + (totalesAzul.controlJaula / 60);
        const scoreRojo = totalesRojo.golpes + (totalesRojo.derribos * 5) + (totalesRojo.controlJaula / 60);
        const total = scoreAzul + scoreRojo;

        if (total === 0) {
            return { blue: 50, red: 50 };
        }

        const blue = Math.round((scoreAzul / total) * 100);
        const red = 100 - blue;

        return { blue, red };
    },

    /**
     * Actualizar display de probabilidades
     */
    updateProbabilityDisplay(blueProbability, redProbability) {
        DomUtils.setText('probBlueText', `${blueProbability}%`);
        DomUtils.setText('probRedText', `${redProbability}%`);

        const probBlueBar = DomUtils.getElement('probBlueBar');
        const probRedBar = DomUtils.getElement('probRedBar');

        if (probBlueBar) probBlueBar.style.width = `${blueProbability}%`;
        if (probRedBar) probRedBar.style.width = `${redProbability}%`;
    },

    /**
     * Iniciar polling de datos
     */
    startPolling(peleaId, luchadorAzulId, luchadorRojoId) {
        this.stopPolling();
        this.isPollingActive = true;

        const updateData = async () => {
            if (!this.isPollingActive) return;

            await this.loadFightStatistics(peleaId, luchadorAzulId, luchadorRojoId);
        };

        // Actualizar inmediatamente
        updateData();

        // Programar actualizaciones periódicas
        this.pollingTimer = setInterval(updateData, Config.POLLING_INTERVAL);
    },

    /**
     * Detener polling
     */
    stopPolling() {
        this.isPollingActive = false;
        if (this.pollingTimer) {
            clearInterval(this.pollingTimer);
            this.pollingTimer = null;
        }
    },

    /**
     * Actualizar estadísticas desde WebSocket
     */
    updateStatsFromWebSocket(data) {
        // Actualizar UI con datos del WebSocket
        if (data.blueStrikes !== undefined) DomUtils.setText('blueStrikes', data.blueStrikes);
        if (data.redStrikes !== undefined) DomUtils.setText('redStrikes', data.redStrikes);
        if (data.blueTakedowns !== undefined) DomUtils.setText('blueTakedowns', data.blueTakedowns);
        if (data.redTakedowns !== undefined) DomUtils.setText('redTakedowns', data.redTakedowns);
        if (data.blueCageControl !== undefined) DomUtils.setText('blueCageControl', DateUtils.formatTime(data.blueCageControl));
        if (data.redCageControl !== undefined) DomUtils.setText('redCageControl', DateUtils.formatTime(data.redCageControl));

        if (data.blueProbability !== undefined && data.redProbability !== undefined) {
            this.updateProbabilityDisplay(data.blueProbability, data.redProbability);
        }
    },

    /**
     * Mostrar mensaje de no hay peleas en vivo
     */
    showNoLiveFightsMessage() {
        DomUtils.setHTML('liveFight', `
            <div class="no-live-fights" style="text-align: center; padding: 4rem 2rem; color: #cccccc;">
                <button class="btn btn-secondary" onclick="App.showDashboard()" style="float: left; margin-bottom: 2rem;">
                    <i class="fas fa-arrow-left"></i> Volver
                </button>
                <div style="clear: both;"></div>

                <div style="background: linear-gradient(145deg, #1a1f2e, #2d1b2e); padding: 3rem; border-radius: 20px; border: 2px solid #404040; max-width: 600px; margin: 0 auto;">
                    <i class="fas fa-clock" style="font-size: 4rem; color: #666; margin-bottom: 2rem;"></i>
                    <h2 style="color: #d32f2f; margin-bottom: 1rem; font-size: 2rem;">No hay peleas en vivo ahora</h2>
                    <p style="font-size: 1.2rem; margin-bottom: 2rem; line-height: 1.6;">
                        No hay peleas que hayan empezado y estén transmitiendo en este momento.
                    </p>

                    <div style="background: rgba(255, 193, 7, 0.1); padding: 1.5rem; border-radius: 10px; border-left: 4px solid #ffc107; margin-bottom: 2rem;">
                        <h4 style="color: #ffc107; margin-bottom: 0.5rem;">
                            <i class="fas fa-info-circle"></i> ¿Qué significa "en vivo"?
                        </h4>
                        <p style="color: #cccccc; margin: 0; font-size: 0.9rem;">
                            Una pelea está "en vivo" solo cuando ya empezó (la fecha/hora de la pelea es anterior a ahora)
                            y aún no ha terminado. Las peleas programadas para el futuro aparecen como "Próximamente".
                        </p>
                    </div>

                    <div style="display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap;">
                        <button class="btn" onclick="App.showDashboard()">
                            <i class="fas fa-home"></i> Dashboard
                        </button>
                        <button class="btn btn-secondary" onclick="App.refreshData()">
                            <i class="fas fa-sync"></i> Actualizar
                        </button>
                    </div>
                </div>
            </div>
        `);
    },

    /**
     * Renderizar HTML del componente
     */
    render() {
        return `
            <div class="fight-header">
                <button class="btn btn-secondary" onclick="App.showDashboard()" style="float: left;">
                    <i class="fas fa-arrow-left"></i> Volver
                </button>
                <div class="fight-title" id="fightTitle">Cargando...</div>
                <div class="fighters-vs">
                    <div class="fighter-card">
                        <div class="fighter-avatar">
                            <img src="https://ssl.gstatic.com/onebox/media/sports/photos/ufc/3605_H-bQHA_96x96.png" 
                                 alt="Avatar" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">
                        </div>
                        <div class="fighter-name" id="blueNameElement">Cargando...</div>
                        <div class="fighter-record" id="blueRecordElement">Cargando...</div>
                    </div>
                    <div class="vs-separator">VS</div>
                    <div class="fighter-card">
                        <div class="fighter-avatar">
                            <img src="https://ssl.gstatic.com/onebox/media/sports/photos/ufc/1531_bqT22g_96x96.png" 
                                 alt="Avatar" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">
                        </div>
                        <div class="fighter-name" id="redNameElement">Cargando...</div>
                        <div class="fighter-record" id="redRecordElement">Cargando...</div>
                    </div>
                </div>
                <div style="text-align: center; margin-top: 1rem;">
                    <span class="fight-status status-live" id="fightStatus">🔴 EN VIVO</span>
                </div>
            </div>

            <div class="live-stats">
                <div class="stats-panel">
                    <h3>📊 Estadísticas del Round</h3>
                    <div class="stat-row">
                        <span class="stat-label">Golpes Conectados</span>
                        <div class="stat-values">
                            <span class="stat-blue" id="blueStrikes">0</span>
                            <span class="stat-red" id="redStrikes">0</span>
                        </div>
                    </div>
                    <div class="stat-row">
                        <span class="stat-label">Derribos</span>
                        <div class="stat-values">
                            <span class="stat-blue" id="blueTakedowns">0</span>
                            <span class="stat-red" id="redTakedowns">0</span>
                        </div>
                    </div>
                    <div class="stat-row">
                        <span class="stat-label">Control de Jaula</span>
                        <div class="stat-values">
                            <span class="stat-blue" id="blueCageControl">0:00</span>
                            <span class="stat-red" id="redCageControl">0:00</span>
                        </div>
                    </div>
                </div>

                <div class="probability-panel">
                    <h3>🎯 Probabilidades de Victoria</h3>
                    <div class="probability-chart">
                        <div class="prob-blue" id="probBlueBar" style="width: 50%;"></div>
                        <div class="prob-red" id="probRedBar" style="width: 50%;"></div>
                    </div>
                    <div class="prob-values">
                        <span class="stat-blue" id="probBlueText">50%</span>
                        <span class="stat-red" id="probRedText">50%</span>
                    </div>
                    <p style="color: #cccccc; margin-top: 1rem; font-size: 0.9rem;">
                        Actualizado en tiempo real
                    </p>
                </div>
            </div>
        `;
    }
};