import api from './api';
import type {
  Medication,
  CreateMedicationData,
  MedicationAdministration,
  UpdateAdministrationData,
  MedicationAdherence
} from '../../domain/pet/Medication';

export const medicationApi = {
  createMedication: async (petId: string, data: CreateMedicationData): Promise<Medication> => {
    const response = await api.post<{ data: Medication }>(`/pets/${petId}/medications`, data);
    return response.data.data;
  },

  listMedications: async (petId: string): Promise<Medication[]> => {
    const response = await api.get<{ data: Medication[] }>(`/pets/${petId}/medications`);
    return response.data.data;
  },

  updateMedication: async (petId: string, medicationId: string, data: Partial<CreateMedicationData>): Promise<Medication> => {
    const response = await api.put<{ data: Medication }>(`/pets/${petId}/medications/${medicationId}`, data);
    return response.data.data;
  },

  updateAdministration: async (doseId: string, data: UpdateAdministrationData): Promise<MedicationAdministration> => {
    const response = await api.patch<{ data: MedicationAdministration }>(`/medications/doses/${doseId}`, data);
    return response.data.data;
  },

  stopMedication: async (id: string): Promise<Medication> => {
    const response = await api.patch<{ data: Medication }>(`/medications/${id}/stop`);
    return response.data.data;
  },

  getAdherence: async (petId: string): Promise<MedicationAdherence> => {
    const response = await api.get<{ data: MedicationAdherence }>(`/pets/${petId}/medications/adherence`);
    return response.data.data;
  },
};
