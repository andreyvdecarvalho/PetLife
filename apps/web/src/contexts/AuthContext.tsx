import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import api from '../services/api';

export interface User {
  id: string;
  name: string;
  email: string;
  avatarUrl: string;
  timezone: string;
  plan: 'FREE' | 'PREMIUM' | 'FAMILY';
  emailVerified: boolean;
  createdAt: string;
}

interface AuthContextData {
  user: User | null;
  loading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  loginWithGoogle: (idToken: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  updateProfile: (name: string, email: string, avatarUrl: string, timezone: string) => Promise<void>;
  deleteAccount: () => Promise<void>;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const logout = useCallback(() => {
    localStorage.removeItem('@PetLife:accessToken');
    localStorage.removeItem('@PetLife:refreshToken');
    setUser(null);
  }, []);

  // Reidratação de sessão
  useEffect(() => {
    async function loadStorageData() {
      const token = localStorage.getItem('@PetLife:accessToken');

      if (token) {
        try {
          const response = await api.get('/auth/me');
          setUser(response.data.data);
        } catch (error) {
          logError(error);
          logout();
        }
      }
      setLoading(false);
    }

    loadStorageData();
  }, [logout]);

  const login = useCallback(async (email: string, password: string) => {
    setLoading(true);
    try {
      const response = await api.post('/auth/login', { email, password });
      const { accessToken, refreshToken } = response.data.data;

      localStorage.setItem('@PetLife:accessToken', accessToken);
      localStorage.setItem('@PetLife:refreshToken', refreshToken);

      // Busca dados do perfil recém-logado
      const profileResponse = await api.get('/auth/me');
      setUser(profileResponse.data.data);
    } catch (error) {
      logout();
      throw error;
    } finally {
      setLoading(false);
    }
  }, [logout]);

  const loginWithGoogle = useCallback(async (idToken: string) => {
    setLoading(true);
    try {
      const response = await api.post('/auth/google', { idToken });
      const { accessToken, refreshToken } = response.data.data;

      localStorage.setItem('@PetLife:accessToken', accessToken);
      localStorage.setItem('@PetLife:refreshToken', refreshToken);

      // Busca dados do perfil recém-logado
      const profileResponse = await api.get('/auth/me');
      setUser(profileResponse.data.data);
    } catch (error) {
      logout();
      throw error;
    } finally {
      setLoading(false);
    }
  }, [logout]);

  const register = useCallback(async (name: string, email: string, password: string) => {
    setLoading(true);
    try {
      const response = await api.post('/auth/register', { name, email, password });
      const { accessToken, refreshToken } = response.data.data;

      localStorage.setItem('@PetLife:accessToken', accessToken);
      localStorage.setItem('@PetLife:refreshToken', refreshToken);

      // Busca dados do perfil recém-cadastrado
      const profileResponse = await api.get('/auth/me');
      setUser(profileResponse.data.data);
    } catch (error) {
      logout();
      throw error;
    } finally {
      setLoading(false);
    }
  }, [logout]);

  const updateProfile = useCallback(async (name: string, email: string, avatarUrl: string, timezone: string) => {
    const response = await api.put('/auth/me', { name, email, avatarUrl, timezone });
    setUser(response.data.data);
  }, []);

  const deleteAccount = useCallback(async () => {
    await api.delete('/auth/me');
    logout();
  }, [logout]);

  const logError = (error: any) => {
    console.error('Session rehydration failed:', error);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        isAuthenticated: !!user,
        login,
        loginWithGoogle,
        register,
        logout,
        updateProfile,
        deleteAccount
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado com um AuthProvider');
  }
  return context;
};
