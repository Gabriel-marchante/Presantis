<script setup>
import { useAuthStore } from '../store/auth';
import { useRouter } from 'vue-router';
import { LogOut, User } from 'lucide-vue-next';
import logoUrl from '../assets/logo_Presantix.png';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<template>
  <header class="topbar glass-panel">
    <div class="brand">
      <img :src="logoUrl" class="logo" alt="Presantis Logo" />
      <h2>Presantis</h2>
    </div>
    
    <div class="user-profile">
      <div class="avatar">
        <User size="20" />
      </div>
      <div class="user-info">
        <span class="name">{{ authStore.user?.nombre }}</span>
        <span class="role">{{ authStore.user?.rol }}</span>
      </div>
      <button @click="handleLogout" class="btn btn-outline ml-4">
        <LogOut size="16" />
        Salir
      </button>
    </div>
  </header>
</template>

<style scoped>
.topbar {
  height: var(--header-h);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 2rem;
  border-radius: 0;
  border-left: none;
  border-right: none;
  border-top: none;
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
}

h2 {
  margin: 0;
  font-size: 1.3rem;
  letter-spacing: 0.05em;
  background: linear-gradient(to right, #fff, #94a3b8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.avatar {
  background: rgba(255, 255, 255, 0.1);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.name {
  font-size: 0.9rem;
  font-weight: 500;
}

.role {
  font-size: 0.75rem;
  color: var(--primary);
  font-weight: 600;
}

.ml-4 {
  margin-left: 1rem;
}
</style>
