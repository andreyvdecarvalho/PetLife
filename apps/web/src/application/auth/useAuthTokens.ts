import { authApi, type UserResponse } from '../../infrastructure/http/auth.api';
import { tokenStorage } from '../../infrastructure/storage/tokenStorage';

/**
 * Função auxiliar: salva tokens e retorna o perfil do usuário.
 * Elimina a duplicação tripla em login, register, googleLogin.
 * Princípio: DRY + SRP.
 */
export async function saveTokensAndGetProfile(
  accessToken: string,
  refreshToken: string
): Promise<UserResponse> {
  tokenStorage.setTokens(accessToken, refreshToken);
  const response = await authApi.getProfile();
  return response.data.data;
}
