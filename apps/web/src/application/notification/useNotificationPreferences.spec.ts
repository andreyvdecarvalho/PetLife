import { renderHook, act } from '@testing-library/react';
import { useNotificationPreferences } from './useNotificationPreferences';
import { notificationApi } from '../../infrastructure/http/notification.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/notification.api', () => ({
  notificationApi: {
    getPreferences: vi.fn(),
    updatePreferences: vi.fn(),
    registerDeviceToken: vi.fn(),
  },
}));

describe('useNotificationPreferences Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch preferences successfully', async () => {
    const mockPrefs = {
      userId: 'user-123',
      pushEnabled: true,
      emailEnabled: true,
      vaccines: true,
      medications: true,
      appointments: true,
      grooming: true,
      marketing: false,
      doNotDisturbStart: '22:00',
      doNotDisturbEnd: '07:00',
    };

    (notificationApi.getPreferences as any).mockResolvedValue({
      data: { data: mockPrefs }
    });

    const { result } = renderHook(() => useNotificationPreferences());

    await act(async () => {
      await result.current.fetchPreferences();
    });

    expect(notificationApi.getPreferences).toHaveBeenCalled();
    expect(result.current.preferences).toEqual(mockPrefs);
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should save preferences successfully', async () => {
    const newPrefs = {
      pushEnabled: false,
      emailEnabled: false,
      vaccines: false,
      medications: false,
      appointments: false,
      grooming: false,
      marketing: false,
      doNotDisturbStart: '23:00',
      doNotDisturbEnd: '06:00',
    };

    (notificationApi.updatePreferences as any).mockResolvedValue({
      data: { data: { userId: 'user-123', ...newPrefs } }
    });

    const { result } = renderHook(() => useNotificationPreferences());

    let success = false;
    await act(async () => {
      success = await result.current.savePreferences(newPrefs);
    });

    expect(success).toBe(true);
    expect(notificationApi.updatePreferences).toHaveBeenCalledWith(newPrefs);
    expect(result.current.preferences?.pushEnabled).toBe(false);
    expect(result.current.isSaving).toBe(false);
  });

  it('should register device token successfully', async () => {
    (notificationApi.registerDeviceToken as any).mockResolvedValue({});

    const { result } = renderHook(() => useNotificationPreferences());

    await act(async () => {
      await result.current.registerToken('my-token');
    });

    expect(notificationApi.registerDeviceToken).toHaveBeenCalledWith('my-token');
  });
});
