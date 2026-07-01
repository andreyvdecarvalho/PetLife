import { useState } from 'react';
import { petApi } from '../../infrastructure/http/pet.api';
import type { CreatePetData } from '../../infrastructure/http/pet.api';
import type { Pet } from '../../domain/pet/Pet';

/**
 * Caso de Uso de Criação de Pet (Application Layer).
 * Orquestra a criação básica e o subsequente upload de foto do pet.
 * Princípio: Single Responsibility (SRP).
 */
export function useCreatePet() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const createPet = async (data: CreatePetData, photo?: File): Promise<Pet> => {
    setLoading(true);
    setError(null);
    try {
      // 1. Cria o pet básico na API
      const response = await petApi.create(data);
      let pet = response.data.data;

      // 2. Se houver arquivo de foto, faz o upload para associar ao pet recém-criado
      if (photo && pet.id) {
        const photoResponse = await petApi.uploadPhoto(pet.id, photo);
        pet = photoResponse.data.data;
      }

      return pet;
    } catch (err: any) {
      const message = err.response?.data?.error?.message || 'Ocorreu um erro ao cadastrar o pet.';
      setError(message);
      throw new Error(message);
    } finally {
      setLoading(false);
    }
  };

  return {
    createPet,
    loading,
    error,
  };
}
