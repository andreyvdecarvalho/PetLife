import { renderHook, act } from '@testing-library/react';
import { useNotifications } from './useNotifications';
import { notificationApi } from '../../infrastructure/http/notification.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/notification.api', () => ({
  notificationApi: {
    getNotifications: vi.fn(),
    markAsRead: vi.fn(),
    markAllAsRead: vi.fn(),
  },
}));

describe('useNotifications Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch notifications successfully', async () => {
    const mockNotifications = [
      { id: '1', userId: 'user-1', type: 'SYSTEM', title: 'Aviso', body: 'Mensagem', read: false, createdAt: '2026-07-08T10:00:00' }
    ];

    (notificationApi.getNotifications as any).mockResolvedValue({
      data: { data: mockNotifications }
    });

    const { result } = renderHook(() => useNotifications());

    await act(async () => {
      await result.current.fetchNotifications(0);
    });

    expect(notificationApi.getNotifications).toHaveBeenCalledWith(0);
    expect(result.current.notifications).toEqual(mockNotifications);
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should mark a notification as read successfully', async () => {
    const mockNotifications = [
      { id: '1', userId: 'user-1', type: 'SYSTEM', title: 'Aviso', body: 'Mensagem', read: false, createdAt: '2026-07-08T10:00:00' }
    ];

    (notificationApi.getNotifications as any).mockResolvedValue({
      data: { data: mockNotifications }
    });
    (notificationApi.markAsRead as any).mockResolvedValue({});

    const { result } = renderHook(() => useNotifications());

    await act(async () => {
      await result.current.fetchNotifications(0);
    });

    await act(async () => {
      await result.current.markAsRead('1');
    });

    expect(notificationApi.markAsRead).toHaveBeenCalledWith('1');
    expect(result.current.notifications[0].read).toBe(true);
  });

  it('should mark all notifications as read successfully', async () => {
    const mockNotifications = [
      { id: '1', userId: 'user-1', type: 'SYSTEM', title: 'Aviso 1', body: 'Mensagem 1', read: false, createdAt: '2026-07-08T10:00:00' },
      { id: '2', userId: 'user-1', type: 'SYSTEM', title: 'Aviso 2', body: 'Mensagem 2', read: false, createdAt: '2026-07-08T10:05:00' }
    ];

    (notificationApi.getNotifications as any).mockResolvedValue({
      data: { data: mockNotifications }
    });
    (notificationApi.markAllAsRead as any).mockResolvedValue({});

    const { result } = renderHook(() => useNotifications());

    await act(async () => {
      await result.current.fetchNotifications(0);
    });

    await act(async () => {
      await result.current.markAllAsRead();
    });

    expect(notificationApi.markAllAsRead).toHaveBeenCalled();
    expect(result.current.notifications.every(n => n.read)).toBe(true);
  });
});
