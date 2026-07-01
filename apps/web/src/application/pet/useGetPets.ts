import { useState, useCallback } from 'react';
import { petApi } from '../../infrastructure/http/pet.api';
import type { Pet } from '../../domain/pet/Pet';

/**
 * Hook de aplicação para obter lista de pets do tutor logado.
 * Encapsula lógica de busca, loading, erro e paginação.
 * Princípio: Single Responsibility (SRP).
 */
export function useGetPets() {
  const [pets, setPets] = useState<Pet[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [meta, setMeta] = useState<{ page: number; perPage: number; total: number; totalPages: number } | null>(null);

  const fetchPets = useCallback(async (page = 0, size = 10) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await petApi.list(page, size);
      setPets(response.data.data);
      if (response.data.meta) {
        setMeta(response.data.meta);
      }
    } catch (err: any) {
      const errMsg = err.response?.data?.error?.message || 'Falha ao buscar pets.';
      setError(errMsg);
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    pets,
    isLoading,
    error,
    meta,
    fetchPets,
  };
}
