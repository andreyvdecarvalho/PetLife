import { renderHook, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { useVeterinarianProfile } from './useVeterinarianProfile';
import api from '../../infrastructure/http/api';

vi.mock('../../infrastructure/http/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
  }
}));

describe('useVeterinarianProfile', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should create profile successfully', async () => {
    (api.post as any).mockResolvedValueOnce({ data: { id: '1', fullName: 'Dr. Test' } });

    const { result } = renderHook(() => useVeterinarianProfile());

    let response;
    await act(async () => {
      response = await result.current.createProfile({ fullName: 'Dr. Test' });
    });

    expect(response).toEqual({ id: '1', fullName: 'Dr. Test' });
    expect(api.post).toHaveBeenCalledWith('/veterinarians', { fullName: 'Dr. Test' });
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should handle error on create profile', async () => {
    (api.post as any).mockRejectedValueOnce({ response: { data: { message: 'Erro na criação' } } });

    const { result } = renderHook(() => useVeterinarianProfile());

    await act(async () => {
      try {
        await result.current.createProfile({});
      } catch (e) {
        // expected error
      }
    });

    expect(result.current.error).toBe('Erro na criação');
    expect(result.current.loading).toBe(false);
  });

  it('should get profile successfully', async () => {
    (api.get as any).mockResolvedValueOnce({ data: { id: '1' } });

    const { result } = renderHook(() => useVeterinarianProfile());

    let response;
    await act(async () => {
      response = await result.current.getMyProfile();
    });

    expect(response).toEqual({ id: '1' });
    expect(api.get).toHaveBeenCalledWith('/veterinarians/me');
  });

  it('should handle get profile error', async () => {
    (api.get as any).mockRejectedValueOnce({ response: { data: { message: 'Not found' } } });

    const { result } = renderHook(() => useVeterinarianProfile());

    await act(async () => {
      try {
        await result.current.getMyProfile();
      } catch (e) {
        // expected error
      }
    });

    expect(result.current.error).toBe('Not found');
  });

  it('should update profile successfully', async () => {
    (api.put as any).mockResolvedValueOnce({ data: { id: '1', fullName: 'Updated' } });

    const { result } = renderHook(() => useVeterinarianProfile());

    let response;
    await act(async () => {
      response = await result.current.updateProfile({ fullName: 'Updated' });
    });

    expect(response).toEqual({ id: '1', fullName: 'Updated' });
    expect(api.put).toHaveBeenCalledWith('/veterinarians/me', { fullName: 'Updated' });
  });

  it('should update availability successfully', async () => {
    (api.patch as any).mockResolvedValueOnce({});

    const { result } = renderHook(() => useVeterinarianProfile());

    await act(async () => {
      await result.current.updateAvailability('AVAILABLE');
    });

    expect(api.patch).toHaveBeenCalledWith('/veterinarians/me/availability', { status: 'AVAILABLE' });
  });

  it('should update emergency successfully', async () => {
    (api.patch as any).mockResolvedValueOnce({});

    const { result } = renderHook(() => useVeterinarianProfile());

    await act(async () => {
      await result.current.updateEmergency(true);
    });

    expect(api.patch).toHaveBeenCalledWith('/veterinarians/me/emergency', { emergencyOnDuty: true });
  });
});
