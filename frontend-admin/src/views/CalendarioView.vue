<script setup>
import { ref, onMounted, computed } from 'vue';
import { Calendar, CheckCircle2, AlertCircle, Plus, ChevronLeft, ChevronRight } from 'lucide-vue-next';
import { useRouter } from 'vue-router';
import api from '../services/api';
import ModalSolicitar from '../components/ModalSolicitar.vue';

const router = useRouter();

// Mock data (Pending Backend endpoint)
const stats = ref({
  diasConsumidos: 0,
  diasTotales: 22,
  diasPendientesDeAprobar: 0
});

// For empty state testing, leave as empty or fetch
const festivos = ref([]); // As requested: "si pone 0 que lo ponga, y si aparece vacio poniendo No hay dias festivos"

const vacacionesAprobadas = ref([]);

const fetchFestivos = async () => {
  try {
    const [resFestivos, resVacaciones] = await Promise.all([
      api.get('/festivos'),
      api.get('/vacaciones/todas')
    ]);
    festivos.value = resFestivos.data;
    // Filtrar solo las aprobadas para marcarlas en el calendario visual
    vacacionesAprobadas.value = resVacaciones.data.filter(v => v.estado === 'APROBADA');
  } catch (error) {
    console.error("Error al cargar datos del calendario:", error);
  }
};

const currentDate = ref(new Date());
const isModalOpen = ref(false);

const currentMonthName = computed(() => {
  return currentDate.value.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' });
});

onMounted(() => {
  fetchFestivos();
});

// Calendar Grid Logic
const daysInMonth = computed(() => {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  
  const days = new Date(year, month + 1, 0).getDate();
  const firstDayIndex = new Date(year, month, 1).getDay();
  // Adjust to make Monday the first day (0 = Mon, 6 = Sun)
  const startDay = firstDayIndex === 0 ? 6 : firstDayIndex - 1;

  const daysArray = [];

  // Padding days
  for (let i = 0; i < startDay; i++) {
    daysArray.push({ id: `empty-${i}`, empty: true });
  }

  // Actual days
  for (let i = 1; i <= days; i++) {
    const dDate = new Date(year, month, i);
    // Determine if it's a festivo
    const isFestivo = festivos.value.find(f => {
      const fDate = new Date(f.fecha);
      return fDate.getFullYear() === year && fDate.getMonth() === month && fDate.getDate() === i;
    });

    // Validar si el día cae en una vacación (Rango Inicio - Fin)
    const isVacacion = vacacionesAprobadas.value.find(v => {
      const start = new Date(v.fechaInicio);
      const end = new Date(v.fechaFin);
      return dDate >= start && dDate <= end;
    });

    daysArray.push({
      id: `day-${i}`,
      number: i,
      empty: false,
      isToday: new Date().toDateString() === dDate.toDateString(),
      type: isFestivo ? isFestivo.tipo : (isVacacion ? 'VACACION' : null)
    });
  }

  return daysArray;
});

const prevMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1);
};

const nextMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1);
};

const openModal = () => {
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

</script>

<template>
  <div class="page-content">
    <ModalSolicitar :isOpen="isModalOpen" @close="closeModal" />
    
    <header class="page-header">
      <div>
        <h2>Calendario Laboral</h2>
        <p class="subtitle">Gestiona tus vacaciones y revisa los días festivos de la empresa</p>
      </div>
      <button @click="openModal" class="btn btn-primary">
        <Plus size="18" />
        Solicitar Vacaciones
      </button>
    </header>

    <div class="calendario-layout">
      <!-- CALENDAR WIDGET -->
      <div class="calendar-container glass-panel">
        <div class="calendar-header">
          <button class="btn btn-icon" @click="prevMonth"><ChevronLeft size="20" /></button>
          <h3 class="capitalize">{{ currentMonthName }}</h3>
          <button class="btn btn-icon" @click="nextMonth"><ChevronRight size="20" /></button>
        </div>
        
        <div class="calendar-grid-wrapper">
          <div class="weekdays">
            <span>Lun</span><span>Mar</span><span>Mié</span><span>Jue</span><span>Vie</span><span>Sáb</span><span>Dom</span>
          </div>
          <div class="days-grid">
            <div 
              v-for="day in daysInMonth" 
              :key="day.id" 
              class="day-cell"
              :class="{ 
                'empty': day.empty, 
                'today': day.isToday,
                'estatal': day.type === 'ESTATAL',
                'empresa': day.type === 'EMPRESA',
                'vacacion': day.type === 'VACACION'
              }"
            >
              <span v-if="!day.empty">{{ day.number }}</span>
            </div>
          </div>
        </div>
        
        <div class="calendar-legend">
          <div class="legend-item">
            <span class="dot estatal"></span> Festivo Estatal
          </div>
          <div class="legend-item">
            <span class="dot empresa"></span> Festivo de Empresa
          </div>
          <div class="legend-item">
            <span class="dot vacacion"></span> Día Solicitado
          </div>
        </div>
      </div>

      <!-- RIGHT SIDEBAR: CUOTA & FESTIVOS LIST -->
      <div class="sidebar-container">
        
        <div class="cuota-widget glass-panel">
          <div class="cuota-header">
            <h3>Cuota de Vacaciones</h3>
          </div>
          <div class="cuota-circle">
            <div class="circle-content">
              <span class="number text-success">{{ stats.diasTotales - stats.diasConsumidos - stats.diasPendientesDeAprobar }}</span>
              <span class="label">Disponibles</span>
            </div>
            <svg viewBox="0 0 36 36" class="circular-chart">
              <path class="circle-bg"
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
              <path class="circle"
                stroke-dasharray="75, 100"
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
            </svg>
          </div>
          <div class="cuota-stats">
            <div class="cuota-stat">
              <CheckCircle2 size="16" class="text-primary" />
              <span>Totales: {{ stats.diasTotales }}</span>
            </div>
            <div class="cuota-stat">
              <CheckCircle2 size="16" class="text-success" />
              <span>Consumidos: {{ stats.diasConsumidos }}</span>
            </div>
            <div class="cuota-stat">
              <AlertCircle size="16" class="text-warning" />
              <span>Pendientes: {{ stats.diasPendientesDeAprobar }}</span>
            </div>
          </div>
        </div>

        <div class="festivos-lista glass-panel mt-4">
          <div class="cuota-header">
            <h3>Próximos Festivos</h3>
          </div>
          
          <div v-if="festivos.length === 0" class="empty-state">
            <Calendar size="32" color="rgba(255,255,255,0.2)" />
            <p>No hay dias festivos.</p>
          </div>

          <ul v-else class="festivos-ul">
            <li v-for="f in festivos" :key="f.id">
              <div class="f-date">
                <span class="f-day">{{ new Date(f.fecha).getDate() }}</span>
                <span class="f-month">{{ new Date(f.fecha).toLocaleDateString('es-ES', { month: 'short' }) }}</span>
              </div>
              <div class="f-info">
                <strong>{{ f.nombre }}</strong>
                <span class="badge" :class="f.tipo.toLowerCase()">{{ f.tipo }}</span>
              </div>
            </li>
          </ul>
        </div>
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

.calendario-layout {
  display: flex;
  gap: 2rem;
  align-items: flex-start;
}

.calendar-container {
  flex: 2;
  padding: 1.5rem;
  min-height: 500px;
  display: flex;
  flex-direction: column;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}
.calendar-header h3 { margin: 0; font-size: 1.5rem; }
.capitalize { text-transform: capitalize; }
.btn-icon { background: rgba(255,255,255,0.05); border: none; color: white; padding: 0.5rem; border-radius: 8px; cursor: pointer; }
.btn-icon:hover { background: rgba(255,255,255,0.1); }

/* GRID CSS FOR CALENDAR */
.calendar-grid-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-bottom: 1.5rem;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-weight: 600;
  color: var(--text-dim);
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.5rem;
  flex: 1;
}

.day-cell {
  background: rgba(0,0,0,0.2);
  border: 1px solid rgba(255,255,255,0.02);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  font-weight: 500;
  min-height: 100px;
  cursor: pointer;
  transition: all 0.2s;
}

.day-cell:not(.empty):hover {
  background: rgba(0, 240, 255, 0.1);
  border-color: rgba(0, 240, 255, 0.3);
}

.day-cell.empty {
  background: transparent;
  border-color: transparent;
  cursor: default;
}

.day-cell.today {
  border: 2px solid var(--primary-col);
  box-shadow: 0 0 10px rgba(0, 240, 255, 0.3);
}

/* Colors for specific days */
.day-cell.estatal { background: rgba(0, 150, 255, 0.2); color: #fff; border-color: rgba(0, 150, 255, 0.4);}
.day-cell.empresa { background: rgba(255, 170, 0, 0.2); color: #fff; border-color: rgba(255, 170, 0, 0.4);}
.day-cell.vacacion { background: rgba(0, 200, 83, 0.2); color: #fff; border-color: rgba(0, 200, 83, 0.4);}

.calendar-legend {
  display: flex;
  gap: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(255,255,255,0.05);
}
.legend-item { display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; }
.dot { width: 10px; height: 10px; border-radius: 50%; }
.dot.estatal { background: var(--info-col); }
.dot.empresa { background: var(--warning-col); }
.dot.vacacion { background: var(--success-col); }

.sidebar-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 320px;
}

.cuota-widget, .festivos-lista {
  padding: 1.5rem;
}

.cuota-header h3 {
  margin: 0 0 1.5rem 0;
  font-size: 1.1rem;
  text-align: center;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 0.8rem;
}

.cuota-circle {
  width: 150px;
  height: 150px;
  margin: 0 auto 2rem auto;
  position: relative;
}

.circle-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.circle-content .number {
  font-size: 2.8rem;
  font-weight: bold;
}

.circle-content .label {
  font-size: 0.8rem;
  color: var(--text-dim);
  text-transform: uppercase;
}

.circular-chart {
  display: block;
  margin: 0 auto;
  max-width: 100%;
  max-height: 250px;
}
.circle-bg {
  fill: none;
  stroke: rgba(255, 255, 255, 0.05);
  stroke-width: 2;
}
.circle {
  fill: none;
  stroke-width: 2.5;
  stroke-linecap: round;
  stroke: var(--success-col);
  animation: progress 1s ease-out forwards;
}

@keyframes progress {
  0% { stroke-dasharray: 0 100; }
}

.cuota-stats {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.cuota-stat {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  font-size: 0.95rem;
  padding: 0.5rem;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
}

.text-success { color: var(--success-col); }
.text-warning { color: var(--warning-col); }
.text-primary { color: var(--primary-col); }

.empty-state { text-align: center; padding: 2rem 0; color: var(--text-dim); display: flex; flex-direction: column; align-items: center; gap: 0.5rem; }
.empty-state p { margin: 0; }

.festivos-ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.festivos-ul li {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  border-left: 3px solid transparent;
}

.festivos-ul li:nth-child(even) { border-left-color: var(--info-col); }
.festivos-ul li:nth-child(odd) { border-left-color: var(--warning-col); }

.f-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.05);
  padding: 0.5rem;
  border-radius: 6px;
  min-width: 50px;
}
.f-day { font-size: 1.2rem; font-weight: bold; color: white;}
.f-month { font-size: 0.75rem; text-transform: uppercase; color: var(--text-dim); }

.f-info {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.f-info strong { font-size: 0.9rem; color: #fff; }

.badge {
  align-self: flex-start;
  font-size: 0.65rem;
  padding: 0.1rem 0.5rem;
  border-radius: 20px;
  font-weight: 600;
}
.badge.estatal { background: rgba(0, 150, 255, 0.2); color: var(--info-col); }
.badge.empresa { background: rgba(255, 170, 0, 0.2); color: var(--warning-col); }

.mt-4 { margin-top: 1rem; }

@media (max-width: 1024px) {
  .calendario-layout { flex-direction: column; }
  .sidebar-container, .calendar-container { width: 100%; flex: auto; }
}
</style>
