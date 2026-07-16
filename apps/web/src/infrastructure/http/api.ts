import axios from 'axios';
import { tokenStorage } from '../storage/tokenStorage';

/**
 * Instância base do cliente HTTP Axios.
 * Princípio: Single Responsibility — apenas configuração base + interceptors.
 *
 * IMPORTANTE:
 * - window.location NÃO é usado aqui (DIP) — usar setUnauthorizedCallback
 * - Não importar axios diretamente nas camadas application/domain — usar auth.api.ts
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8081/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Callback injetado externamente para tratar 401 sem acoplar window.location
let onUnauthorizedCallback: (() => void) | null = null;

export function setUnauthorizedCallback(callback: () => void): void {
  onUnauthorizedCallback = callback;
}

// Interceptor de request: injeta Bearer token
api.interceptors.request.use((config) => {
  const token = tokenStorage.getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor de response: trata 401 via callback (sem window.location)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      tokenStorage.clearTokens();
      onUnauthorizedCallback?.();
    }
    return Promise.reject(error);
  }
);

export default api;
