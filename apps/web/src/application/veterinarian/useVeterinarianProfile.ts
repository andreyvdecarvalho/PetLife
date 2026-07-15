import { useState, useCallback } from 'react';
import api from '../../infrastructure/http/api';
import type { Veterinarian } from '../../domain/models/Veterinarian';

export function useVeterinarianProfile() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const createProfile = async (data: Partial<Veterinarian>) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.post<Veterinarian>('/veterinarians', data);
      return response.data;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao criar perfil');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const getMyProfile = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get<Veterinarian>('/veterinarians/me');
      return response.data;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao obter perfil.');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const updateProfile = async (data: Partial<Veterinarian>) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.put<Veterinarian>('/veterinarians/me', data);
      return response.data;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao atualizar perfil.');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const updateAvailability = async (status: 'AVAILABLE' | 'UNAVAILABLE') => {
    setLoading(true);
    try {
      await api.patch('/veterinarians/me/availability', { status });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao atualizar disponibilidade.');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const updateEmergency = async (emergencyOnDuty: boolean) => {
    setLoading(true);
    try {
      await api.patch('/veterinarians/me/emergency', { emergencyOnDuty });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao atualizar plantão.');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { createProfile, getMyProfile, updateProfile, updateAvailability, updateEmergency, loading, error };
}
