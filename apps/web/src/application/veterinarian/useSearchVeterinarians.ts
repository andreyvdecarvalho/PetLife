import { useState, useCallback } from 'react';
import { veterinarianApi, SearchParams, PageResponse } from '../../infrastructure/http/veterinarian.api';
import type { Veterinarian } from '../../domain/models/Veterinarian';

export function useSearchVeterinarians() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<PageResponse<Veterinarian> | null>(null);

  const search = useCallback(async (params: SearchParams) => {
    setLoading(true);
    setError(null);
    try {
      const result = await veterinarianApi.search(params);
      setData(result);
      return result;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar veterinários.');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return { search, data, loading, error };
}
