import { useState, useCallback } from 'react';
import { notificationApi } from '../../infrastructure/http/notification.api';
import type { NotificationPreferences } from '../../domain/notification/Notification';

export const useNotificationPreferences = () => {
  const [preferences, setPreferences] = useState<NotificationPreferences | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchPreferences = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await notificationApi.getPreferences();
      setPreferences(response.data.data);
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar preferências.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const savePreferences = useCallback(
    async (newPrefs: Omit<NotificationPreferences, 'userId'>) => {
      setIsSaving(true);
      setError(null);
      try {
        const response = await notificationApi.updatePreferences(newPrefs);
        setPreferences(response.data.data);
        return true;
      } catch (err: any) {
        setError(err.message || 'Erro ao salvar preferências.');
        return false;
      } finally {
        setIsSaving(false);
      }
    },
    []
  );

  const registerToken = useCallback(async (token: string) => {
    try {
      await notificationApi.registerDeviceToken(token);
    } catch (err: any) {
      console.error('Falha ao registrar token do dispositivo:', err);
    }
  }, []);

  return {
    preferences,
    isLoading,
    isSaving,
    error,
    fetchPreferences,
    savePreferences,
    registerToken,
  };
};
