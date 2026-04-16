<script setup>
import { ref, onMounted } from 'vue';
import { Users, UserPlus, Search, Building, Clock, AlertTriangle, CalendarDays, CheckCircle2 } from 'lucide-vue-next';
import api from '../services/api';
import HistorialJornadasWidget from '../components/HistorialJornadasWidget.vue';
import ModalCrearUsuario from '../components/ModalCrearUsuario.vue';

const usuarios = ref([]);
const loading = ref(true);
const selectedUser = ref(null);
const searchTerm = ref('');
const isModalOpen = ref(false);
const selectedUserStats = ref(null);

const openModal = () => {
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const handleUserCreated = (newUser) => {
  usuarios.value.push(newUser);
  // Opcional: auto-seleccionar al nuevo usuario
  selectUser(newUser);
};

const fetchUsuarios = async () => {
  try {
    const res = await api.get('/admin/usuarios');
    usuarios.value = res.data;
  } catch (error) {
    console.error("Error fetching usuarios:", error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchUsuarios();
});

const selectUser = async (u) => {
  selectedUser.value = u;
  selectedUserStats.value = null; // reset while loading
  try {
    const res = await api.get(`/admin/usuarios/${u.id}/stats`);
    selectedUserStats.value = res.data;
  } catch (e) {
    console.error("Error al cargar estadisticas", e);
  }
};
</script>

<template>
  <div class="page-content usuarios-page">
    <ModalCrearUsuario :isOpen="isModalOpen" @close="closeModal" @userCreated="handleUserCreated" />
    
    <header class="page-header">
      <div>
        <h2>Gestión de Trabajadores</h2>
        <p class="subtitle">Directorio de empleados y auditoría de jornadas</p>
      </div>
      <button class="btn btn-primary" @click="openModal">
        <UserPlus size="18" />
        Nuevo Empleado
      </button>
    </header>

    <div class="usuarios-layout">
      <!-- Left List -->
      <div class="usuarios-list-container glass-panel">
        <div class="list-header">
          <div class="search-bar">
            <Search size="16" class="icon-muted" />
            <input type="text" v-model="searchTerm" placeholder="Buscar empleado..." />
          </div>
          <span class="total-users">{{ usuarios.length }} en total</span>
        </div>

        <div v-if="loading" class="loading-state">Cargando...</div>
        
        <ul v-else class="user-list">
          <li 
            v-for="u in usuarios" 
            :key="u.id" 
            @click="selectUser(u)"
            :class="{ active: selectedUser?.id === u.id }"
          >
            <div class="avatar-sm">
              {{ u.nombre.charAt(0) }}{{ u.apellidos.charAt(0) }}
            </div>
            <div class="user-info">
              <span class="font-medium">{{ u.nombre }} {{ u.apellidos }}</span>
              <span class="role-badge">{{ u.rol }}</span>
            </div>
          </li>
        </ul>
      </div>

      <!-- Right Details / History -->
      <div class="user-details-container">
        
        <div v-if="selectedUser" class="selected-user-panel glass-panel">
          <div class="profile-header">
            <div class="avatar-lg">
              <Users size="40" color="white" />
            </div>
            <div class="profile-info">
              <h3>{{ selectedUser.nombre }} {{ selectedUser.apellidos }}</h3>
              <p class="text-muted"><Building size="14"/> {{ selectedUser.email }}</p>
            </div>
            <div class="profile-actions">
              <button class="btn btn-outline btn-sm">Editar Datos</button>
            </div>
          </div>

          <div class="user-stats-bar" v-if="selectedUserStats">
            <div class="stat-item">
              <CheckCircle2 size="16" color="var(--primary-col)" />
              <div class="stat-text">
                <span class="val">{{ selectedUserStats.diasTrabajados }}</span>
                <span class="lbl">Días Activos</span>
              </div>
            </div>
            <div class="stat-item">
              <Clock size="16" color="var(--warning)" />
              <div class="stat-text">
                <span class="val">{{ selectedUserStats.horasExtra }}h</span>
                <span class="lbl">Extra</span>
              </div>
            </div>
            <div class="stat-item">
              <AlertTriangle size="16" color="var(--danger)" />
              <div class="stat-text">
                <span class="val">{{ selectedUserStats.retrasos }}</span>
                <span class="lbl">Retrasos</span>
              </div>
            </div>
            <div class="stat-item">
              <CalendarDays size="16" color="var(--success)" />
              <div class="stat-text">
                <span class="val">{{ selectedUserStats.diasLibresAprobados }}</span>
                <span class="lbl">Días Libres</span>
              </div>
            </div>
          </div>
          
          <div class="historial-wrapper">
            <HistorialJornadasWidget :userId="selectedUser.id" />
          </div>
        </div>

        <div v-else class="empty-selection glass-panel">
          <Users size="48" color="rgba(255,255,255,0.1)" />
          <h3>Ningún empleado seleccionado</h3>
          <p>Selecciona a alguien de la lista para ver su registro de jornadas y editar su información.</p>
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

.usuarios-layout {
  display: flex;
  gap: 2rem;
  height: calc(100vh - 180px);
}

/* Left List */
.usuarios-list-container {
  width: 350px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.list-header {
  padding: 1.5rem;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}

.search-bar {
  display: flex;
  align-items: center;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  padding: 0.5rem 1rem;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.search-bar input {
  background: transparent;
  border: none;
  color: white;
  width: 100%;
  outline: none;
}

.total-users {
  font-size: 0.8rem;
  color: var(--text-dim);
}

.user-list {
  list-style: none;
  padding: 0;
  margin: 0;
  overflow-y: auto;
  flex: 1;
}

.user-list li {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.5rem;
  cursor: pointer;
  border-bottom: 1px solid rgba(255,255,255,0.02);
  transition: all 0.2s;
}

.user-list li:hover {
  background: rgba(255,255,255,0.05);
}

.user-list li.active {
  background: rgba(0, 240, 255, 0.1);
  border-left: 3px solid var(--primary-col);
}

.avatar-sm {
  width: 40px;
  height: 40px;
  background: var(--primary-col);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 0.9rem;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.role-badge {
  font-size: 0.7rem;
  color: var(--text-dim);
  text-transform: capitalize;
}

/* Right Details */
.user-details-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.selected-user-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.profile-header {
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}

.avatar-lg {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, var(--primary-col), var(--secondary-col));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(0, 240, 255, 0.3);
}

.profile-info {
  flex: 1;
}

.profile-info h3 { margin: 0 0 0.5rem 0; font-size: 1.5rem;}
.profile-info p { margin: 0; display: flex; align-items: center; gap: 0.5rem; }

.historial-wrapper {
  padding: 1.5rem;
  flex: 1;
  overflow-y: auto;
}

.user-stats-bar {
  display: flex;
  justify-content: space-around;
  padding: 1rem 1.5rem;
  background: rgba(0, 0, 0, 0.15);
  border-bottom: 1px solid rgba(255,255,255,0.05);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.stat-text {
  display: flex;
  flex-direction: column;
}

.stat-text .val {
  font-weight: bold;
  font-size: 1.1rem;
  color: white;
}

.stat-text .lbl {
  font-size: 0.75rem;
  color: var(--text-dim);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* No Selection */
.empty-selection {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: var(--text-dim);
}

.empty-selection h3 { color: white; margin: 1.5rem 0 0.5rem 0; }

@media (max-width: 900px) {
  .usuarios-layout { flex-direction: column; height: auto; }
  .usuarios-list-container { width: 100%; height: 300px; }
}
</style>
