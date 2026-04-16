<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../store/auth';
import { useRouter } from 'vue-router';
import { Clock } from 'lucide-vue-next';

const authStore = useAuthStore();
const router = useRouter();

const email = ref('');
const password = ref('');

const handleLogin = async () => {
  const success = await authStore.login(email.value, password.value);
  if (success) {
    router.push('/');
  }
};
</script>

<template>
  <div class="login-container">
    <div class="login-box glass-panel">
      <div class="login-header">
        <img src="../assets/logo_Presantix.png" alt="Presantis Logo" class="brand-logo-large" />
        <p class="subtitle">Área de Control y Presencia</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div v-if="authStore.error" class="error-msg">
          {{ authStore.error }}
        </div>

        <div class="form-group">
          <label class="form-label">Email Corporativo</label>
          <input 
            v-model="email" 
            type="email" 
            class="form-input" 
            placeholder="admin@presantis.com"
            required
          />
        </div>

        <div class="form-group">
          <label class="form-label">Contraseña</label>
          <input 
            v-model="password" 
            type="password" 
            class="form-input" 
            placeholder="••••••••"
            required
          />
        </div>

        <button type="submit" class="btn btn-primary login-btn" :disabled="authStore.loading">
          {{ authStore.loading ? 'Entrando...' : 'Acceder al Panel' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at center, var(--bg-card), var(--bg-dark));
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 3rem 2rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.login-header {
  text-align: center;
}

.logo {
  background: linear-gradient(135deg, var(--primary), #8b5cf6);
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
  box-shadow: var(--shadow-glow);
}

.login-header h1 {
  margin: 0;
  font-size: 1.75rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.login-btn {
  margin-top: 1rem;
  width: 100%;
  padding: 0.8rem;
  font-size: 1rem;
}

.error-msg {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger);
  padding: 0.75rem;
  border-radius: var(--radius-md);
  text-align: center;
  font-size: 0.9rem;
  border: 1px solid rgba(239, 68, 68, 0.3);
}
</style>
