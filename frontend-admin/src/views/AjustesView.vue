<script setup>
import { ref, onMounted, watch } from 'vue';
import { Settings, User, Bell, Palette, Shield } from 'lucide-vue-next';
import { useAuthStore } from '../store/auth';

const authStore = useAuthStore();
const activeTab = ref('perfil');

const userProfile = ref({
  nombre: authStore.user?.nombre || '',
  apellidos: authStore.user?.apellidos || '',
  email: authStore.user?.email || 'email@ejemplo.com',
  telefono: '+34 600 000 000',
  notificacionesOn: true,
  modoOscuro: true
});

const fileInput = ref(null);
const profilePicUrl = ref(localStorage.getItem('userAvatar') || '');
const isSaving = ref(false);

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      profilePicUrl.value = e.target.result;
      localStorage.setItem('userAvatar', e.target.result);
    };
    reader.readAsDataURL(file);
  }
};

const saveProfileSettings = async () => {
  isSaving.value = true;
  try {
    const res = await api.put('/usuarios/me', userProfile.value);
    alert("Datos de perfil guardados correctamente.");
    // Sync store
    authStore.user.nombre = res.data.nombre;
    authStore.user.apellidos = res.data.apellidos;
    authStore.user.email = res.data.email;
  } catch (e) {
    alert("Error al guardar");
  } finally {
    isSaving.value = false;
  }
};

const passwordData = ref({
  current: '',
  newPassword: '',
  confirm: ''
});

const updatePassword = async () => {
  if (passwordData.value.newPassword !== passwordData.value.confirm) {
    alert("Las contraseñas no coinciden.");
    return;
  }
  isSaving.value = true;
  try {
    await api.put('/usuarios/me/password', passwordData.value);
    alert("Contraseña actualizada con éxito.");
    passwordData.value = { current: '', newPassword: '', confirm: '' };
  } catch (e) {
    if (e.response && e.response.status === 400) {
      alert("La contraseña actual es incorrecta.");
    } else {
      alert("Error al actualizar la contraseña.");
    }
  } finally {
    isSaving.value = false;
  }
};

// Reactive Theme change
watch(() => userProfile.value.modoOscuro, (isDark) => {
  if(!isDark) {
    document.documentElement.setAttribute('data-theme', 'light');
    localStorage.setItem('theme', 'light');
  } else {
    document.documentElement.setAttribute('data-theme', 'dark');
    localStorage.setItem('theme', 'dark');
  }
});

onMounted(() => {
  const currentTheme = document.documentElement.getAttribute('data-theme');
  userProfile.value.modoOscuro = currentTheme !== 'light';
});
</script>

<template>
  <div class="page-content ajustes-container">
    <header class="page-header">
      <div>
        <h2>Ajustes de Cuenta</h2>
        <p class="subtitle">Preferencias y datos personales</p>
      </div>
    </header>

    <div class="settings-layout">
      <!-- Menu Lateral de Ajustes -->
      <nav class="settings-nav glass-panel">
        <ul>
          <li>
            <button :class="{ active: activeTab === 'perfil' }" @click="activeTab = 'perfil'">
              <User size="18" /> Perfil y Datos
            </button>
          </li>
          <li>
            <button :class="{ active: activeTab === 'seguridad' }" @click="activeTab = 'seguridad'">
              <Shield size="18" /> Seguridad y Contraseña
            </button>
          </li>
          <li>
            <button :class="{ active: activeTab === 'apariencia' }" @click="activeTab = 'apariencia'">
              <Palette size="18" /> Apariencia
            </button>
          </li>
          <li>
            <button :class="{ active: activeTab === 'notificaciones' }" @click="activeTab = 'notificaciones'">
              <Bell size="18" /> Notificaciones
            </button>
          </li>
        </ul>
      </nav>

      <!-- Panel Central de Ajustes -->
      <div class="settings-content glass-panel">
        
        <div v-if="activeTab === 'perfil'" class="tab-pane">
          <h3>Información Personal</h3>
          <form @submit.prevent="saveProfileSettings" class="settings-form">
            
            <div class="avatar-section">
              <div class="avatar-circle">
                <img v-if="profilePicUrl" :src="profilePicUrl" alt="Avatar" class="avatar-img" />
                <span v-else-if="userProfile.nombre">{{ userProfile.nombre.charAt(0) }}{{ userProfile.apellidos.charAt(0) }}</span>
                <User v-else size="40" color="white" />
              </div>
              <div class="avatar-actions">
                <input type="file" ref="fileInput" @change="handleFileUpload" hidden accept="image/*" />
                <button type="button" class="btn btn-outline" @click="$refs.fileInput.click()">Cambiar Foto</button>
              </div>
            </div>

            <div class="form-grid">
              <div class="form-group">
                <label>Nombre</label>
                <input type="text" v-model="userProfile.nombre" class="form-control" />
              </div>
              <div class="form-group">
                <label>Apellidos</label>
                <input type="text" v-model="userProfile.apellidos" class="form-control" />
              </div>
              <div class="form-group">
                <label>Email</label>
                <input type="email" v-model="userProfile.email" class="form-control" disabled />
                <small>Contacta con RR.HH para cambiar el email</small>
              </div>
              <div class="form-group">
                <label>Teléfono</label>
                <input type="tel" v-model="userProfile.telefono" class="form-control" />
              </div>
            </div>
            
            <div class="form-actions">
              <button type="submit" class="btn btn-primary" :disabled="isSaving">{{ isSaving ? 'Guardando...' : 'Guardar Cambios' }}</button>
            </div>
          </form>
        </div>

        <div v-if="activeTab === 'seguridad'" class="tab-pane">
          <h3>Seguridad</h3>
          <p class="text-muted">Actualiza tu contraseña para mantener tu cuenta segura.</p>
          <form @submit.prevent="updatePassword" class="settings-form">
            <div class="form-group">
              <label>Contraseña Actual</label>
              <input type="password" v-model="passwordData.current" class="form-control" placeholder="••••••••" required />
            </div>
            <div class="form-group">
              <label>Nueva Contraseña</label>
              <input type="password" v-model="passwordData.newPassword" class="form-control" placeholder="••••••••" required />
            </div>
            <div class="form-group">
              <label>Confirmar Nueva Contraseña</label>
              <input type="password" v-model="passwordData.confirm" class="form-control" placeholder="••••••••" required />
            </div>
            <div class="form-actions">
              <button type="submit" class="btn btn-primary" :disabled="isSaving">{{ isSaving ? 'Actualizando...' : 'Actualizar Contraseña' }}</button>
            </div>
          </form>
        </div>

        <div v-if="activeTab === 'apariencia'" class="tab-pane">
          <h3>Apariencia del Sistema</h3>
          <div class="toggle-group">
            <div class="toggle-info">
              <h4>Tema Oscuro (Neón)</h4>
              <p class="text-muted">Activar interfaz de alto espectro con acentos brillantes.</p>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="userProfile.modoOscuro">
              <span class="slider round"></span>
            </label>
          </div>
        </div>

        <div v-if="activeTab === 'notificaciones'" class="tab-pane">
          <h3>Preferencias de Notificación</h3>
          <div class="toggle-group">
            <div class="toggle-info">
              <h4>Alertas por Email</h4>
              <p class="text-muted">Recibe notificaciones cuando haya cambios en tu jornada.</p>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="userProfile.notificacionesOn">
              <span class="slider round"></span>
            </label>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-layout {
  display: flex;
  gap: 2rem;
  align-items: flex-start;
}

.settings-nav {
  flex: 0 0 250px;
  padding: 1rem 0;
}

.settings-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.settings-nav li button {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.5rem;
  background: transparent;
  border: none;
  border-left: 3px solid transparent;
  color: var(--text-dim);
  font-size: 1rem;
  font-weight: 500;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s ease;
}

.settings-nav li button:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-light);
}

.settings-nav li button.active {
  background: rgba(0, 240, 255, 0.1);
  color: var(--primary-col);
  border-left-color: var(--primary-col);
}

.settings-content {
  flex: 1;
  padding: 2.5rem;
}

.tab-pane h3 {
  margin-top: 0;
  margin-bottom: 2rem;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 1rem;
}

.text-muted {
  color: var(--text-dim);
  font-size: 0.9rem;
  margin-bottom: 2rem;
}

.settings-form {
  max-width: 600px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 2rem;
  margin-bottom: 2.5rem;
}

.avatar-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-col), var(--secondary-col));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  color: white;
  box-shadow: 0 4px 15px rgba(0, 240, 255, 0.3);
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 500;
  color: var(--text-light);
  font-size: 0.9rem;
}

.form-group small {
  color: var(--text-dim);
  font-size: 0.8rem;
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
  box-shadow: 0 0 10px rgba(0, 240, 255, 0.1);
}

.form-control:disabled {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-dim);
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 2rem;
}

/* Toggle Switch Styling */
.toggle-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  background: rgba(0, 0, 0, 0.15);
  border-radius: 12px;
  margin-bottom: 1rem;
}

.toggle-info h4 {
  margin: 0 0 0.5rem 0;
  color: #fff;
}

.toggle-info p {
  margin: 0;
}

.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 28px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(255, 255, 255, 0.1);
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: .4s;
}

input:checked + .slider {
  background-color: var(--primary-col);
  box-shadow: 0 0 10px rgba(0, 240, 255, 0.4);
}

input:focus + .slider {
  box-shadow: 0 0 10px var(--primary-col);
}

input:checked + .slider:before {
  transform: translateX(22px);
}

.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}

@media (max-width: 768px) {
  .settings-layout {
    flex-direction: column;
  }
  
  .settings-nav {
    width: 100%;
    flex: auto;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
