<script setup>
import { ref, onMounted, watch } from 'vue';
import { Calendar, Clock, MapPin, Edit3 } from 'lucide-vue-next';
import api from '../services/api';

const props = defineProps({
  userId: {
    type: String,
    default: null
  }
});

const historial = ref([]);
const loading = ref(true);

const fetchHistorial = async () => {
  loading.value = true;
  try {
    const url = props.userId ? `/admin/usuarios/${props.userId}/jornadas` : '/jornadas/historial';
    const res = await api.get(url);
    historial.value = res.data;
  } catch (error) {
    console.error("Error fetching historial:", error);
  } finally {
    loading.value = false;
  }
};

watch(() => props.userId, () => {
  fetchHistorial();
});

onMounted(() => {
  fetchHistorial();
});

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const d = new Date(dateString);
  return d.toLocaleDateString('es-ES', { weekday: 'short', day: 'numeric', month: 'short' });
};

const formatTime = (dateString) => {
  if (!dateString) return '--:--';
  const d = new Date(dateString);
  return d.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
};

const getHorasTotales = (inicio, fin) => {
  if (!inicio || !fin) return '-';
  const dInicio = new Date(inicio);
  const dFin = new Date(fin);
  const diffMs = dFin - dInicio;
  const hours = Math.floor(diffMs / (1000 * 60 * 60));
  const mins = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
  return `${hours}h ${mins}m`;
};

const requestModification = (jornada) => {
  alert(`Funcionalidad en desarrollo: Crear Solicitud para modificar la jornada del ${formatDate(jornada.fechaInicio)}`);
};
</script>

<template>
  <div class="historial-widget glass-panel">
    <div class="widget-header">
      <div>
        <h3>Historial de Jornadas</h3>
        <p class="subtitle">Tus últimos registros de presencia</p>
      </div>
      <button @click="fetchHistorial" class="btn btn-outline btn-sm">↻</button>
    </div>

    <div v-if="loading" class="loading-state">
      Cargando historial...
    </div>

    <div v-else-if="historial.length === 0" class="empty-state">
      <Calendar size="40" color="rgba(255,255,255,0.2)" />
      <p>Aún no hay registros de jornadas.</p>
    </div>

    <div v-else class="historial-list">
      <div v-for="j in historial" :key="j.id" class="historial-item">
        
        <div class="date-col">
          <Calendar size="16" class="icon-muted" />
          <span class="font-medium capitalize">{{ formatDate(j.fechaInicio) }}</span>
        </div>

        <div class="time-col">
          <div class="time-block">
            <span class="text-xs text-muted">Entrada</span>
            <span class="text-sm">{{ formatTime(j.fechaInicio) }}</span>
          </div>
          <div class="time-divider"></div>
          <div class="time-block">
            <span class="text-xs text-muted">Salida</span>
            <span class="text-sm">{{ formatTime(j.fechaFin) }}</span>
          </div>
        </div>

        <div class="stats-col hide-mobile">
          <Clock size="16" class="icon-muted" />
          <span class="text-sm font-medium">{{ getHorasTotales(j.fechaInicio, j.fechaFin) }}</span>
        </div>

        <div class="location-col hide-mobile text-sm">
          <MapPin size="14" class="icon-info" />
          {{ j.ubicacionInicio }}
        </div>

        <div class="status-col">
           <span class="badge" :class="j.estado.toLowerCase()">{{ j.estado }}</span>
        </div>

        <div class="actions-col">
          <button @click="requestModification(j)" class="btn-icon" title="Solicitar Modificación">
            <Edit3 size="16" />
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
.historial-widget {
  padding: 1.5rem;
}

.widget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  padding-bottom: 1rem;
}

.widget-header h3 {
  margin: 0 0 0.2rem 0;
  font-size: 1.1rem;
}

.subtitle {
  margin: 0;
  font-size: 0.85rem;
  color: var(--text-dim);
}

.historial-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.historial-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.historial-item:hover {
  background: rgba(0, 0, 0, 0.3);
  border-color: rgba(0, 240, 255, 0.2);
}

.date-col {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  width: 140px;
}

.capitalize {
  text-transform: capitalize;
}

.time-col {
  display: flex;
  align-items: center;
  gap: 1rem;
  width: 160px;
}

.time-block {
  display: flex;
  flex-direction: column;
}

.time-divider {
  width: 15px;
  height: 2px;
  background: rgba(255, 255, 255, 0.1);
}

.stats-col, .location-col {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  width: 100px;
}

.actions-col {
  display: flex;
  justify-content: flex-end;
}

.badge {
  font-size: 0.75rem;
  padding: 0.25rem 0.6rem;
  border-radius: 12px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.1);
}

.badge.activa { background: rgba(0, 240, 255, 0.15); color: var(--primary-col); border: 1px solid rgba(0,240,255,0.3); }
.badge.finalizada { background: rgba(0, 200, 83, 0.15); color: var(--success-col); border: 1px solid rgba(0,200,83,0.3); }

.btn-icon {
  background: transparent;
  border: none;
  color: var(--text-dim);
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 6px;
  transition: all 0.2s;
}

.btn-icon:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.text-xs { font-size: 0.75rem; }
.text-sm { font-size: 0.9rem; }
.font-medium { font-weight: 500; }
.icon-muted { color: var(--text-dim); }
.icon-info { color: var(--primary-col); }

.empty-state, .loading-state {
  text-align: center;
  padding: 3rem;
  color: var(--text-dim);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

@media (max-width: 768px) {
  .hide-mobile { display: none; }
  .historial-item { flex-wrap: wrap; gap: 1rem; }
}
</style>
