<script setup>
import { ref } from 'vue';
import { X } from 'lucide-vue-next';
import { useRouter } from 'vue-router';
import api from '../services/api';

const props = defineProps({
  isOpen: Boolean
});

const emit = defineEmits(['close']);
const router = useRouter();

const esPeriodo = ref(false);
const fechaUnica = ref('');
const fechaInicio = ref('');
const fechaFin = ref('');
const motivo = ref('');
const tipo = ref('VACACIONES');
const isSubmitting = ref(false);

const close = () => {
  emit('close');
};

const submitSolicitud = async () => {
  if (!motivo.value) {
    alert("El motivo es obligatorio");
    return;
  }
  
  if (esPeriodo.value && (!fechaInicio.value || !fechaFin.value)) {
    alert("Ambas fechas son obligatorias para un período");
    return;
  } else if (!esPeriodo.value && !fechaUnica.value) {
    alert("La fecha es obligatoria");
    return;
  }

  isSubmitting.value = true;
  try {
    const payload = {
      tipo: tipo.value,
      fechaInicio: esPeriodo.value ? fechaInicio.value : fechaUnica.value,
      fechaFin: esPeriodo.value ? fechaFin.value : fechaUnica.value,
      motivo: motivo.value
    };

    await api.post('/vacaciones', payload);
    
    alert("Solicitud guardada correctamente.");
    close();
    router.push('/solicitudes'); // Irá al historial de solicitudes (Admin view or Employee view)
  } catch (error) {
    console.error("Error al enviar solicitud:", error);
    alert("Error enviando solicitud. Compruebe la conexión.");
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="close">
    <div class="modal-content glass-panel">
      <div class="modal-header">
        <h3>Solicitar Vacaciones / Ausencia</h3>
        <button class="btn-icon" @click="close">
          <X size="20" />
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="submitSolicitud">
          
          <div class="form-group checkbox-group">
            <label class="toggle-container">
              <input type="checkbox" v-model="esPeriodo">
              <span class="checkmark"></span>
              Solicitar un período de varios días
            </label>
          </div>

          <!-- Día Único -->
          <div v-if="!esPeriodo" class="form-group fade-in">
            <label>Día a solicitar</label>
            <input type="date" v-model="fechaUnica" class="form-control" required>
          </div>

          <!-- Período -->
          <div v-else class="form-group-row fade-in">
            <div class="form-group">
              <label>Fecha de Inicio</label>
              <input type="date" v-model="fechaInicio" class="form-control" required>
            </div>
            <div class="form-group">
              <label>Fecha de Fin</label>
              <input type="date" v-model="fechaFin" class="form-control" required>
            </div>
          </div>

          <div class="form-group">
            <label>Motivo y Observaciones</label>
            <textarea v-model="motivo" placeholder="Explica brevemente el motivo..." class="form-control" rows="4" required></textarea>
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-outline" @click="close">Cancelar</button>
            <button type="submit" class="btn btn-primary">Enviar Solicitud</button>
          </div>

        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 90%;
  max-width: 500px;
  background: rgba(15, 20, 30, 0.95);
  border: 1px solid rgba(0, 240, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.modal-header h3 {
  margin: 0;
  color: white;
}

.btn-icon {
  background: transparent;
  border: none;
  color: var(--text-dim);
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
  display: flex;
  transition: all 0.2s;
}

.btn-icon:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.form-group-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.form-group-row .form-group {
  flex: 1;
  margin-bottom: 0;
}

.form-group label {
  font-weight: 500;
  color: var(--text-light);
  font-size: 0.9rem;
}

.form-control {
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 0.8rem 1rem;
  color: white;
  font-family: inherit;
  transition: all 0.3s ease;
}

.form-control:focus {
  outline: none;
  border-color: var(--primary-col);
  background: rgba(0, 0, 0, 0.4);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 1.5rem;
}

/* Custom Checkbox */
.checkbox-group {
  margin-bottom: 2rem;
}

.toggle-container {
  display: flex;
  align-items: center;
  position: relative;
  padding-left: 35px;
  cursor: pointer;
  font-size: 0.95rem;
  color: #fff;
  user-select: none;
}

.toggle-container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  position: absolute;
  top: 0;
  left: 0;
  height: 20px;
  width: 20px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  transition: all 0.2s;
}

.toggle-container:hover input ~ .checkmark {
  background-color: rgba(255, 255, 255, 0.2);
}

.toggle-container input:checked ~ .checkmark {
  background-color: var(--primary-col);
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
  left: 7px;
  top: 3px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.toggle-container input:checked ~ .checkmark:after {
  display: block;
}

.fade-in {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

input[type="date"]::-webkit-calendar-picker-indicator {
  filter: invert(1);
  cursor: pointer;
}
</style>
