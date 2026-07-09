import api from './api';
import type { NotificationMessage, NotificationPreferences } from '../../domain/notification/Notification';

export const notificationApi = {
  getNotifications: (page = 0, size = 20) => {
    return api.get<{ data: NotificationMessage[] }>(`/notifications?page=${page}&size=${size}`);
  },

  markAsRead: (id: string) => {
    return api.patch<void>(`/notifications/${id}/read`);
  },

  markAllAsRead: () => {
    return api.patch<void>('/notifications/read-all');
  },

  getPreferences: () => {
    return api.get<{ data: NotificationPreferences }>('/users/me/notification-preferences');
  },

  updatePreferences: (preferences: Omit<NotificationPreferences, 'userId'>) => {
    return api.put<{ data: NotificationPreferences }>('/users/me/notification-preferences', preferences);
  },

  registerDeviceToken: (token: string) => {
    return api.post<void>('/users/me/device-tokens', { token });
  },
};
