/**
 * Abstração de acesso ao localStorage para tokens JWT.
 * Princípio: Dependency Inversion (DIP) — Clean Architecture.
 * Nenhuma camada de aplicação deve acessar localStorage diretamente.
 */

const ACCESS_TOKEN_KEY = '@PetLife:accessToken';
const REFRESH_TOKEN_KEY = '@PetLife:refreshToken';

export const tokenStorage = {
  getAccessToken: (): string | null =>
    localStorage.getItem(ACCESS_TOKEN_KEY),

  getRefreshToken: (): string | null =>
    localStorage.getItem(REFRESH_TOKEN_KEY),

  setTokens: (accessToken: string, refreshToken: string): void => {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  },

  clearTokens: (): void => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },

  hasToken: (): boolean =>
    localStorage.getItem(ACCESS_TOKEN_KEY) !== null,
};
