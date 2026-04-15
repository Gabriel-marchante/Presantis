<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';
import StatCard from '../components/StatCard.vue';
import StatusBadge from '../components/StatusBadge.vue';
import { Users, UserX, Clock, Building } from 'lucide-vue-next';

const kpis = ref({
  totalEmpleados: 12,
  trabajandoAhora: 0,
  ausentes: 0,
  horasSemanales: 340
});

const presencias = ref([]);
const loading = ref(true);

const fetchDashboardData = async () => {
  try {
    const res = await api.get('/admin/presencia');
    presencias.value = res.data;
    
    // Calcular KPIs (simplificado)
    kpis.value.trabajandoAhora = res.data.length;
    kpis.value.ausentes = kpis.value.totalEmpleados - kpis.value.trabajandoAhora;
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
    <div class="page-header">
      <h2>Dashboard de Presencia</h2>
      <button @click="fetchDashboardData" class="btn btn-outline" :class="{ 'spinning': loading }">
        ↻ Actualizar
      </button>
    </div>

    <!-- KPI Row -->
    <div class="kpi-grid">
      <StatCard 
        title="Trabajando Ahora" 
        :value="kpis.trabajandoAhora" 
        :icon="Users"
        trend="+2"
        :trendPositive="true"
      />
      <StatCard 
        title="Ausentes" 
        :value="kpis.ausentes" 
        :icon="UserX"
        trend="-1"
        :trendPositive="true"
      />
      <StatCard 
        title="Horas Semanales" 
        :value="kpis.horasSemanales" 
        :icon="Clock"
        trend="+14h"
        :trendPositive="true"
      />
      <StatCard 
        title="Departamentos" 
        value="4" 
        :icon="Building"
      />
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
                  <div class="avatar-sm">{{ j.usuario.nombre.charAt(0) }}</div>
                  <div>
                    <span class="d-block font-medium">{{ j.usuario.nombre }} {{ j.usuario.apellidos }}</span>
                    <span class="d-block text-s text-muted">{{ j.usuario.email }}</span>
                  </div>
                </div>
              </td>
              <td>{{ formatDate(j.fechaInicio) }}</td>
              <td>
                <span class="location-chip">
                  {{ j.ubicacionInicio }}
                </span>
              </td>
              <td>
                <StatusBadge :status="j.estado" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
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
</style>
