import { describe, it, expect, beforeEach } from 'vitest';
import { tokenStorage } from './tokenStorage';

describe('tokenStorage utility', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('should store and retrieve access and refresh tokens', () => {
    tokenStorage.setTokens('access-123', 'refresh-456');

    expect(tokenStorage.getAccessToken()).toBe('access-123');
    expect(tokenStorage.getRefreshToken()).toBe('refresh-456');
    expect(tokenStorage.hasToken()).toBe(true);
  });

  it('should clear tokens correctly', () => {
    tokenStorage.setTokens('access-123', 'refresh-456');
    tokenStorage.clearTokens();

    expect(tokenStorage.getAccessToken()).toBeNull();
    expect(tokenStorage.getRefreshToken()).toBeNull();
    expect(tokenStorage.hasToken()).toBe(false);
  });
});
