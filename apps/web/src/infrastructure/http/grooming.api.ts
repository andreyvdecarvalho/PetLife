import api from './api';
import type { Grooming } from '../../domain/pet/Grooming';

export interface CreateGroomingData {
  type: string;
  date: string;
  provider?: string;
  cost?: number;
  frequencyDays?: number;
  notes?: string;
}

export const groomingApi = {
  createGrooming: async (petId: string, data: CreateGroomingData): Promise<Grooming> => {
    const response = await api.post<{ data: Grooming }>(`/pets/${petId}/groomings`, data);
    return response.data.data;
  },

  listGroomings: async (petId: string): Promise<Grooming[]> => {
    const response = await api.get<{ data: Grooming[] }>(`/pets/${petId}/groomings`);
    return response.data.data;
  },

  updateGrooming: async (petId: string, groomingId: string, data: CreateGroomingData): Promise<Grooming> => {
    const response = await api.put<{ data: Grooming }>(`/pets/${petId}/groomings/${groomingId}`, data);
    return response.data.data;
  },

  uploadPhoto: async (petId: string, groomingId: string, file: File, type: 'BEFORE' | 'AFTER'): Promise<Grooming> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);
    const response = await api.post<{ data: Grooming }>(`/pets/${petId}/groomings/${groomingId}/photos`, formData);
    return response.data.data;
  },
};
