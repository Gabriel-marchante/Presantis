<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';
import { Trash2 } from 'lucide-vue-next';

const festivos = ref([]);
const loading = ref(true);

const nuevoFestivo = ref({
  nombre: '',
  fecha: '',
  tipo: 'EMPRESA'
});
const loadingCrear = ref(false);

const fetchFestivos = async () => {
  try {
    const res = await api.get('/festivos');
    festivos.value = res.data;
  } catch (error) {
    console.error("Error cargando festivos:", error);
  } finally {
    loading.value = false;
  }
};

const crearFestivo = async () => {
  if(!nuevoFestivo.value.nombre || !nuevoFestivo.value.fecha) return;
  loadingCrear.value = true;
  try {
    const res = await api.post('/festivos', nuevoFestivo.value);
    festivos.value.push(res.data);
    nuevoFestivo.value.nombre = '';
    nuevoFestivo.value.fecha = '';
  } catch (err) {
    console.error(err);
    alert('Error al crear festivo');
  } finally {
    loadingCrear.value = false;
  }
};

const borrarFestivo = async (id) => {
  if(!confirm("¿Borrar este festivo?")) return;
  try {
    await api.delete(`/festivos/${id}`);
    festivos.value = festivos.value.filter(f => f.id !== id);
  } catch (err) {
    console.error(err);
    alert('Error al borrar festivo');
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleDateString('es-ES', { 
    year: 'numeric', month: 'long', day: 'numeric'
  });
};

onMounted(() => {
  fetchFestivos();
});
</script>

<template>
  <div class="page-content">
    <div class="page-header">
      <h2>Calendario de Festivos</h2>
      <p class="text-subtitle">Gestiona días especiales y festivos de empresa</p>
    </div>

    <div class="grid-layout">
      <!-- Create Box -->
      <div class="glass-panel p-6 h-fit">
        <h3>Añadir Festivo</h3>
        <form @submit.prevent="crearFestivo" class="mt-4 form-group">
          <div class="form-group">
            <label class="form-label">Nombre del Festivo</label>
            <input v-model="nuevoFestivo.nombre" type="text" class="form-input" required placeholder="Ej: Patrón local" />
          </div>
          
          <div class="form-group">
            <label class="form-label">Fecha</label>
            <input v-model="nuevoFestivo.fecha" type="date" class="form-input" required />
          </div>

          <div class="form-group">
            <label class="form-label">Tipo</label>
            <select v-model="nuevoFestivo.tipo" class="form-input">
              <option value="NACIONAL">Nacional</option>
              <option value="AUTONOMICO">Autonómico</option>
              <option value="LOCAL">Local</option>
              <option value="EMPRESA">Empresa</option>
            </select>
          </div>

          <button type="submit" class="btn btn-primary mt-2" :disabled="loadingCrear">
            {{ loadingCrear ? 'Creando...' : 'Añadir al calendario' }}
          </button>
        </form>
      </div>

      <!-- List Box -->
      <div class="table-card glass-panel">
        <div v-if="loading" class="loading-state">Cargando...</div>
        
        <div v-else class="table-container">
          <table class="table">
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Nombre</th>
                <th>Tipo</th>
                <th class="text-right">Ajustes</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="festivos.length === 0">
                <td colspan="4" class="empty-state">No hay festivos registrados.</td>
              </tr>
              <tr v-for="f in festivos" :key="f.id">
                <td class="font-medium date-text">{{ formatDate(f.fecha) }}</td>
                <td>{{ f.nombre }}</td>
                <td><span class="badge badge-pending">{{ f.tipo }}</span></td>
                <td class="text-right">
                  <button @click="borrarFestivo(f.id)" class="btn-icon">
                    <Trash2 size="18" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  margin-bottom: 2rem;
}

.text-subtitle {
  color: var(--text-muted);
  margin-top: 0.5rem;
}

.grid-layout {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 2rem;
}

@media (max-width: 1024px) {
  .grid-layout {
    grid-template-columns: 1fr;
  }
}

.p-6 {
  padding: 1.5rem;
}

.h-fit {
  height: fit-content;
}

.mt-4 { margin-top: 1rem; }
.mt-2 { margin-top: 0.5rem; }

.table-card {
  padding: 0;
  overflow: hidden;
}

select.form-input {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 1rem center;
  background-size: 1em;
}

.date-text {
  color: var(--primary);
}

.text-right {
  text-align: right !important;
}

.btn-icon {
  background: transparent;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  padding: 0.5rem;
  border-radius: var(--radius-md);
  transition: all 0.2s;
}

.btn-icon:hover {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger);
}

.font-medium { font-weight: 500; }
.empty-state { text-align: center; padding: 3rem; color: var(--text-muted); }
.loading-state { padding: 3rem; text-align: center; color: var(--text-muted); }
</style>
