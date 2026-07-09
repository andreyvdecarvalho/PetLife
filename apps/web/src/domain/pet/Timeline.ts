export type TimelineEventType =
  | 'VACCINE'
  | 'CONSULTATION'
  | 'MEDICATION_START'
  | 'MEDICATION_END'
  | 'GROOMING'
  | 'PHOTO'
  | 'WEIGHT'
  | 'BIRTHDAY';

export interface TimelineEvent {
  id: string | null;
  type: TimelineEventType;
  date: string;
  title: string;
  description: string;
  icon: string;
  color: string;
  photoUrl?: string;
}
