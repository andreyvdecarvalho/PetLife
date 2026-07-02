export interface Vaccination {
  id: string;
  petId: string;
  vaccineName: string;
  dateAdministered: string; // ISO format (YYYY-MM-DD)
  nextDoseDate?: string;
  veterinarian?: string;
  clinic?: string;
  batchNumber?: string;
  manufacturer?: string;
  proofUrl?: string;
  notes?: string;
  reminderActive: boolean;
  createdAt: string;
}

export interface CreateVaccinationData {
  vaccineName: string;
  dateAdministered: string;
  nextDoseDate?: string;
  veterinarian?: string;
  clinic?: string;
  batchNumber?: string;
  manufacturer?: string;
  proofUrl?: string;
  notes?: string;
  reminderActive?: boolean;
}
