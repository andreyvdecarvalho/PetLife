import api from '../../services/api';
import type { Pet, PetSex, PetSize, PetSpecies } from '../../domain/pet/Pet';

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
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
};
