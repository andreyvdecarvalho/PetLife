import { useState, useCallback } from 'react';
import api from '../../infrastructure/http/api';
import type { Veterinarian } from '../../domain/models/Veterinarian';

export function useGetVetProfile() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [vet, setVet] = useState<Veterinarian | null>(null);

  const getProfile = useCallback(async (id: string) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get<Veterinarian>(`/veterinarians/${id}`);
      setVet(response.data);
      return response.data;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao carregar perfil do veterinário.');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return { getProfile, vet, loading, error };
}
