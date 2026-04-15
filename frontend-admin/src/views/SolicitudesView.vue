<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';
import StatusBadge from '../components/StatusBadge.vue';

const solicitudes = ref([]);
const loading = ref(true);

const fetchSolicitudes = async () => {
  try {
    const res = await api.get('/solicitudes/todas');
    solicitudes.value = res.data;
  } catch (error) {
    console.error("Error cargando solicitudes:", error);
  } finally {
    loading.value = false;
  }
};

const responder = async (id, estado) => {
  try {
    await api.patch(`/solicitudes/${id}/responder`, { estado, respuesta: 'Revisado por admin' });
    // Actualizar localmente el estado de la solicitud
    const index = solicitudes.value.findIndex(s => s.id === id);
    if (index !== -1) {
      solicitudes.value[index].estado = estado;
    }
  } catch (err) {
    console.error(err);
    alert('Error al actualizar solicitud');
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleDateString('es-ES', { 
    year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  });
};

onMounted(() => {
  fetchSolicitudes();
});
</script>

<template>
  <div class="page-content">
    <div class="page-header">
      <h2>Gestión de Solicitudes</h2>
      <p class="text-subtitle">Revisa y aprueba las peticiones de corrección de jornadas</p>
    </div>

    <div class="table-card glass-panel">
      <div v-if="loading" class="loading-state">Cargando solicitudes...</div>
      
      <div v-else class="table-container">
        <table class="table">
          <thead>
            <tr>
              <th>Fecha Solicitud</th>
              <th>Empleado</th>
              <th>Motivo & Descripción</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="solicitudes.length === 0">
              <td colspan="5" class="empty-state">No hay solicitudes pendientes.</td>
            </tr>
            <tr v-for="sol in solicitudes" :key="sol.id">
              <td>{{ formatDate(sol.fechaSolicitud) }}</td>
              <td>
                <span class="font-medium">{{ sol.usuario.nombre }} {{ sol.usuario.apellidos }}</span>
              </td>
              <td>
                <div class="desc-box">
                  <strong>{{ sol.motivo }}</strong>
                  <p class="text-s text-muted">{{ sol.descripcion }}</p>
                </div>
              </td>
              <td>
                <StatusBadge :status="sol.estado" />
              </td>
              <td>
                <div class="actions" v-if="sol.estado === 'PENDIENTE'">
                  <button @click="responder(sol.id, 'APROBADA')" class="btn-action check">✓</button>
                  <button @click="responder(sol.id, 'RECHAZADA')" class="btn-action times">✕</button>
                </div>
                <div v-else class="text-s text-muted">
                  Resuelta el {{ formatDate(sol.fechaRespuesta) }}
                </div>
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
  margin-bottom: 2rem;
}

.text-subtitle {
  color: var(--text-muted);
  margin-top: 0.5rem;
}

.table-card {
  padding: 0;
  overflow: hidden;
}

.desc-box {
  max-width: 300px;
}

.desc-box strong {
  display: block;
  font-size: 0.9rem;
  margin-bottom: 0.2rem;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn-action {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: white;
  font-weight: bold;
}

.btn-action.check {
  background: var(--success);
}
.btn-action.check:hover {
  background: #059669;
}

.btn-action.times {
  background: var(--danger);
}
.btn-action.times:hover {
  background: #dc2626;
}

.text-s { font-size: 0.8rem; }
.text-muted { color: var(--text-muted); }
.font-medium { font-weight: 500; }
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
</style>
