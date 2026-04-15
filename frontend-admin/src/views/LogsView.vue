<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const logs = ref([]);
const loading = ref(true);

const fetchLogs = async () => {
  try {
    const res = await api.get('/admin/logs');
    logs.value = res.data;
  } catch (error) {
    console.error("Error cargando logs:", error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const d = new Date(dateString);
  return d.toLocaleDateString('es-ES', { 
    year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit'
  });
};

onMounted(() => {
  fetchLogs();
});
</script>

<template>
  <div class="page-content">
    <div class="page-header">
      <h2>Trazabilidad de Cambios</h2>
      <p class="text-subtitle">Registro legal de todas las modificaciones realizadas a las jornadas (Real Decreto-ley 8/2019)</p>
    </div>

    <div class="table-card glass-panel">
      <div v-if="loading" class="loading-state">Cargando registros...</div>
      
      <div v-else class="table-container">
        <table class="table">
          <thead>
            <tr>
              <th>Fecha Log</th>
              <th>Jornada (Afectado)</th>
              <th>Modificado por</th>
              <th>Campo</th>
              <th>Valor Anterior → Nuevo</th>
              <th>Motivo de Edición</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="logs.length === 0">
              <td colspan="6" class="empty-state">No existe ningún log de modificaciones. Todas las jornadas están inalteradas.</td>
            </tr>
            <tr v-for="log in logs" :key="log.id">
              <td class="font-timestamp">{{ formatDate(log.fecha) }}</td>
              <td>{{ log.jornada.usuario.nombre }} ({{ formatDate(log.jornada.fechaInicio) }})</td>
              <td class="text-primary font-medium">{{ log.modificadoPor.nombre }} ({{ log.modificadoPor.rol }})</td>
              <td><span class="badge badge-pending">{{ log.campoModificado }}</span></td>
              <td>
                <div class="value-change">
                  <span class="old-val">{{ log.valorAnterior || 'NULL' }}</span>
                  <span class="arrow">→</span>
                  <span class="new-val">{{ log.valorNuevo }}</span>
                </div>
              </td>
              <td><span class="motivo-text">{{ log.motivo || 'Modificación administrativa manual' }}</span></td>
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

.font-timestamp {
  font-family: monospace;
  color: var(--text-muted);
  font-size: 0.9rem;
}

.text-primary {
  color: var(--primary);
}

.font-medium {
  font-weight: 500;
}

.value-change {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-family: monospace;
  font-size: 0.85rem;
}

.old-val {
  color: var(--danger);
  text-decoration: line-through;
  background: rgba(239, 68, 68, 0.1);
  padding: 0.1rem 0.3rem;
  border-radius: 4px;
}

.new-val {
  color: var(--success);
  background: rgba(16, 185, 129, 0.1);
  padding: 0.1rem 0.3rem;
  border-radius: 4px;
}

.arrow {
  color: var(--text-muted);
}

.motivo-text {
  font-size: 0.85rem;
  color: var(--text-muted);
  font-style: italic;
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
</style>
