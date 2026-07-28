import { useCallback, useState } from 'react';
import { authApi, type UserResponse } from '../../infrastructure/http/auth.api';
import { saveTokensAndGetProfile } from './useAuthTokens';

/**
 * Hook: Login via Apple.
 */
export function useAppleLogin() {
  const [isLoading, setIsLoading] = useState(false);

  const loginWithApple = useCallback(
    async (idToken: string, email?: string, name?: string): Promise<UserResponse> => {
      setIsLoading(true);
      try {
        const response = await authApi.loginWithApple(idToken, email, name);
        const { accessToken, refreshToken } = response.data.data;
        return await saveTokensAndGetProfile(accessToken, refreshToken);
      } finally {
        setIsLoading(false);
      }
    },
    []
  );

  return { loginWithApple, isLoading };
}
