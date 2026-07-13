import { useState } from 'react';
import { api } from '../../infrastructure/http/api';
import { Veterinarian } from '../../domain/models/Veterinarian';

export function useVeterinarianProfile() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const createProfile = async (data: Partial<Veterinarian>) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.post<Veterinarian>('/api/v1/veterinarians', data);
      return response.data;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao criar perfil');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { createProfile, loading, error };
}
