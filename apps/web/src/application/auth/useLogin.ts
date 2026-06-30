import { useCallback, useState } from 'react';
import { authApi, type UserResponse } from '../../infrastructure/http/auth.api';
import { saveTokensAndGetProfile } from './useAuthTokens';

/**
 * Hook: Login com e-mail e senha.
 * Responsabilidade única: autenticar usuário e retornar perfil.
 */
export function useLogin() {
  const [isLoading, setIsLoading] = useState(false);

  const login = useCallback(
    async (email: string, password: string): Promise<UserResponse> => {
      setIsLoading(true);
      try {
        const response = await authApi.login(email, password);
        const { accessToken, refreshToken } = response.data.data;
        return await saveTokensAndGetProfile(accessToken, refreshToken);
      } finally {
        setIsLoading(false);
      }
    },
    []
  );

  return { login, isLoading };
}
