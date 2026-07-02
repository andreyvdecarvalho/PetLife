import { renderHook, act } from '@testing-library/react';
import { AuthProvider, useAuth } from './AuthContext';
import api from '../infrastructure/http/api';
import React from 'react';
import { vi, describe, it, expect, beforeEach, Mock } from 'vitest';

vi.mock('../infrastructure/http/api');

describe('AuthContext', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <AuthProvider>{children}</AuthProvider>
  );

  it('should initialize with no user and loading false if no token', async () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    
    // Wait for the rehydration effect to complete (it's async)
    await new Promise((resolve) => setTimeout(resolve, 0));
    
    expect(result.current.user).toBeNull();
    expect(result.current.isAuthenticated).toBe(false);
  });

  it('should rehydrate user if token exists', async () => {
    localStorage.setItem('@PetLife:accessToken', 'fake-token');
    (api.get as Mock).mockResolvedValue({
      data: { data: { id: '1', name: 'Test User' } }
    });

    const { result } = renderHook(() => useAuth(), { wrapper });
    
    // wait for async effect
    await act(async () => {
      await new Promise((resolve) => setTimeout(resolve, 10));
    });

    expect(result.current.user).toEqual({ id: '1', name: 'Test User' });
    expect(result.current.isAuthenticated).toBe(true);
  });

  it('should logout if rehydration fails', async () => {
    localStorage.setItem('@PetLife:accessToken', 'fake-token');
    (api.get as Mock).mockRejectedValue(new Error('Invalid token'));

    const { result } = renderHook(() => useAuth(), { wrapper });
    
    await act(async () => {
      await new Promise((resolve) => setTimeout(resolve, 10));
    });

    expect(result.current.user).toBeNull();
    expect(localStorage.getItem('@PetLife:accessToken')).toBeNull();
  });

  it('should login and set tokens and user', async () => {
    (api.post as Mock).mockResolvedValue({
      data: { data: { accessToken: 'acc', refreshToken: 'ref' } }
    });
    (api.get as Mock).mockResolvedValue({
      data: { data: { id: '1', name: 'Logged In' } }
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await result.current.login('test@test.com', 'password');
    });

    expect(localStorage.getItem('@PetLife:accessToken')).toBe('acc');
    expect(result.current.user?.name).toBe('Logged In');
  });

  it('should handle loginWithGoogle', async () => {
    (api.post as Mock).mockResolvedValue({
      data: { data: { accessToken: 'g-acc', refreshToken: 'g-ref' } }
    });
    (api.get as Mock).mockResolvedValue({
      data: { data: { id: '1', name: 'Google User' } }
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await result.current.loginWithGoogle('token');
    });

    expect(result.current.user?.name).toBe('Google User');
  });

  it('should handle register', async () => {
    (api.post as Mock).mockResolvedValue({
      data: { data: { accessToken: 'r-acc', refreshToken: 'r-ref' } }
    });
    (api.get as Mock).mockResolvedValue({
      data: { data: { id: '1', name: 'New User' } }
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await result.current.register('New User', 'test@test.com', 'pwd');
    });

    expect(result.current.user?.name).toBe('New User');
  });

  it('should update profile', async () => {
    (api.put as Mock).mockResolvedValue({
      data: { data: { id: '1', name: 'Updated' } }
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await result.current.updateProfile('Updated', 't@t.com', 'url', 'UTC');
    });

    expect(result.current.user?.name).toBe('Updated');
  });

  it('should delete account and logout', async () => {
    (api.delete as Mock).mockResolvedValue({});
    localStorage.setItem('@PetLife:accessToken', 'token');

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await result.current.deleteAccount();
    });

    expect(localStorage.getItem('@PetLife:accessToken')).toBeNull();
  });
});
