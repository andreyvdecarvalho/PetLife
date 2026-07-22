import { useEffect, useState, useCallback } from 'react';
import { getToken, onMessage } from 'firebase/messaging';
import { messaging } from '../../infrastructure/firebase/firebase';
import { notificationApi } from '../../infrastructure/http/notification.api';

export interface UsePushNotificationsProps {
  isAuthenticated: boolean;
  onNotificationReceived?: (title: string, body: string) => void;
}

export const usePushNotifications = ({ isAuthenticated, onNotificationReceived }: UsePushNotificationsProps) => {
  const [permissionGranted, setPermissionGranted] = useState<boolean>(false);
  const [fcmToken, setFcmToken] = useState<string | null>(null);

  const requestPermissionAndRegister = useCallback(async () => {
    try {
      const msg = await messaging();
      if (!msg) {
        console.warn('Push Notifications not supported in this environment.');
        return;
      }

      const permission = await Notification.requestPermission();
      if (permission === 'granted') {
        setPermissionGranted(true);

        const currentToken = await getToken(msg, {
          vapidKey: import.meta.env.VITE_FIREBASE_VAPID_KEY || 'mock-vapid-key'
        });

        if (currentToken) {
          setFcmToken(currentToken);
          try {
            await notificationApi.registerDeviceToken(currentToken);
            console.log('FCM token registered with backend successfully.');
          } catch (error) {
            console.error('Failed to register FCM token with backend:', error);
          }
        } else {
          console.warn('No registration token available. Request permission to generate one.');
        }
      } else {
        console.warn('Notification permission not granted.');
        setPermissionGranted(false);
      }
    } catch (error) {
      console.error('An error occurred while retrieving token:', error);
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      requestPermissionAndRegister();
    }
  }, [isAuthenticated, requestPermissionAndRegister]);

  // Handle incoming messages while the app is in the foreground
  useEffect(() => {
    const setupForegroundListener = async () => {
      const msg = await messaging();
      if (msg) {
        const unsubscribe = onMessage(msg, (payload) => {
          console.log('Message received in foreground:', payload);
          if (payload.notification && onNotificationReceived) {
            onNotificationReceived(payload.notification.title || '', payload.notification.body || '');
          }
        });
        return unsubscribe;
      }
    };

    let unsub: (() => void) | undefined;
    if (permissionGranted) {
      setupForegroundListener().then(fn => { if (fn) unsub = fn; });
    }

    return () => {
      if (unsub) unsub();
    };
  }, [permissionGranted, onNotificationReceived]);

  return { requestPermissionAndRegister, fcmToken, permissionGranted };
};
