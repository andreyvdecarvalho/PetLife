import { useCallback } from 'react';
import { tokenStorage } from '../../infrastructure/storage/tokenStorage';

/**
 * Hook: Logout do usuário.
 * Responsabilidade única: limpar tokens e estado de autenticação.
 */
export function useLogout(onLogout: () => void) {
  const logout = useCallback(() => {
    tokenStorage.clearTokens();
    onLogout();
  }, [onLogout]);

  return { logout };
}
