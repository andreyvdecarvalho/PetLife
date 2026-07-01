import { useCallback } from 'react';
import { authApi } from '../../infrastructure/http/auth.api';
import { tokenStorage } from '../../infrastructure/storage/tokenStorage';

/**
 * Hook: Excluir conta do usuário (LGPD Art. 18).
 */
export function useDeleteAccount(onLogout: () => void) {
  const deleteAccount = useCallback(async (): Promise<void> => {
    await authApi.deleteAccount();
    tokenStorage.clearTokens();
    onLogout();
  }, [onLogout]);

  return { deleteAccount };
}
