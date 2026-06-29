import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para injetar o Access Token automaticamente em cada requisição
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('@PetLife:accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para tratar expiração de tokens ou desautenticação
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Se o erro for 401 (Unauthorized) e não for uma tentativa de login/registro
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest.url.includes('/auth/login') &&
      !originalRequest.url.includes('/auth/register') &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;

      // Limpa os dados em caso de desautenticação (ou poderíamos tentar o Refresh Token)
      localStorage.removeItem('@PetLife:accessToken');
      localStorage.removeItem('@PetLife:refreshToken');
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);

export default api;
