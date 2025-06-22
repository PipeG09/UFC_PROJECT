/**
 * Utilidades para manipulación del DOM
 */
const DomUtils = {
    /**
     * Obtener elemento por ID
     */
    getElement(id) {
        return document.getElementById(id);
    },

    /**
     * Mostrar elemento
     */
    showElement(id) {
        const element = this.getElement(id);
        if (element) {
            element.style.display = 'block';
        }
    },

    /**
     * Ocultar elemento
     */
    hideElement(id) {
        const element = this.getElement(id);
        if (element) {
            element.style.display = 'none';
        }
    },

    /**
     * Añadir clase a elemento
     */
    addClass(id, className) {
        const element = this.getElement(id);
        if (element) {
            element.classList.add(className);
        }
    },

    /**
     * Remover clase de elemento
     */
    removeClass(id, className) {
        const element = this.getElement(id);
        if (element) {
            element.classList.remove(className);
        }
    },

    /**
     * Toggle clase
     */
    toggleClass(id, className) {
        const element = this.getElement(id);
        if (element) {
            element.classList.toggle(className);
        }
    },

    /**
     * Establecer contenido HTML
     */
    setHTML(id, html) {
        const element = this.getElement(id);
        if (element) {
            element.innerHTML = html;
        }
    },

    /**
     * Establecer texto
     */
    setText(id, text) {
        const element = this.getElement(id);
        if (element) {
            element.textContent = text;
        }
    },

    /**
     * Ocultar todas las vistas
     */
    hideAllViews() {
        this.removeClass('dashboard', 'active');
        this.removeClass('liveFight', 'active');
    },

    /**
     * Establecer estado de carga
     */
    setLoadingState(form, loading) {
        const button = this.getElement(`${form}ButtonText`);
        const spinner = this.getElement(`${form}Loading`);

        if (loading) {
            button.style.display = 'none';
            spinner.classList.remove('hidden');
        } else {
            button.style.display = 'inline';
            spinner.classList.add('hidden');
        }
    }
};