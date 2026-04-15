<script setup>
import { useRoute } from 'vue-router';
import { LayoutDashboard, FileText, CalendarDays, Activity } from 'lucide-vue-next';

const route = useRoute();

const links = [
  { name: 'Dashboard', path: '/', icon: LayoutDashboard },
  { name: 'Solicitudes', path: '/solicitudes', icon: FileText },
  { name: 'Festivos', path: '/festivos', icon: CalendarDays },
  { name: 'Logs', path: '/logs', icon: Activity }
];
</script>

<template>
  <aside class="sidebar glass-panel">
    <nav class="nav-menu">
      <router-link 
        v-for="link in links" 
        :key="link.path" 
        :to="link.path"
        class="nav-item"
        :class="{ active: route.path === link.path }"
      >
        <component :is="link.icon" size="20" />
        {{ link.name }}
      </router-link>
    </nav>
  </aside>
</template>

<style scoped>
.sidebar {
  width: var(--sidebar-w);
  height: 100vh;
  border-radius: 0;
  border-left: none;
  border-top: none;
  border-bottom: none;
  display: flex;
  flex-direction: column;
  padding: 2rem 1rem;
  z-index: 5;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-top: 2rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  color: var(--text-muted);
  text-decoration: none;
  border-radius: var(--radius-md);
  transition: all 0.2s ease;
  font-weight: 500;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-main);
  transform: translateX(5px);
}

.nav-item.active {
  background: rgba(59, 130, 246, 0.15);
  color: var(--primary);
  border-left: 3px solid var(--primary);
  box-shadow: inset 0 0 10px rgba(59, 130, 246, 0.05);
}
</style>
