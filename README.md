# UFC Live Tracker - Sistema de Seguimiento en Tiempo Real

## 📋 Descripción del Proyecto

UFC Live Tracker es una aplicación web distribuida que permite a los usuarios seguir el desarrollo de peleas de artes marciales mixtas (MMA) en tiempo real. El sistema proporciona estadísticas detalladas durante el transcurso de cada pelea, incluyendo golpes conectados, derribos, control de jaula y probabilidades dinámicas de victoria.

## 🏗️ Arquitectura del Sistema

### Arquitectura de Microservicios
- **API Gateway**: Punto único de entrada para todas las peticiones HTTP
- **Microservicios especializados**:
  - Gestión de usuarios y autenticación
  - Estadísticas de peleas
  - Gestión de luchadores
  - Gestión de eventos
  - Sistema de notificaciones

### Tecnologías de Comunicación
- **REST API**: Para operaciones CRUD estándar
- **WebSockets**: Para actualizaciones en tiempo real durante las peleas
- **RabbitMQ**: Para mensajería asíncrona y sistema de notificaciones

## 📁 Estructura del Proyecto

### Backend (Spring Boot)

#### Configuración Principal
- `src/main/java/org/example/ufc_api/UfcApiApplication.java` - Punto de entrada de la aplicación
- `Application.yml` - Configuración de Spring Boot, base de datos, RabbitMQ y email
- `build.gradle` - Dependencias del proyecto

#### Paquete de Configuración (`/config`)
- `RabbitMQConfig.java` - Configuración de exchanges, queues y bindings para mensajería
- `SecurityConfig.java` - Configuración de seguridad y autenticación HTTP Basic
- `WebSocketConfig.java` - Configuración del servidor WebSocket
- `ModelMapperConfig.java` - Mapeo entre DTOs y entidades
- `TimeZoneConfig.java` - Configuración de zona horaria y serialización de fechas
- `CorsConfig.java` - Políticas de origen cruzado

#### Controladores REST (`/controller`)
- `UsuarioController.java` - Gestión de usuarios (CRUD, cambio de roles)
- `AuthController.java` - Autenticación y login
- `EventoController.java` - Gestión de eventos UFC
- `LuchadorController.java` - Gestión de luchadores
- `PeleaController.java` - Gestión de peleas (incluye endpoints para peleas en vivo)
- `EstadisticaController.java` - Gestión de estadísticas de combate
- `ProbabilidadController.java` - Gestión de probabilidades de victoria
- `ResultadoController.java` - Gestión de resultados de peleas

#### Controladores de Debug y Administración
- `DebugWebSocketController.java` - Herramientas de debug para WebSocket
- `FightSwitchController.java` - Control administrativo de peleas activas
- `WebSocketTestController.java` - Testing de funcionalidad WebSocket

#### Modelos JPA (`/model`)
- `Usuario.java` - Entidad de usuario con roles
- `Evento.java` - Entidad de eventos UFC
- `Luchador.java` - Entidad de luchadores con historial
- `Pelea.java` - Entidad de peleas con relaciones a evento y luchadores
- `Estadistica.java` - Estadísticas por round (golpes, derribos, control)
- `Probabilidad.java` - Probabilidades de victoria en tiempo real
- `Resultado.java` - Resultados finales de peleas

#### DTOs (`/dto`)
- `UsuarioDto.java` - DTO para transferencia de datos de usuario
- `EventoDto.java` - DTO de eventos
- `LuchadorDto.java` - DTO de luchadores
- `PeleaDto.java` - DTO de peleas
- `EstadisticaDto.java` - DTO de estadísticas
- `ProbabilidadDto.java` - DTO de probabilidades
- `ResultadoDto.java` - DTO de resultados
- `EmailNotification.java` - DTO para notificaciones por email
- `UsuarioRegistroEvent.java` - Evento de registro de usuario

#### Servicios (`/service`)
Interfaces:
- `UsuarioService.java`, `EventoService.java`, `LuchadorService.java`, `PeleaService.java`
- `EstadisticaService.java`, `ProbabilidadService.java`, `ResultadoService.java`

Implementaciones (`/service/impl`):
- `UsuarioServiceImpl.java` - Lógica de negocio de usuarios con publicación de eventos
- `EventoServiceImpl.java` - Gestión de eventos
- `LuchadorServiceImpl.java` - Gestión de luchadores
- `PeleaServiceImpl.java` - Lógica compleja de clasificación de peleas
- `EstadisticaServiceImpl.java` - Cálculo de estadísticas
- `ProbabilidadServiceImpl.java` - Gestión de probabilidades
- `ResultadoServiceImpl.java` - Gestión de resultados

Servicios especializados:
- `EmailService.java` - Envío de emails con templates HTML
- `MessagePublisher.java` - Publicación de mensajes en RabbitMQ

#### Repositorios JPA (`/repository`)
- `UsuarioRepository.java` - Acceso a datos de usuarios
- `EventoRepository.java` - Acceso a datos de eventos
- `LuchadorRepository.java` - Acceso a datos de luchadores
- `PeleaRepository.java` - Queries personalizadas para peleas (en vivo, futuras, finalizadas)
- `EstadisticaRepository.java` - Queries para estadísticas agregadas
- `ProbabilidadRepository.java` - Queries para últimas probabilidades
- `ResultadoRepository.java` - Acceso a resultados

#### WebSocket (`/websocket`)
- `LiveFightHandler.java` - Handler principal para transmisión en tiempo real
- `SimpleWebSocketHandler.java` - Handler básico para testing
- `FightStats.java` - Modelo de estadísticas para WebSocket
- `FightUpdateMessage.java` - Mensaje de actualización para clientes

#### Listeners RabbitMQ (`/listener`)
- `UsuarioEventListener.java` - Escucha eventos de registro y dispara emails
- `EmailNotificationListener.java` - Procesa notificaciones de email

### Frontend (HTML5/CSS3/JavaScript)

#### Estructura de archivos (`src/main/resources/static/`)

##### HTML Principal
- `index.html` - Página principal con estructura de la aplicación

##### CSS (`/css`)
- `main.css` - Estilos globales
- `/components/`:
  - `auth.css` - Estilos del modal de autenticación
  - `dashboard.css` - Estilos del dashboard principal
  - `events.css` - Estilos de tarjetas de eventos
  - `live-fights.css` - Estilos de vista de peleas en vivo
- `/utilities/`:
  - `animations.css` - Animaciones y transiciones
  - `responsive.css` - Media queries para diseño responsivo

##### JavaScript (`/js`)

**Archivo principal:**
- `app.js` - Punto de entrada y orquestador de la aplicación

**Configuración (`/config`):**
- `constants.js` - Configuración global, estado de la aplicación y constantes

**Servicios (`/services`):**
- `api.service.js` - Servicio base para llamadas HTTP autenticadas
- `auth.service.js` - Manejo de autenticación y sesiones
- `event.service.js` - Operaciones CRUD de eventos y luchadores
- `fight.service.js` - Gestión de peleas y estadísticas
- `websocket.service.js` - Conexión y manejo de WebSocket

**Componentes (`/components`):**
- `auth.component.js` - Componente de login/registro
- `dashboard.component.js` - Vista principal con estadísticas
- `events.component.js` - Gestión y visualización de eventos
- `live-fight.component.js` - Vista de pelea en vivo con actualizaciones

**Utilidades (`/utils`):**
- `date.utils.js` - Formateo y parsing de fechas
- `dom.utils.js` - Manipulación del DOM
- `toast.utils.js` - Sistema de notificaciones toast

### Configuración de Infraestructura

#### Docker
- `compose.yaml` - Configuración de contenedores para PostgreSQL y RabbitMQ

#### Base de Datos
- PostgreSQL con las siguientes tablas:
  - `usuario` - Usuarios del sistema
  - `evento` - Eventos UFC
  - `luchador` - Información de luchadores
  - `pelea` - Peleas programadas
  - `estadistica` - Estadísticas por round
  - `probabilidad` - Probabilidades en tiempo real
  - `resultado` - Resultados finales

## 🚀 Características Principales

### Sistema de Usuarios
- Registro con validación y email de bienvenida
- Autenticación HTTP Basic
- Roles: usuario y administrador
- Gestión de sesiones en frontend

### Seguimiento en Tiempo Real
- WebSocket para actualizaciones instantáneas
- Polling como respaldo
- Estadísticas en vivo: golpes, derribos, control de jaula
- Probabilidades dinámicas de victoria

### Sistema de Notificaciones
- Email de bienvenida mediante RabbitMQ
- Templates HTML personalizados
- Procesamiento asíncrono

### Interfaz de Usuario
- Dashboard con estadísticas generales
- Vista de eventos con estado de peleas
- Vista especializada de pelea en vivo
- Diseño completamente responsivo
- Sistema de notificaciones toast

## 🔧 Configuración y Ejecución

### Requisitos
- Java 21
- PostgreSQL
- RabbitMQ
- Node.js (para servir archivos estáticos)

### Variables de Entorno Principales
```yaml
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/ufc_tracker
spring.datasource.username=ufc_user
spring.datasource.password=ufc_pass

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.username=myuser
spring.rabbitmq.password=secret

# Email (Gmail)
spring.mail.username=tu_email@gmail.com
spring.mail.password=tu_app_password
```

### Ejecución
1. Levantar servicios con Docker: `docker-compose up -d`
2. Ejecutar aplicación Spring Boot: `./gradlew bootRun`
3. Acceder a: `http://localhost:8080`

## 📊 Endpoints Principales

### Autenticación
- `POST /api/usuarios` - Registro de usuario
- `POST /api/auth/login` - Login
- `GET /api/auth/verify` - Verificar autenticación

### Gestión de Datos
- `/api/eventos` - CRUD de eventos
- `/api/luchadores` - CRUD de luchadores
- `/api/peleas` - CRUD de peleas
- `/api/peleas/live` - Peleas en vivo
- `/api/estadisticas` - Estadísticas de combate
- `/api/probabilidades` - Probabilidades de victoria

### WebSocket
- `ws://localhost:8080/live-fight` - Conexión para actualizaciones en vivo

### Administración
- `/api/debug/*` - Herramientas de debug
- `/api/fight-control/*` - Control de peleas activas
- `/api/test/*` - Endpoints de testing

## 👥 Equipo de Desarrollo

- Juan Ignacio Zangaro
- Mateo Guasch
- Felipe Guasch
- Tomás Tarigo

## 📝 Licencia

Proyecto académico desarrollado para el curso de Sistemas Distribuidos - Primer Semestre 2025.
