export type NotificationType =
  | 'VACCINATION_DUE'
  | 'MEDICATION_DOSE'
  | 'CONSULTATION_FOLLOWUP'
  | 'GROOMING_DUE'
  | 'PET_BIRTHDAY'
  | 'MEDICATION_LATE'
  | 'SYSTEM';

export interface NotificationMessage {
  id: string;
  userId: string;
  type: NotificationType;
  title: string;
  body: string;
  targetId: string | null;
  read: boolean;
  createdAt: string;
}

export interface NotificationPreferences {
  userId: string;
  pushEnabled: boolean;
  emailEnabled: boolean;
  vaccines: boolean;
  medications: boolean;
  appointments: boolean;
  grooming: boolean;
  marketing: boolean;
  doNotDisturbStart: string; // "HH:mm"
  doNotDisturbEnd: string; // "HH:mm"
}
