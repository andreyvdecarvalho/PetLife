export type MedicationFrequency = 'ONCE' | 'DAILY' | 'TWICE_DAILY' | 'EVERY_8H' | 'EVERY_12H' | 'WEEKLY' | 'CUSTOM';

export type MedicationStatus = 'ACTIVE' | 'COMPLETED' | 'CANCELLED';

export type MedicationAdministrationStatus = 'PENDING' | 'TAKEN' | 'SKIPPED' | 'LATE';

export interface Medication {
  id: string;
  petId: string;
  name: string;
  dosage: string;
  frequency: MedicationFrequency;
  customFrequencyHours?: number;
  startDate: string; // ISO date string (YYYY-MM-DD)
  endDate?: string;  // ISO date string (YYYY-MM-DD)
  timesOfDay: string[];
  status: MedicationStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface MedicationAdministration {
  id: string;
  medicationId: string;
  medicationName: string;
  scheduledTime: string; // ISO datetime string
  administeredAt?: string; // ISO datetime string
  status: MedicationAdministrationStatus;
  skippedReason?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateMedicationData {
  name: string;
  dosage: string;
  frequency: MedicationFrequency;
  customFrequencyHours?: number;
  startDate: string;
  endDate?: string;
  timesOfDay: string[];
}

export interface UpdateAdministrationData {
  status: MedicationAdministrationStatus;
  skippedReason?: string;
}

export interface MedicationAdherence {
  adherenceRate: number;
  totalDoses: number;
  takenDoses: number;
  skippedDoses: number;
  lateDoses: number;
  pendingDoses: number;
}
