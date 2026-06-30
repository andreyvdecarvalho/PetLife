import { useCallback, useState } from 'react';
import { authApi, type UserResponse } from '../../infrastructure/http/auth.api';
import { saveTokensAndGetProfile } from './useAuthTokens';

/**
 * Hook: Login via Google OAuth2.
 * Responsabilidade única: processar token Google e retornar perfil.
 * NOTA: A integração real com SDK do Google deve ser feita no componente.
 * Este hook apenas processa a resposta do backend.
 */
export function useGoogleLogin() {
  const [isLoading, setIsLoading] = useState(false);

  const loginWithGoogle = useCallback(
    async (idToken: string): Promise<UserResponse> => {
      setIsLoading(true);
      try {
        const response = await authApi.loginWithGoogle(idToken);
        const { accessToken, refreshToken } = response.data.data;
        return await saveTokensAndGetProfile(accessToken, refreshToken);
      } finally {
        setIsLoading(false);
      }
    },
    []
  );

  return { loginWithGoogle, isLoading };
}
