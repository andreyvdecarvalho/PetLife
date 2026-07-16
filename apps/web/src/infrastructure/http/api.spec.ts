import { vi, describe, it, expect, beforeEach } from 'vitest';
import api, { setUnauthorizedCallback } from './api';
import { tokenStorage } from '../storage/tokenStorage';

vi.mock('../storage/tokenStorage', () => ({
  tokenStorage: {
    getAccessToken: vi.fn(),
    getRefreshToken: vi.fn(),
    clearTokens: vi.fn(),
  },
}));

describe('api client', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should inject authorization header if token exists', async () => {
    (tokenStorage.getAccessToken as any).mockReturnValue('token-123');

    // Simulate requesting config interceptor execution
    const requestInterceptor = (api.interceptors.request as any).handlers[0];
    const config = { headers: {} };
    const modifiedConfig = requestInterceptor.fulfilled(config);

    expect(tokenStorage.getAccessToken).toHaveBeenCalled();
    expect(modifiedConfig.headers.Authorization).toBe('Bearer token-123');
  });

  it('should not inject authorization header if token does not exist', () => {
    (tokenStorage.getAccessToken as any).mockReturnValue(null);

    const requestInterceptor = (api.interceptors.request as any).handlers[0];
    const config = { headers: {} };
    const modifiedConfig = requestInterceptor.fulfilled(config);

    expect(tokenStorage.getAccessToken).toHaveBeenCalled();
    expect(modifiedConfig.headers.Authorization).toBeUndefined();
  });

  it('should clear tokens and trigger callback on 401 response error', async () => {
    const unauthorizedSpy = vi.fn();
    setUnauthorizedCallback(unauthorizedSpy);

    const responseInterceptor = (api.interceptors.response as any).handlers[0];
    
    const mockError = {
      response: {
        status: 401,
      },
      config: {},
    };

    await expect(responseInterceptor.rejected(mockError)).rejects.toEqual(mockError);

    expect(tokenStorage.clearTokens).toHaveBeenCalled();
    expect(unauthorizedSpy).toHaveBeenCalled();
  });

  it('should pass through other response errors without clearing tokens', async () => {
    const unauthorizedSpy = vi.fn();
    setUnauthorizedCallback(unauthorizedSpy);

    const responseInterceptor = (api.interceptors.response as any).handlers[0];
    
    const mockError = {
      response: {
        status: 500,
      },
      config: {},
    };

    await expect(responseInterceptor.rejected(mockError)).rejects.toEqual(mockError);

    expect(tokenStorage.clearTokens).not.toHaveBeenCalled();
    expect(unauthorizedSpy).not.toHaveBeenCalled();
  });
});
