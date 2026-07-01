import { useCallback, useState } from 'react';
import { authApi, type UserResponse } from '../../infrastructure/http/auth.api';
import { saveTokensAndGetProfile } from './useAuthTokens';

/**
 * Hook: Cadastro de novo usuário.
 * Responsabilidade única: registrar usuário e retornar perfil.
 */
export function useRegister() {
  const [isLoading, setIsLoading] = useState(false);

  const register = useCallback(
    async (name: string, email: string, password: string): Promise<UserResponse> => {
      setIsLoading(true);
      try {
        const response = await authApi.register(name, email, password);
        const { accessToken, refreshToken } = response.data.data;
        return await saveTokensAndGetProfile(accessToken, refreshToken);
      } finally {
        setIsLoading(false);
      }
    },
    []
  );

  return { register, isLoading };
}
