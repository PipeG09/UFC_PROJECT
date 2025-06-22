/**
 * Componente del Dashboard
 */
const DashboardComponent = {
    /**
     * Mostrar dashboard
     */
    async show() {
        DomUtils.hideAllViews();
        DomUtils.addClass('dashboard', 'active');
        AppState.currentView = ViewNames.DASHBOARD;

        await this.loadData();
    },

    /**
     * Cargar datos del dashboard
     */
    async loadData() {
        if (!AppState.currentUser) return;

        try {
            await Promise.all([
                this.loadEvents(),
                this.loadFighters(),
                this.loadStats()
            ]);
        } catch (error) {
            console.error('Error cargando datos del dashboard:', error);
            ToastUtils.show('Error cargando datos', 'error');
        }
    },

    /**
     * Cargar eventos
     */
    async loadEvents() {
        try {
            const eventos = await EventService.getAllEvents();
            DomUtils.setText('totalEvents', eventos.length);
            await EventsComponent.renderEventsWithFighters(eventos);
        } catch (error) {
            console.error('Error cargando eventos:', error);
            DomUtils.setText('totalEvents', '0');
            EventsComponent.renderEventsWithFighters([]);
        }
    },

    /**
     * Cargar luchadores
     */
    async loadFighters() {
        try {
            const luchadores = await EventService.getAllFighters();
            DomUtils.setText('totalFighters', luchadores.length);
        } catch (error) {
            console.error('Error cargando luchadores:', error);
            DomUtils.setText('totalFighters', '0');
        }
    },

    /**
     * Cargar estadísticas
     */
    async loadStats() {
        try {
            const todasLasPeleas = await FightService.getAllFights();
            const clasificadas = FightService.clasificarPeleas(todasLasPeleas);

            DomUtils.setText('liveFights', clasificadas.enVivo.length);
            DomUtils.setText('completedFights', clasificadas.finalizadas.length);

            // Añadir animación si hay peleas en vivo
            const liveFightsElement = DomUtils.getElement('liveFights');
            if (clasificadas.enVivo.length > 0) {
                liveFightsElement.style.color = '#4caf50';
                liveFightsElement.style.fontWeight = 'bold';
                liveFightsElement.parentElement.classList.add('updating');
                setTimeout(() => liveFightsElement.parentElement.classList.remove('updating'), 500);
            } else {
                liveFightsElement.style.color = '#666';
                liveFightsElement.style.fontWeight = 'normal';
            }

        } catch (error) {
            console.error('❌ Error cargando estadísticas:', error);
            DomUtils.setText('liveFights', '0');
            DomUtils.setText('completedFights', '0');
        }
    },

    /**
     * Renderizar dashboard HTML
     */
    render() {
        return `
            <div class="dashboard-header">
                <h1>🥊 UFC Live Tracker</h1>
                <p>Tu plataforma definitiva para seguimiento en tiempo real de peleas y eventos UFC</p>
            </div>

            <div class="stats-grid">
                <div class="stat-card">
                    <div class="icon"><i class="fas fa-calendar-alt"></i></div>
                    <h3 id="totalEvents">-</h3>
                    <p>Eventos Totales</p>
                </div>
                <div class="stat-card">
                    <div class="icon"><i class="fas fa-fire"></i></div>
                    <h3 id="liveFights">-</h3>
                    <p>Peleas En Vivo</p>
                </div>
                <div class="stat-card">
                    <div class="icon"><i class="fas fa-users"></i></div>
                    <h3 id="totalFighters">-</h3>
                    <p>Luchadores</p>
                </div>
                <div class="stat-card">
                    <div class="icon"><i class="fas fa-trophy"></i></div>
                    <h3 id="completedFights">-</h3>
                    <p>Peleas Finalizadas</p>
                </div>
            </div>

            <div class="events-section">
                <div class="section-header">
                    <h2><i class="fas fa-calendar"></i> Próximos Eventos</h2>
                    <div>
                        <button class="btn" onclick="App.refreshData()">
                            <i class="fas fa-sync-alt"></i> Actualizar
                        </button>
                        ${AuthService.isAdmin() ? `
                            <button class="btn" onclick="EventsComponent.createEvent()">
                                <i class="fas fa-plus"></i> Crear Evento
                            </button>
                        ` : ''}
                    </div>
                </div>
                <div class="events-grid" id="eventsGrid">
                    <!-- Events will be loaded here -->
                </div>
            </div>
        `;
    }
};