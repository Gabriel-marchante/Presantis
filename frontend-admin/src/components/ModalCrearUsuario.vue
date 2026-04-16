<script setup>
import { ref } from 'vue';
import { X, UserPlus } from 'lucide-vue-next';
import api from '../services/api';

const props = defineProps({
  isOpen: Boolean
});

const emit = defineEmits(['close', 'userCreated']);

const formData = ref({
  nombre: '',
  apellidos: '',
  email: '',
  password: '',
  rol: 'EMPLOYEE'
});

const loading = ref(false);

const close = () => {
  emit('close');
};

const submitUsuario = async () => {
  loading.value = true;
  try {
    // Requires backend POST /admin/usuarios to be implemented for full completion
    // but the frontend widget layout will be fully operational
    const res = await api.post('/auth/register', formData.value);
    
    emit('userCreated', res.data); // Return new user struct to update the list instantly
    
    // Reset Form
    formData.value = {
      nombre: '',
      apellidos: '',
      email: '',
      password: '',
      rol: 'EMPLOYEE'
    };
    close();
  } catch (error) {
    console.error("Error al crear usuario", error);
    alert("Hubo un error al registrar al empleado.");
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="close">
    <div class="modal-content glass-panel">
      <div class="modal-header">
        <div class="header-title">
          <div class="icon-box"><UserPlus size="20" color="white" /></div>
          <h3>Registrar Nuevo Empleado</h3>
        </div>
        <button class="btn-icon" @click="close">
          <X size="20" />
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="submitUsuario">
          
          <div class="form-group-row fade-in">
            <div class="form-group">
              <label>Nombre</label>
              <input type="text" v-model="formData.nombre" class="form-control" placeholder="P.ej. Ana" required>
            </div>
            <div class="form-group">
              <label>Apellidos</label>
              <input type="text" v-model="formData.apellidos" class="form-control" placeholder="P.ej. García" required>
            </div>
          </div>

          <div class="form-group fade-in">
            <label>Correo Electrónico (Login)</label>
            <input type="email" v-model="formData.email" class="form-control" placeholder="ana@presantis.com" required>
          </div>

          <div class="form-group-row fade-in">
            <div class="form-group">
              <label>Contraseña Temporal</label>
              <input type="text" v-model="formData.password" class="form-control" placeholder="Generar contraseña..." required>
            </div>
            <div class="form-group">
              <label>Rol de Permisos</label>
              <select v-model="formData.rol" class="form-control" required>
                <option value="EMPLOYEE">Empleado Base</option>
                <option value="ADMIN">Administrador RRHH</option>
              </select>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-outline" @click="close" :disabled="loading">Cancelar</button>
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? 'Creando...' : 'Guardar Ficha' }}
            </button>
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
  max-width: 550px;
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

.header-title {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.icon-box {
  background: linear-gradient(135deg, var(--primary-col), var(--secondary-col));
  padding: 0.5rem;
  border-radius: 8px;
  display: flex;
}

.modal-header h3 {
  margin: 0;
  color: white;
  font-size: 1.2rem;
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

select.form-control {
  cursor: pointer;
}

select.form-control option {
  background: #111;
  color: white;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 1.5rem;
}

.fade-in {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
