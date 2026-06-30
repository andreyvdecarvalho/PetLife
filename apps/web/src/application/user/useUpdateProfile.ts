import { useCallback } from 'react';
import { authApi, type UserResponse, type UpdateProfileData } from '../../infrastructure/http/auth.api';

/**
 * Hook: Atualizar perfil do usuário autenticado.
 */
export function useUpdateProfile() {
  const updateProfile = useCallback(
    async (data: UpdateProfileData): Promise<UserResponse> => {
      const response = await authApi.updateProfile(data);
      return response.data.data;
    },
    []
  );

  return { updateProfile };
}
