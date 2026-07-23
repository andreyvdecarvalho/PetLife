import { useState, useCallback } from 'react';
import { veterinarianApi } from '../../infrastructure/http/veterinarian.api';
import type { Veterinarian } from '../../domain/models/Veterinarian';

export function useVetFavorites() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [favorites, setFavorites] = useState<Veterinarian[]>([]);

  const listFavorites = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await veterinarianApi.listFavorites();
      setFavorites(data);
      return data;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar favoritos.');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const addFavorite = useCallback(async (vetId: string) => {
    setLoading(true);
    setError(null);
    try {
      await veterinarianApi.addFavorite(vetId);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao favoritar.');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const removeFavorite = useCallback(async (vetId: string) => {
    setLoading(true);
    setError(null);
    try {
      await veterinarianApi.removeFavorite(vetId);
      setFavorites(prev => prev.filter(v => v.id !== vetId));
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao remover favorito.');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return { listFavorites, addFavorite, removeFavorite, favorites, loading, error };
}
