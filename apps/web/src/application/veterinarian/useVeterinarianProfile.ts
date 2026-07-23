import { useState, useCallback } from 'react';
import { veterinarianApi } from '../../infrastructure/http/veterinarian.api';
import type { Veterinarian } from '../../domain/models/Veterinarian';

export function useVeterinarianProfile() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const createProfile = async (data: Partial<Veterinarian>) => {
    setLoading(true);
    setError(null);
    try {
      return await veterinarianApi.createProfile(data);
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
      return await veterinarianApi.getMyProfile();
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
      return await veterinarianApi.updateProfile(data);
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
      await veterinarianApi.updateAvailability(status);
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
      await veterinarianApi.updateEmergency(emergencyOnDuty);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao atualizar plantão.');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { createProfile, getMyProfile, updateProfile, updateAvailability, updateEmergency, loading, error };
}
