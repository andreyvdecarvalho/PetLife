import { useState, useCallback } from 'react';
import type { Grooming } from '../../domain/pet/Grooming';
import { groomingApi, type CreateGroomingData } from '../../infrastructure/http/grooming.api';

export function useGrooming(petId: string) {
  const [groomings, setGroomings] = useState<Grooming[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchGroomings = useCallback(async () => {
    if (!petId) return;
    try {
      setLoading(true);
      setError(null);
      const data = await groomingApi.listGroomings(petId);
      setGroomings(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar serviços de banho e tosa');
    } finally {
      setLoading(false);
    }
  }, [petId]);

  const addGrooming = async (data: CreateGroomingData): Promise<Grooming | null> => {
    try {
      setLoading(true);
      setError(null);
      const result = await groomingApi.createGrooming(petId, data);
      await fetchGroomings();
      return result;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao adicionar serviço');
      return null;
    } finally {
      setLoading(false);
    }
  };

  const updateGrooming = async (groomingId: string, data: CreateGroomingData): Promise<boolean> => {
    try {
      setLoading(true);
      setError(null);
      await groomingApi.updateGrooming(petId, groomingId, data);
      await fetchGroomings();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao atualizar serviço');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const uploadPhoto = async (groomingId: string, file: File, type: 'BEFORE' | 'AFTER'): Promise<boolean> => {
    try {
      setLoading(true);
      setError(null);
      await groomingApi.uploadPhoto(petId, groomingId, file, type);
      await fetchGroomings();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao fazer upload da foto');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    groomings,
    loading,
    error,
    fetchGroomings,
    addGrooming,
    updateGrooming,
    uploadPhoto,
  };
}
