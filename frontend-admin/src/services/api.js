import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080', // Quarkus backend URL
  headers: {
    'Content-Type': 'application/json'
  }
});

// Intercept requests to inject the JWT
api.interceptors.request.use(config => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// Intercept responses to handle global errors (like 401 Unauthorized)
api.interceptors.response.use(response => response, error => {
  if (error.response && error.response.status === 401 && !error.config.url.includes('/auth/login')) {
    // We are unauthorized, clear state and kick to login
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_info');
    window.location.href = '/login';
  }
  return Promise.reject(error);
});

export default api;
