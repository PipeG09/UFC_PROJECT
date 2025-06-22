/**
 * Utilidades para manejo de fechas
 */
const DateUtils = {
    /**
     * Parsear fecha de manera segura
     */
    parseFecha(fechaString) {
        if (!fechaString) {
            console.warn('⚠️ Fecha vacía o null:', fechaString);
            return null;
        }

        try {
            // Si es un array, convertir a fecha
            if (Array.isArray(fechaString)) {
                console.warn('⚠️ Fecha es un array:', fechaString);
                if (fechaString.length >= 6) {
                    const [year, month, day, hour, minute, second, nanosecond] = fechaString;
                    const millisecond = nanosecond ? Math.floor(nanosecond / 1000000) : 0;
                    const fecha = new Date(year, month - 1, day, hour || 0, minute || 0, second || 0, millisecond);
                    console.log('✅ Fecha convertida desde array:', fecha.toISOString());
                    return fecha;
                }
                return null;
            }

            let fecha;

            // Si la fecha no tiene 'Z' o zona horaria, agregarla
            if (typeof fechaString === 'string' && !fechaString.includes('Z') && !fechaString.includes('+') && !fechaString.includes('-', 10)) {
                fecha = new Date(fechaString + 'Z');
                console.log(`🔧 Fecha sin zona horaria, agregando Z: ${fechaString} -> ${fecha.toISOString()}`);
            } else {
                fecha = new Date(fechaString);
            }

            if (isNaN(fecha.getTime())) {
                console.error('❌ Fecha inválida después del parsing:', fechaString);
                return null;
            }

            return fecha;
        } catch (error) {
            console.error('❌ Error parseando fecha:', fechaString, error);
            return null;
        }
    },

    /**
     * Formatear fecha para mostrar
     */
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },

    /**
     * Formatear fecha y hora
     */
    formatDateTime(dateString) {
        const date = new Date(dateString);
        return date.toLocaleString('es-ES', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    /**
     * Formatear tiempo desde segundos
     */
    formatTime(seconds) {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    }
};