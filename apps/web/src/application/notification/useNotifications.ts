import { useState, useCallback } from 'react';
import { notificationApi } from '../../infrastructure/http/notification.api';
import type { NotificationMessage } from '../../domain/notification/Notification';

export const useNotifications = () => {
  const [notifications, setNotifications] = useState<NotificationMessage[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchNotifications = useCallback(async (page = 0) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await notificationApi.getNotifications(page);
      const data = response.data.data || [];
      setNotifications(prev => (page === 0 ? data : [...prev, ...data]));
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar notificações');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const markAsRead = useCallback(async (id: string) => {
    try {
      await notificationApi.markAsRead(id);
      setNotifications(prev =>
        prev.map(n => (n.id === id ? { ...n, read: true } : n))
      );
    } catch (err: any) {
      console.error(err);
    }
  }, []);

  const markAllAsRead = useCallback(async () => {
    try {
      await notificationApi.markAllAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, read: true })));
    } catch (err: any) {
      console.error(err);
    }
  }, []);

  return {
    notifications,
    isLoading,
    error,
    fetchNotifications,
    markAsRead,
    markAllAsRead,
  };
};
