import { useState, useEffect, useCallback } from 'react';
import { petApi } from '../../infrastructure/http/pet.api';
import type { Pet } from '../../domain/pet/Pet';

export function useGetPetById(id: string | undefined) {
  const [pet, setPet] = useState<Pet | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPet = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const response = await petApi.getById(id);
      setPet(response.data.data);
    } catch (err: any) {
      setError(err.response?.data?.error?.message || 'Erro ao carregar dados do pet.');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchPet();
  }, [fetchPet]);

  return { pet, loading, error, refetch: fetchPet };
}
