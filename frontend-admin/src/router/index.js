import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../store/auth';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/',
    component: () => import('../components/BaseLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/DashboardView.vue')
      },
      {
        path: 'solicitudes',
        name: 'Solicitudes',
        component: () => import('../views/SolicitudesView.vue')
      },
      {
        path: 'festivos',
        name: 'Festivos',
        component: () => import('../views/FestivosView.vue')
      },
      {
        path: 'logs',
        name: 'Logs',
        component: () => import('../views/LogsView.vue')
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Navigation Guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next('/');
  } else if (to.meta.requiresAdmin && !authStore.isAdmin && authStore.isAuthenticated) {
    // If not admin, shouldn't be here (in a real app, maybe kick to employee view or 403)
    authStore.logout();
    next('/login');
  } else {
    next();
  }
});

export default router;
