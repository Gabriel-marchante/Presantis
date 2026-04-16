<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { PlayCircle, Square, Coffee, RotateCcw } from 'lucide-vue-next';
import api from '../services/api';
import { useAuthStore } from '../store/auth';

const authStore = useAuthStore();
const activeJornada = ref(null);
const currentPause = ref(null);
const loading = ref(true);

const currentTime = ref(new Date());
let timeInterval;

const fetchActiveJornada = async () => {
  loading.value = true;
  try {
    const res = await api.get('/jornadas/activa');
    activeJornada.value = res.data;
    
    // Check if there is an active pause (a pause without a 'fin')
    if (activeJornada.value && activeJornada.value.estado === 'EN_PAUSA') {
       // We would need to fetch pauses or assume state.
       // The backend should ideally expose current pause or we just rely on state.
    }
  } catch (err) {
    if (err.response && err.response.status === 404) {
      activeJornada.value = null; // No active shift
    } else {
      console.error("Error fetching active jornada", err);
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchActiveJornada();
  timeInterval = setInterval(() => {
    currentTime.value = new Date();
  }, 1000);
});

onUnmounted(() => {
  clearInterval(timeInterval);
});

const formattedTime = computed(() => {
  return currentTime.value.toLocaleTimeString('es-ES', { 
    hour: '2-digit', minute: '2-digit', second: '2-digit' 
  });
});

const actionIcon = computed(() => {
  if (!activeJornada.value) return PlayCircle;
  if (activeJornada.value.estado === 'ACTIVA') return Square;
  return PlayCircle;
});

const iniciarJornada = async () => {
  try {
    await api.post('/jornadas/iniciar', { ubicacion: 'OFICINA' });
    fetchActiveJornada();
  } catch (err) {
    console.error("Error initiating", err);
  }
};

const finalizarJornada = async () => {
  if (!activeJornada.value) return;
  try {
    await api.post('/jornadas/finalizar', { ubicacion: 'OFICINA' });
    fetchActiveJornada();
  } catch (err) {
    console.error("Error finalizing", err);
  }
};

const handleMainAction = () => {
  if (!activeJornada.value) {
    iniciarJornada();
  } else if (activeJornada.value.estado === 'ACTIVA') {
    finalizarJornada();
  }
};
</script>

<template>
  <div class="fichaje-widget glass-panel">
    <div class="widget-header">
      <h3>Control de Presencia</h3>
      <div class="status-indicator">
        <span class="dot" :class="activeJornada ? 'active' : 'inactive'"></span>
        <span>{{ activeJornada ? activeJornada.estado.replace('_', ' ') : 'NO INICIADA' }}</span>
      </div>
    </div>
    
    <div class="clock-display">
      <div class="time">{{ formattedTime }}</div>
      <div class="date">{{ new Date().toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long' }) }}</div>
    </div>
    
    <div class="widget-actions" v-if="!loading">
      <button 
        @click="handleMainAction" 
        class="btn-fichar primary" 
        :class="{ 'en-curso': activeJornada?.estado === 'ACTIVA' }"
      >
        <component :is="activeJornada ? Square : PlayCircle" size="24" />
        <span>{{ !activeJornada ? 'Iniciar Jornada' : 'Finalizar Jornada' }}</span>
      </button>
    </div>
    
    <div v-else class="widget-actions loading">
      Cargando estado...
    </div>
  </div>
</template>

<style scoped>
.fichaje-widget {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background: linear-gradient(135deg, rgba(16, 20, 30, 0.9) 0%, rgba(30, 35, 50, 0.9) 100%);
  border-top: 3px solid var(--primary-col);
}

.widget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.widget-header h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--text-light);
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  font-weight: 600;
  padding: 0.3rem 0.75rem;
  border-radius: 20px;
  background: rgba(0, 0, 0, 0.2);
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.dot.active {
  background-color: var(--success-col);
  box-shadow: 0 0 8px var(--success-col);
}

.dot.inactive {
  background-color: var(--text-dim);
}

.clock-display {
  text-align: center;
  padding: 1rem 0;
}

.time {
  font-size: 3rem;
  font-weight: 300;
  letter-spacing: 2px;
  color: #fff;
  text-shadow: 0 0 15px rgba(0, 240, 255, 0.3);
  font-variant-numeric: tabular-nums;
}

.date {
  font-size: 0.9rem;
  color: var(--text-dim);
  text-transform: capitalize;
  margin-top: 0.5rem;
}

.widget-actions {
  display: flex;
  gap: 1rem;
}

.btn-fichar {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  color: white;
}

.btn-fichar.primary {
  background: linear-gradient(135deg, var(--primary-col) 0%, var(--primary-dark) 100%);
  box-shadow: 0 4px 15px rgba(0, 240, 255, 0.2);
}

.btn-fichar.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 240, 255, 0.4);
}

.btn-fichar.primary.en-curso {
  background: linear-gradient(135deg, var(--warning-col) 0%, #d89000 100%);
  box-shadow: 0 4px 15px rgba(255, 170, 0, 0.2);
}

.btn-fichar.primary.en-curso:hover {
  box-shadow: 0 6px 20px rgba(255, 170, 0, 0.4);
}

.btn-fichar.primary.en-pausa {
  background: linear-gradient(135deg, var(--info-col) 0%, #0077ff 100%);
  box-shadow: 0 4px 15px rgba(0, 150, 255, 0.2);
}

.btn-fichar.stop {
  background: linear-gradient(135deg, var(--danger-col) 0%, #b30000 100%);
  box-shadow: 0 4px 15px rgba(255, 60, 60, 0.2);
  flex: 0.7;
}

.btn-fichar.stop:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 60, 60, 0.4);
}

.widget-actions.loading {
  justify-content: center;
  color: var(--text-dim);
  padding: 1rem 0;
}
</style>
