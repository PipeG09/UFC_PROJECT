/**
 * Componente de Eventos
 */
const EventsComponent = {
    /**
     * Crear evento
     */
    createEvent() {
        if (!AuthService.isAdmin()) {
            ToastUtils.show('Solo los administradores pueden crear eventos', 'error');
            return;
        }

        const name = prompt('Nombre del evento:');
        const location = prompt('Ubicación:');
        const date = prompt('Fecha (YYYY-MM-DD):');

        if (name && location && date) {
            this.createEventAPI(name, location, date);
        }
    },

    /**
     * Crear evento en la API
     */
    async createEventAPI(nombre, ubicacion, fecha) {
        try {
            await EventService.createEvent({
                nombre: nombre,
                ubicacion: ubicacion,
                fecha: fecha
            });

            ToastUtils.show('Evento creado exitosamente', 'success');
            await DashboardComponent.loadEvents();
        } catch (error) {
            ToastUtils.show('Error creando evento', 'error');
        }
    },

    /**
     * Editar evento
     */
    editEvent(eventId) {
        if (!AuthService.isAdmin()) {
            ToastUtils.show('Solo los administradores pueden editar eventos', 'error');
            return;
        }
        ToastUtils.show(`Editando evento ID: ${eventId}`, 'success');
    },
        async renderEventsWithFighters(eventos) {
        const eventsGrid = DomUtils.getElement('eventsGrid');

        if (eventos.length === 0) {
            eventsGrid.innerHTML = this.renderEmptyState();
            return;
        }

        // Mostrar loading
        eventsGrid.innerHTML = this.renderLoadingState();

        // Cargar luchadores
        let luchadores = [];
        try {
            luchadores = await EventService.getAllFighters();
        } catch (error) {
            console.error('❌ Error cargando luchadores:', error);
        }

        // Cargar peleas para cada evento
        const eventosConPeleas = [];
        for (const evento of eventos) {
            try {
                const peleasDelEvento = await FightService.getFightsByEvent(evento.id);
                const clasificadas = FightService.clasificarPeleas(peleasDelEvento);

                eventosConPeleas.push({
                    ...evento,
                    peleas: peleasDelEvento,
                    peleasEnVivo: clasificadas.enVivo,
                    peleasFuturas: clasificadas.futuras,
                    peleasFinalizadas: clasificadas.finalizadas
                });
            } catch (error) {
                console.error(`❌ Error cargando peleas para evento ${evento.id}:`, error);
                eventosConPeleas.push({
                    ...evento,
                    peleas: [],
                    peleasEnVivo: [],
                    peleasFuturas: [],
                    peleasFinalizadas: []
                });
            }
        }

        // Renderizar eventos
        eventsGrid.innerHTML = eventosConPeleas.map(evento =>
            this.renderEventCard(evento, luchadores)
        ).join('');
    },

    /**
     * Renderizar una tarjeta de evento
     */
    renderEventCard(evento, luchadores) {
        const totalPeleas = evento.peleas.length;
        const enVivo = evento.peleasEnVivo.length;
        const futuras = evento.peleasFuturas.length;
        const finalizadas = evento.peleasFinalizadas.length;

        // Crear resumen de estado
        let estadoInfo = '';
        if (totalPeleas > 0) {
            const estados = [];
            if (enVivo > 0) estados.push(`${enVivo} en vivo`);
            if (futuras > 0) estados.push(`${futuras} futuras`);
            if (finalizadas > 0) estados.push(`${finalizadas} finalizadas`);
            estadoInfo = estados.length > 0 ? ` (${estados.join(', ')})` : '';
        }

        // Peleas para mostrar
        const peleasParaMostrar = [
            ...evento.peleasEnVivo.slice(0, 2),
            ...evento.peleasFuturas.slice(0, 3 - evento.peleasEnVivo.slice(0, 2).length)
        ];

        const peleasHTML = this.renderFightsList(peleasParaMostrar, luchadores);
        const tieneEnVivo = enVivo > 0;

        return `
            <div class="event-card ${tieneEnVivo ? 'live-event' : ''}">
                <div class="event-header">
                    <h3>${evento.nombre} ${tieneEnVivo ? '🔴' : ''}</h3>
                    <div class="event-date">
                        <i class="fas fa-calendar"></i>
                        ${DateUtils.formatDate(evento.fecha)}
                    </div>
                </div>
                <div class="event-body">
                    <div class="event-location">
                        <i class="fas fa-map-marker-alt"></i>
                        ${evento.ubicacion || 'Ubicación por definir'}
                    </div>
                    <div class="fights-preview">
                        <h4>
                            <span><i class="fas fa-fist-raised"></i> Peleas ${totalPeleas}</span>
                            ${tieneEnVivo ? '<span class="fight-count-badge live-badge">EN VIVO</span>' : ''}
                        </h4>
                        ${peleasHTML}
                        ${totalPeleas > peleasParaMostrar.length ? `
                            <div style="text-align: center; margin-top: 1rem; color: #d32f2f; font-size: 0.9rem;">
                                <i class="fas fa-plus-circle"></i> ${totalPeleas - peleasParaMostrar.length} peleas más
                            </div>
                        ` : ''}
                    </div>
                    <div class="event-actions">
                        <button class="btn" onclick="EventsComponent.viewEvent(${evento.id})">
                            <i class="fas fa-eye"></i> Ver Evento
                        </button>
                        ${tieneEnVivo ? `
                            <button class="btn" onclick="EventsComponent.verPeleasEnVivo(${evento.id})" 
                                    style="background: linear-gradient(45deg, #4caf50, #66bb6a);">
                                <i class="fas fa-play"></i> Ver En Vivo
                            </button>
                        ` : ''}
                        ${AuthService.isAdmin() ? `
                            <button class="btn btn-secondary" onclick="EventsComponent.editEvent(${evento.id})">
                                <i class="fas fa-edit"></i> Editar
                            </button>
                        ` : ''}
                    </div>
                </div>
            </div>
        `;
    },

    /**
     * Renderizar lista de peleas
     */
    renderFightsList(peleas, luchadores) {
        if (peleas.length === 0) {
            return `
                <div class="fight-item">
                    <div class="fighters">
                        <span class="fighter">No hay peleas programadas</span>
                    </div>
                    <span class="fight-status status-upcoming">Por definir</span>
                </div>
            `;
        }

        return peleas.map(pelea => {
            const luchadorAzul = luchadores.find(l => l.id === pelea.luchadorAzulId);
            const luchadorRojo = luchadores.find(l => l.id === pelea.luchadorRojoId);
            const estado = FightService.determinarEstadoPelea(pelea);

            return `
                <div class="fight-item">
                    <div class="fighters">
                        <span class="fighter">${luchadorAzul ? luchadorAzul.nombre : 'Luchador TBD'}</span>
                        ${luchadorAzul && luchadorAzul.historial ?
                `<span class="fighter-record">(${luchadorAzul.historial})</span>` : ''}
                        <span class="vs">VS</span>
                        <span class="fighter">${luchadorRojo ? luchadorRojo.nombre : 'Luchador TBD'}</span>
                        ${luchadorRojo && luchadorRojo.historial ?
                `<span class="fighter-record">(${luchadorRojo.historial})</span>` : ''}
                    </div>
                    <span class="fight-status ${this.getFightStatusClass(estado)}">
                        ${this.getFightStatusText(estado)}
                    </span>
                </div>
            `;
        }).join('');
    },

    /**
     * Obtener clase CSS para estado de pelea
     */
    getFightStatusClass(estado) {
        switch (estado) {
            case FightStatus.FINISHED:
                return 'status-finished';
            case FightStatus.LIVE:
                return 'status-live';
            case FightStatus.UPCOMING:
                return 'status-upcoming';
            default:
                return 'status-upcoming';
        }
    },

    /**
     * Obtener texto para estado de pelea
     */
    getFightStatusText(estado) {
        switch (estado) {
            case FightStatus.FINISHED:
                return 'Finalizada';
            case FightStatus.LIVE:
                return '🔴 En Vivo';
            case FightStatus.UPCOMING:
                return 'Próximamente';
            default:
                return 'Próximamente';
        }
    },

    /**
     * Renderizar estado vacío
     */
    renderEmptyState() {
        return `
            <div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: #cccccc;">
                <i class="fas fa-calendar-times" style="font-size: 3rem; margin-bottom: 1rem; color: #666;"></i>
                <h3>No hay eventos disponibles</h3>
                <p>Crea tu primer evento para comenzar</p>
                ${AuthService.isAdmin() ? `
                    <button class="btn" onclick="EventsComponent.createEvent()" style="margin-top: 1rem;">
                        <i class="fas fa-plus"></i> Crear Evento
                    </button>
                ` : ''}
            </div>
        `;
    },

    /**
     * Renderizar estado de carga
     */
    renderLoadingState() {
        return `
            <div style="grid-column: 1/-1; text-align: center; padding: 2rem; color: #cccccc;">
                <div class="loading-spinner"></div>
                <p>Cargando peleas de los eventos...</p>
            </div>
        `;
    },

    /**
     * Ver evento
     */
    async viewEvent(eventId) {
        try {
            const evento = await EventService.getEventById(eventId);
            const peleas = await FightService.getFightsByEvent(eventId);
            const luchadores = await EventService.getAllFighters();

            this.showEventDetails(evento, peleas, luchadores);
        } catch (error) {
            console.error('Error cargando detalles del evento:', error);
            ToastUtils.show('Error cargando detalles del evento', 'error');
        }
    },

    /**
     * Mostrar detalles del evento
     */
    showEventDetails(evento, peleas, luchadores) {
        // Implementar modal de detalles del evento
        ToastUtils.show(`Viendo evento: ${evento.nombre}`, 'success');
    },

    /**
     * Ver peleas en vivo de un evento
     */
    async verPeleasEnVivo(eventoId) {
        try {
            const peleasDelEvento = await FightService.getFightsByEvent(eventoId);
            const clasificadas = FightService.clasificarPeleas(peleasDelEvento);

            if (clasificadas.enVivo.length > 0) {
                await LiveFightComponent.showFight(clasificadas.enVivo[0]);
                ToastUtils.show(`Viendo pelea en vivo`, 'success');
            } else {
                ToastUtils.show('No hay peleas en vivo en este evento ahora mismo', 'error');
            }
        } catch (error) {
            console.error('Error accediendo a peleas en vivo:', error);
            ToastUtils.show('Error accediendo a peleas en vivo', 'error');
        }
    },
};

