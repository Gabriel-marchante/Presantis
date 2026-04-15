import { defineStore } from 'pinia';
import api from '../services/api';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('jwt_token') || null,
    user: JSON.parse(localStorage.getItem('user_info')) || null,
    loading: false,
    error: null
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.rol === 'ADMIN'
  },
  actions: {
    async login(email, password) {
      this.loading = true;
      this.error = null;
      try {
        const response = await api.post('/auth/login', { email, password });
        this.token = response.data.token;
        this.user = {
          id: response.data.usuarioId,
          nombre: response.data.nombre,
          apellidos: response.data.apellidos,
          rol: response.data.rol,
          empresa: response.data.empresaNombre
        };
        
        localStorage.setItem('jwt_token', this.token);
        localStorage.setItem('user_info', JSON.stringify(this.user));
        return true;
      } catch (err) {
        this.error = err.response?.data?.error || 'Error al iniciar sesión';
        return false;
      } finally {
        this.loading = false;
      }
    },
    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_info');
    }
  }
});
