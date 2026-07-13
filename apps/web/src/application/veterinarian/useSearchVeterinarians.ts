import { useState, useCallback } from 'react';
import { api } from '../../infrastructure/http/api';
import { Veterinarian } from '../../domain/models/Veterinarian';

interface SearchParams {
  lat?: number;
  lng?: number;
  radiusKm?: number;
  modality?: string;
  specialty?: string;
  emergency?: boolean;
  page?: number;
  size?: number;
}

interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export function useSearchVeterinarians() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<PageResponse<Veterinarian> | null>(null);

  const search = useCallback(async (params: SearchParams) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get<PageResponse<Veterinarian>>('/veterinarians/search', { params });
      setData(response.data);
      return response.data;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar veterinários.');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return { search, data, loading, error };
}
