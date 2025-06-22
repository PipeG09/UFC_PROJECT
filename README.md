# UFC Live Tracker - Frontend

## Estructura del Proyecto

```
src/main/resources/static/
├── index.html                 # Archivo HTML principal
├── css/                       # Estilos CSS
│   ├── main.css              # Estilos globales
│   ├── components/           # Estilos por componente
│   │   ├── auth.css          # Estilos de autenticación
│   │   ├── dashboard.css     # Estilos del dashboard
│   │   ├── events.css        # Estilos de eventos
│   │   └── live-fight.css    # Estilos de peleas en vivo
│   └── utilities/            # Utilidades CSS
│       ├── animations.css    # Animaciones
│       └── responsive.css    # Media queries
├── js/                       # JavaScript
│   ├── app.js               # Aplicación principal
│   ├── config/              # Configuración
│   │   └── constants.js     # Constantes y configuración global
│   ├── services/            # Servicios
│   │   ├── api.service.js   # Servicio de API
│   │   ├── auth.service.js  # Servicio de autenticación
│   │   ├── event.service.js # Servicio de eventos
│   │   ├── fight.service.js # Servicio de peleas
│   │   └── websocket.service.js # Servicio WebSocket
│   ├── components/          # Componentes
│   │   ├── auth.component.js      # Componente de autenticación
│   │   ├── dashboard.component.js # Componente del dashboard
│   │   ├── events.component.js    # Componente de eventos
│   │   └── live-fight.component.js # Componente de peleas en vivo
│   └── utils/               # Utilidades
│       ├── date.utils.js    # Utilidades de fecha
│       ├── dom.utils.js     # Utilidades del DOM
│       └── toast.utils.js   # Utilidades de notificaciones
└── assets/                  # Recursos estáticos
    └── images/             # Imágenes
```

## Arquitectura

### Patrón de Diseño
- **Separación de responsabilidades**: Cada archivo tiene una responsabilidad específica
- **Componentes modulares**: Cada vista es un componente independiente
- **Servicios reutilizables**: Lógica de negocio separada de la UI
- **Utilidades compartidas**: Funciones comunes centralizadas

### Flujo de Datos
1. **App.js** - Punto de entrada y orquestador principal
2. **Services** - Manejan comunicación con API y lógica de negocio
3. **Components** - Renderizan UI y manejan interacciones
4. **Utils** - Funciones auxiliares compartidas

### Estado Global
- Centralizado en `AppState` (constants.js)
- Incluye: usuario actual, vista activa, datos de pelea actual, WebSocket

## Características Implementadas

### Autenticación
- Login/Registro con Basic Auth
- Persistencia de sesión en localStorage
- Roles de usuario (admin/usuario)

### Dashboard
- Estadísticas en tiempo real
- Grid de eventos con estado de peleas
- Actualizaciones automáticas cada 30 segundos

### Eventos
- Lista de eventos con peleas
- Detección de peleas en vivo
- Clasificación automática (en vivo/futuras/finalizadas)

### Peleas en Vivo
- Visualización en tiempo real
- Estadísticas actualizadas por polling
- Probabilidades de victoria
- Soporte para WebSocket (opcional)

### Utilidades
- Sistema de notificaciones toast
- Formateo de fechas consistente
- Manipulación del DOM simplificada
- Diseño responsivo

## Mejoras Implementadas

1. **Eliminación de código duplicado**
   - Funciones de fecha consolidadas en DateUtils
   - Lógica de API centralizada en ApiService
   - Componentes reutilizables

2. **Separación de responsabilidades**
   - CSS modular por componente
   - JavaScript organizado por funcionalidad
   - Servicios independientes de la UI

3. **Optimización de rendimiento**
   - Polling inteligente con intervalos configurables
   - Cleanup automático de recursos
   - Actualizaciones solo cuando hay cambios

4. **Mejor mantenibilidad**
   - Configuración centralizada
   - Nombres de funciones descriptivos
   - Comentarios JSDoc

## Configuración

Todas las configuraciones están en `js/config/constants.js`:

```javascript
const Config = {
    API_BASE: 'http://localhost:8080/api',
    WS_URL: 'ws://localhost:8080/live-fight',
    POLLING_INTERVAL: 15000,
    STATS_UPDATE_INTERVAL: 30000,
    // ...
};
```

## Uso

1. Asegúrate de que el backend esté corriendo en `http://localhost:8080`
2. Sirve los archivos estáticos desde `src/main/resources/static/`
3. Abre `index.html` en el navegador

## Notas de Desarrollo

- El código está preparado para TypeScript si se desea migrar
- Los componentes pueden convertirse fácilmente a React/Vue
- La estructura permite agregar un bundler (Webpack/Vite) sin cambios mayores
