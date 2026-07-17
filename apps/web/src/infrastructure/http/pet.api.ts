import api from './api';
import type { Pet, PetSex, PetSize, PetSpecies, PetStatus } from '../../domain/pet/Pet';

export interface CreatePetData {
  name: string;
  species: PetSpecies;
  breed?: string;
  sex: PetSex;
  birthDate?: string;
  weightKg?: number;
  size?: PetSize;
  neutered?: boolean;
  microchipId?: string;
  allergies?: string;
  notes?: string;
}

/**
 * Adaptador de Infraestrutura HTTP para gerenciamento de Pets.
 * Encapsula chamadas Axios de Pet.
 * Princípio: Single Responsibility (SRP) + Dependency Inversion (DIP).
 */
export const petApi = {
  create: (data: CreatePetData) =>
    api.post<{ data: Pet }>('/pets', data),

  uploadPhoto: (id: string, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post<{ data: Pet }>(`/pets/${id}/photo`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  list: (page = 0, size = 10, status?: PetStatus) =>
    api.get<{ data: Pet[]; meta?: { page: number; perPage: number; total: number; totalPages: number } }>(
      `/pets?page=${page}&size=${size}${status ? `&status=${status}` : ''}`
    ),

  getWeightHistory: (id: string) =>
    api.get<{ data: { weightKg: number; recordedAt: string }[] }>(`/pets/${id}/weight-history`),

  getById: (id: string) =>
    api.get<{ data: Pet }>(`/pets/${id}`),

  update: (id: string, data: CreatePetData) =>
    api.put<{ data: Pet }>(`/pets/${id}`, data),

  updateStatus: (id: string, status: PetStatus) =>
    api.patch<{ data: Pet }>(`/pets/${id}/status`, { status }),

  delete: (id: string) =>
    api.delete<void>(`/pets/${id}`),

  updateWeightHistory: (id: string, weightId: string, data: { weightKg: number; recordedAt: string }) =>
    api.put(`/pets/${id}/weight-history/${weightId}`, data),

  deleteWeightHistory: (id: string, weightId: string) =>
    api.delete(`/pets/${id}/weight-history/${weightId}`),
};
