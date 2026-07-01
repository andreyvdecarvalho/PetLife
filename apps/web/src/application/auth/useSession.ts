import { useState, useEffect } from 'react';
import { authApi, type UserResponse } from '../../infrastructure/http/auth.api';
import { tokenStorage } from '../../infrastructure/storage/tokenStorage';

/**
 * Hook: Reidratação de sessão ao carregar o app.
 * Responsabilidade única: verificar token existente e carregar usuário.
 */
export function useSession() {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function rehydrate() {
      if (!tokenStorage.hasToken()) {
        setLoading(false);
        return;
      }
      try {
        const response = await authApi.getProfile();
        setUser(response.data.data);
      } catch {
        tokenStorage.clearTokens();
      } finally {
        setLoading(false);
      }
    }
    rehydrate();
  }, []);

  return { user, setUser, loading };
}
