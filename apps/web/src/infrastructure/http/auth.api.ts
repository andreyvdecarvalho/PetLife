import api from '../../services/api';

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

export interface UserResponse {
  id: string;
  name: string;
  email: string;
  avatarUrl: string;
  timezone: string;
  plan: 'FREE' | 'PREMIUM' | 'FAMILY';
  emailVerified: boolean;
  createdAt: string;
}

export interface UpdateProfileData {
  name: string;
  email: string;
  avatarUrl: string;
  timezone: string;
}

/**
 * Serviço HTTP de autenticação.
 * Camada de infraestrutura — encapsula chamadas HTTP de auth.
 * Princípio: Single Responsibility (SRP) + Dependency Inversion (DIP).
 */
export const authApi = {
  login: (email: string, password: string) =>
    api.post<{ data: TokenResponse }>('/auth/login', { email, password }),

  register: (name: string, email: string, password: string) =>
    api.post<{ data: TokenResponse }>('/auth/register', { name, email, password }),

  loginWithGoogle: (idToken: string) =>
    api.post<{ data: TokenResponse }>('/auth/google', { idToken }),

  getProfile: () =>
    api.get<{ data: UserResponse }>('/auth/me'),

  updateProfile: (data: UpdateProfileData) =>
    api.put<{ data: UserResponse }>('/auth/me', data),

  forgotPassword: (email: string) =>
    api.post('/auth/forgot-password', { email }),

  resetPassword: (token: string, newPassword: string) =>
    api.post('/auth/reset-password', { token, newPassword }),

  deleteAccount: () =>
    api.delete('/auth/me'),
};
