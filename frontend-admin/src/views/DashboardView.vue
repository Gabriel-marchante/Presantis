<script setup>
import { ref, onMounted } from 'vue';
import { Users, Clock, CalendarDays, AlertTriangle, FileText } from 'lucide-vue-next';
import api from '../services/api';
import StatCard from '../components/StatCard.vue';
import StatusBadge from '../components/StatusBadge.vue';
import FichajeWidget from '../components/FichajeWidget.vue';
import HistorialJornadasWidget from '../components/HistorialJornadasWidget.vue';
import { useAuthStore } from '../store/auth';

const authStore = useAuthStore();
const loading = ref(true);

const adminStats = ref({
  empleadosActivos: 0,
  totalHorasHoy: 0,
  solicitudesPendientes: 0,
  incidencias: 0
});

const empStats = ref({
  horasSemanales: 0,
  diasTrabajados: 0,
  solicitudesPropias: 0,
  incidenciasPropias: 0
});

const presencias = ref([]);

const fetchDashboardData = async () => {
  try {
    if (authStore.isAdmin) {
      const resPresencia = await api.get('/admin/presencia');
      presencias.value = resPresencia.data || [];
      const resStats = await api.get('/admin/dashboard-stats');
      adminStats.value = resStats.data;
    } else {
      const resStats = await api.get('/jornadas/stats/me');
      empStats.value = resStats.data;
    }
  } catch (error) {
    console.error("Error cargando dashboard:", error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const d = new Date(dateString);
  return d.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
};

onMounted(() => {
  fetchDashboardData();
});
</script>

<template>
  <div class="page-content">
    <header class="page-header">
      <div>
        <h2>Dashboard de Presantis</h2>
        <p class="subtitle">Resumen de actividad en tiempo real</p>
      </div>
      <button @click="fetchDashboardData" class="btn btn-outline" :class="{ 'spinning': loading }">
        ↻ Actualizar
      </button>
    </header>

    <!-- ADMIN LAYOUT -->
    <template v-if="authStore.isAdmin">
      <div class="dashboard-horizontal-section mb-2">
        <div class="fichaje-container-side">
          <FichajeWidget />
        </div>
        
        <div class="stats-grid-side">
          <StatCard title="Empleados Activos" :value="adminStats.empleadosActivos" :icon="Users" trend="up" color="primary" />
          <StatCard title="Horas Totales Hoy" :value="adminStats.totalHorasHoy + 'h'" :icon="Clock" color="success" />
          <StatCard title="Solicitudes" :value="adminStats.solicitudesPendientes" :icon="CalendarDays" trend="down" color="warning" />
          <StatCard title="Incidencias" :value="adminStats.incidencias" :icon="AlertTriangle" color="danger" />
        </div>
      </div>

      <!-- Active Employees Table -->
      <div class="table-card glass-panel mt-8">
        <div class="card-header">
          <h3>Empleados en activo (Tiempo real)</h3>
        </div>
        
        <div v-if="loading" class="loading-state">Cargando datos...</div>
        
        <div v-else class="table-container">
          <table class="table">
            <thead>
              <tr>
                <th>Empleado</th>
                <th>Hora Inicio</th>
                <th>Ubicación</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="presencias.length === 0">
                <td colspan="4" class="empty-state">No hay empleados trabajando activamente en este momento.</td>
              </tr>
              <tr v-for="j in presencias" :key="j.id">
                <td>
                  <div class="employee-cell">
                    <div class="avatar-sm">{{ j.usuario?.nombre?.charAt(0) || 'U' }}</div>
                    <div>
                      <span class="d-block font-medium">{{ j.usuario?.nombre || 'Usuario' }} {{ j.usuario?.apellidos || 'Desconocido' }}</span>
                      <span class="d-block text-s text-muted">{{ j.usuario?.email }}</span>
                    </div>
                  </div>
                </td>
                <td>{{ formatDate(j.fechaInicio) }}</td>
                <td>
                  <span class="location-chip">{{ j.ubicacionInicio }}</span>
                </td>
                <td>
                  <StatusBadge :status="j.estado" />
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>

    <!-- EMPLOYEE LAYOUT -->
    <template v-else>
      <div class="dashboard-horizontal-section mb-2">
        <div class="fichaje-container-side">
          <FichajeWidget />
        </div>

        <div class="stats-grid-side">
          <StatCard title="Horas Semanales" :value="empStats.horasSemanales + 'h'" :icon="Clock" color="success" />
          <StatCard title="Días Trabajados" :value="empStats.diasTrabajados" :icon="CalendarDays" color="primary" />
          <StatCard title="Mis Solicitudes" :value="empStats.solicitudesPropias" :icon="FileText" color="warning" />
          <StatCard title="Incidencias" :value="empStats.incidenciasPropias" :icon="AlertTriangle" color="danger" />
        </div>
      </div>

      <div class="history-container mt-4">
        <HistorialJornadasWidget />
      </div>
    </template>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

/* Horizontal Layout Fix for Dashboard */
.dashboard-horizontal-section {
  display: flex;
  gap: 1.5rem;
  align-items: stretch;
}

.fichaje-container-side {
  flex: 0 0 320px;
}

.stats-grid-side {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.mb-2 {
  margin-bottom: 2rem;
}
.mt-4 {
  margin-top: 1rem;
}

.mt-8 {
  margin-top: 2rem;
}

.table-card {
  padding: 0;
  overflow: hidden;
}

.card-header {
  padding: 1.5rem;
  border-bottom: 1px solid var(--border);
}

.card-header h3 {
  margin: 0;
}

.employee-cell {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.avatar-sm {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.8rem;
}

.d-block { display: block; }
.font-medium { font-weight: 500; }
.text-s { font-size: 0.8rem; }
.text-muted { color: var(--text-muted); }

.location-chip {
  background: rgba(255, 255, 255, 0.1);
  padding: 0.2rem 0.6rem;
  border-radius: var(--radius-md);
  font-size: 0.85rem;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: var(--text-muted);
}

.loading-state {
  padding: 3rem;
  text-align: center;
  color: var(--text-muted);
}

.spinning {
  opacity: 0.5;
  pointer-events: none;
}

@media (max-width: 1024px) {
  .dashboard-horizontal-section {
    flex-direction: column;
  }
  .fichaje-container-side {
    flex: auto;
    width: 100%;
  }
}
</style>
