<div align="center">

# 🕐 PRESANTIS

### Plataforma de Control Horario Laboral

*Gestión de jornadas, vacaciones y presencia adaptada a la normativa española — accesible desde cualquier dispositivo.*

![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Quarkus](https://img.shields.io/badge/Quarkus-4695EB?style=for-the-badge&logo=quarkus&logoColor=white)
![Flutter](https://img.shields.io/badge/Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

---

## 📖 Descripción

**Presantis** es una plataforma completa de control horario laboral diseñada para cumplir con la normativa española de registro de jornada. Permite a los empleados fichar desde cualquier dispositivo, gestionar sus vacaciones y consultar su historial, mientras que los administradores disponen de un panel completo para supervisar la presencia, aprobar solicitudes y gestionar los cambios con trazabilidad total.

---

## 🏗️ Arquitectura

```
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│   App Móvil     │   │  Web Empleados  │   │  Panel Admin    │
│  Flutter/Dart   │   │    Vue.js       │   │    Vue.js       │
└────────┬────────┘   └────────┬────────┘   └────────┬────────┘
         │                     │                     │
         └─────────────────────┼─────────────────────┘
                               ↓  REST + JWT
              ┌────────────────────────────────┐
              │     Backend — Quarkus (Java)    │
              │                                │
              │  · Auth JWT (MicroProfile)      │
              │  · Gestión de jornadas          │
              │  · Control de vacaciones        │
              │  · Sistema de solicitudes       │
              │  · Notificaciones               │
              │  · Trazabilidad de cambios      │
              └───────────────┬────────────────┘
                              ↓
              ┌────────────────────────────────┐
              │         PostgreSQL              │
              │  Usuarios · Jornadas · Pausas  │
              │  Vacaciones · Solicitudes · Logs│
              └────────────────────────────────┘
```

---

## ✨ Funcionalidades

### 👷 Empleado — App Móvil & Web
- 📍 **Inicio de jornada** — selección de ubicación desde opciones predefinidas (oficina, teletrabajo, cliente...)
- ☕ **Pausas** — registro con motivo predefinido (descanso, comida, gestión...)
- 🏁 **Fin de jornada** — con justificación de ubicación
- 📋 **Historial de jornadas** — vista diaria, semanal, mensual y anual
- ✏️ **Solicitud de correcciones** — el empleado indica qué quiere cambiar y por qué, se envía al admin
- 📅 **Calendario** — visualización de festivos nacionales, festivos de empresa y días libres personales
- 🏖️ **Solicitud de vacaciones y días libres** — directamente desde el calendario
- 🔔 **Notificaciones** — cuando el admin aprueba, rechaza o modifica algo

### 🛠️ Administrador — Panel Web
- 👁️ **Presencia en tiempo real** — quién está trabajando ahora y quién no
- 📊 **Estadísticas globales** — por empleado, equipo, mes y año
- ✅ **Gestión de solicitudes** — aprobar o rechazar correcciones y vacaciones
- 🔧 **Modificación de jornadas** — con registro automático del cambio y notificación al empleado
- 📝 **Trazabilidad total** — todo cambio queda registrado (quién, cuándo y por qué)
- 🗓️ **Gestión del calendario** — festivos de empresa y días especiales
- 👥 **Gestión de empleados** — alta, baja y configuración de perfiles

### 📋 Trazabilidad y Cumplimiento Legal
- Registro inmutable de todas las jornadas
- Log de modificaciones con solicitante, motivo y aprobador
- Adaptado al **Real Decreto-ley 8/2019** de registro de jornada
- Exportación de registros para inspección de trabajo

---

## 🛠️ Tecnologías

### Backend
![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Quarkus](https://img.shields.io/badge/Quarkus-4695EB?style=flat-square&logo=quarkus&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white)

### Frontend & Mobile
![Flutter](https://img.shields.io/badge/Flutter-02569B?style=flat-square&logo=flutter&logoColor=white)
![Dart](https://img.shields.io/badge/Dart-0175C2?style=flat-square&logo=dart&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=flat-square&logo=vue.js&logoColor=white)

### Infraestructura
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=flat-square&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)

---

## 🚀 Instalación

### Requisitos previos
- Java 17+
- Docker y Docker Compose
- Flutter SDK
- Node.js v18+

### Arrancar el sistema

```bash
# 1. Clona el repositorio
git clone https://github.com/Gabriel-marchante/Presantis.git
cd Presantis

# 2. Levanta el backend y PostgreSQL
docker compose up -d

# 3. Accede al panel de administración
http://localhost:8080/admin

# 4. Accede a la web de empleados
http://localhost:8080
```

### App móvil

```bash
cd mobile
flutter pub get
flutter run
```

---

## 📁 Estructura del proyecto

```
Presantis/
├── backend/                  # Quarkus (Java)
│   ├── src/main/java/
│   │   ├── model/            # Entidades JPA
│   │   ├── resource/         # Endpoints REST
│   │   └── service/          # Lógica de negocio
│   └── src/main/resources/
│       └── db/migration/     # Scripts Flyway
├── frontend-admin/           # Panel admin (Vue.js)
├── frontend-web/             # Web empleados (Vue.js)
├── mobile/                   # App móvil (Flutter)
└── docker-compose.yml
```

---

## 📋 Normativa

Presantis está diseñado para cumplir con:
- **Real Decreto-ley 8/2019** — obligatoriedad del registro de jornada
- **Estatuto de los Trabajadores** — art. 34.9
- **LOPDGDD** — protección de datos de los empleados

---

## 👨‍💻 Autor

**Gabriel Marchante Bañuls**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/gabriel-m-833856242/)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/Gabriel-marchante)

---

<div align="center">
  <i>Tu tiempo, bien registrado. ⏱️</i>
</div>
