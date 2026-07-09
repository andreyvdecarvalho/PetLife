export type GroomingType = 'BATH' | 'GROOMING' | 'BATH_AND_GROOMING';

export interface Grooming {
  id: string;
  petId: string;
  type: GroomingType;
  date: string;
  provider?: string;
  cost?: number;
  frequencyDays?: number;
  nextDate?: string;
  notes?: string;
  photos: string[];
}
