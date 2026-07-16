import { useCallback } from 'react';
import { authApi, type UserResponse, type UpdateProfileData } from '../../infrastructure/http/auth.api';

/**
 * Hook: Atualizar perfil do usuário autenticado.
 */
export function useUpdateProfile() {
  const updateProfile = useCallback(
    async (data: UpdateProfileData, photoFile?: File): Promise<UserResponse> => {
      let response = await authApi.updateProfile(data);
      if (photoFile) {
        response = await authApi.uploadPhoto(photoFile);
      }
      return response.data.data;
    },
    []
  );

  return { updateProfile };
}
