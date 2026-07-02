import api from '../../services/api';
import { Vaccination, CreateVaccinationData } from '../../domain/pet/Vaccination';

export const vaccinationApi = {
  addVaccination: async (petId: string, data: CreateVaccinationData): Promise<Vaccination> => {
    const response = await api.post<{ data: Vaccination }>(`/api/v1/pets/${petId}/vaccines`, data);
    return response.data.data;
  },

  listVaccinations: async (petId: string): Promise<Vaccination[]> => {
    const response = await api.get<{ data: Vaccination[] }>(`/api/v1/pets/${petId}/vaccines`);
    return response.data.data;
  },

  updateVaccination: async (petId: string, vaccineId: string, data: CreateVaccinationData): Promise<Vaccination> => {
    const response = await api.put<{ data: Vaccination }>(`/api/v1/pets/${petId}/vaccines/${vaccineId}`, data);
    return response.data.data;
  },

  uploadProof: async (petId: string, vaccineId: string, file: File): Promise<Vaccination> => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post<{ data: Vaccination }>(`/api/v1/pets/${petId}/vaccines/${vaccineId}/proof`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data.data;
  },

  getSuggestions: async (species: string): Promise<string[]> => {
    const response = await api.get<{ data: string[] }>(`/api/v1/vaccines/suggestions`, {
      params: { species }
    });
    return response.data.data;
  },
};
