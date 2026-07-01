import { useState } from 'react';
import { petApi } from '../../infrastructure/http/pet.api';
import type { CreatePetData } from '../../infrastructure/http/pet.api';
import type { Pet } from '../../domain/pet/Pet';

/**
 * Hook de aplicação para atualizar informações do perfil do pet.
 * Encapsula a chamada de API e os estados de loading e erro.
 * Princípio: Single Responsibility (SRP).
 */
export function useUpdatePet() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updatePet = async (id: string, data: CreatePetData) => {
    setLoading(true);
    setError(null);
    try {
      const response = await petApi.update(id, data);
      return response.data.data;
    } catch (err: any) {
      const errMsg = err.response?.data?.error?.message || 'Falha ao atualizar pet.';
      setError(errMsg);
      throw new Error(errMsg);
    } finally {
      setLoading(false);
    }
  };

  return {
    updatePet,
    loading,
    error,
  };
}
