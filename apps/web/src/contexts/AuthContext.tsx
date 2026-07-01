import React, { createContext, useContext, useCallback } from 'react';
import type { UserResponse, UpdateProfileData } from '../infrastructure/http/auth.api';
import { useLogin } from '../application/auth/useLogin';
import { useRegister } from '../application/auth/useRegister';
import { useGoogleLogin } from '../application/auth/useGoogleLogin';
import { useLogout } from '../application/auth/useLogout';
import { useSession } from '../application/auth/useSession';
import { useUpdateProfile } from '../application/user/useUpdateProfile';
import { useDeleteAccount } from '../application/user/useDeleteAccount';
import { setUnauthorizedCallback } from '../services/api';

// Re-exporta o tipo User para compatibilidade com componentes existentes
export type User = UserResponse;

interface AuthContextData {
  user: User | null;
  loading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  loginWithGoogle: (idToken: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  updateProfile: (data: UpdateProfileData) => Promise<void>;
  deleteAccount: () => Promise<void>;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

/**
 * Provedor de contexto de autenticação — thin layer.
 * Responsabilidade: gerenciar estado global do usuário e compor hooks de aplicação.
 * A lógica de negócio está nos hooks em src/application/auth/ e src/application/user/.
 * Princípio: Single Responsibility (SRP) + Dependency Inversion (DIP).
 */
export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Estado gerenciado pelo useSession (reidratação + usuário atual)
  const { user, setUser, loading } = useSession();

  const clearUser = useCallback(() => setUser(null), [setUser]);

  // Hooks de aplicação compostos
  const { login: loginUseCase } = useLogin();
  const { register: registerUseCase } = useRegister();
  const { loginWithGoogle: googleLoginUseCase } = useGoogleLogin();
  const { logout: logoutUseCase } = useLogout(clearUser);
  const { updateProfile: updateProfileUseCase } = useUpdateProfile();
  const { deleteAccount: deleteAccountUseCase } = useDeleteAccount(clearUser);

  // Registra callback para o interceptor de API tratar 401 sem window.location
  React.useEffect(() => {
    setUnauthorizedCallback(logoutUseCase);
  }, [logoutUseCase]);

  const login = useCallback(async (email: string, password: string) => {
    const userProfile = await loginUseCase(email, password);
    setUser(userProfile);
  }, [loginUseCase, setUser]);

  const register = useCallback(async (name: string, email: string, password: string) => {
    const userProfile = await registerUseCase(name, email, password);
    setUser(userProfile);
  }, [registerUseCase, setUser]);

  const loginWithGoogle = useCallback(async (idToken: string) => {
    const userProfile = await googleLoginUseCase(idToken);
    setUser(userProfile);
  }, [googleLoginUseCase, setUser]);

  const updateProfile = useCallback(async (data: UpdateProfileData) => {
    const updatedUser = await updateProfileUseCase(data);
    setUser(updatedUser);
  }, [updateProfileUseCase, setUser]);

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        isAuthenticated: user !== null,
        login,
        loginWithGoogle,
        register,
        logout: logoutUseCase,
        updateProfile,
        deleteAccount: deleteAccountUseCase,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextData => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
};
