import { useState } from 'react';
import { petApi } from '../../infrastructure/http/pet.api';
import type { PetStatus } from '../../domain/pet/Pet';

/**
 * Hook de aplicação para alterar o status (ativo/arquivado) de um pet.
 * Encapsula a chamada de API e os estados de loading e erro.
 * Princípio: Single Responsibility (SRP).
 */
export function useUpdatePetStatus() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updatePetStatus = async (id: string, status: PetStatus) => {
    setLoading(true);
    setError(null);
    try {
      const response = await petApi.updateStatus(id, status);
      return response.data.data;
    } catch (err: any) {
      const errMsg = err.response?.data?.error?.message || 'Falha ao atualizar status do pet.';
      setError(errMsg);
      throw new Error(errMsg);
    } finally {
      setLoading(false);
    }
  };

  return {
    updatePetStatus,
    loading,
    error,
  };
}
