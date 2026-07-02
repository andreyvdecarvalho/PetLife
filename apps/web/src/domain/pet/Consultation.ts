export interface Consultation {
  id: string;
  petId: string;
  date: string; // ISO format date-time
  veterinarian?: string;
  clinic?: string;
  reason: string;
  diagnosis?: string;
  prescriptions?: string;
  notes?: string;
  weightAtVisit?: number;
  followUpDate?: string; // ISO format date (YYYY-MM-DD)
  cost?: number;
  attachments: string[];
  createdAt: string;
  updatedAt?: string;
}

export interface CreateConsultationData {
  date: string;
  veterinarian?: string;
  clinic?: string;
  reason: string;
  diagnosis?: string;
  prescriptions?: string;
  notes?: string;
  weightAtVisit?: number;
  followUpDate?: string;
  cost?: number;
}
