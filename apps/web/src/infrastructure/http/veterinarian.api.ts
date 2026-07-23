import api from './api';
import type { Veterinarian } from '../../domain/models/Veterinarian';

export interface SearchParams {
  lat?: number;
  lng?: number;
  radiusKm?: number;
  modality?: string;
  specialty?: string;
  emergency?: boolean;
  page?: number;
  size?: number;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export const veterinarianApi = {
  getProfile: async (id: string): Promise<Veterinarian> => {
    const response = await api.get<Veterinarian>(`/veterinarians/${id}`);
    return response.data;
  },

  search: async (params: SearchParams): Promise<PageResponse<Veterinarian>> => {
    const response = await api.get<PageResponse<Veterinarian>>('/veterinarians/search', { params });
    return response.data;
  },

  listFavorites: async (): Promise<Veterinarian[]> => {
    const response = await api.get<Veterinarian[]>('/veterinarians/favorites');
    return response.data;
  },

  addFavorite: async (vetId: string): Promise<void> => {
    await api.post(`/veterinarians/${vetId}/favorite`);
  },

  removeFavorite: async (vetId: string): Promise<void> => {
    await api.delete(`/veterinarians/${vetId}/favorite`);
  },

  createProfile: async (data: Partial<Veterinarian>): Promise<Veterinarian> => {
    const response = await api.post<Veterinarian>('/veterinarians', data);
    return response.data;
  },

  getMyProfile: async (): Promise<Veterinarian> => {
    const response = await api.get<Veterinarian>('/veterinarians/me');
    return response.data;
  },

  updateProfile: async (data: Partial<Veterinarian>): Promise<Veterinarian> => {
    const response = await api.put<Veterinarian>('/veterinarians/me', data);
    return response.data;
  },

  updateAvailability: async (status: 'AVAILABLE' | 'UNAVAILABLE'): Promise<void> => {
    await api.patch('/veterinarians/me/availability', { status });
  },

  updateEmergency: async (emergencyOnDuty: boolean): Promise<void> => {
    await api.patch('/veterinarians/me/emergency', { emergencyOnDuty });
  },
};
