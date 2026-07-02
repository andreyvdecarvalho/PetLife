import { useState, useCallback } from 'react';
import { Vaccination, CreateVaccinationData } from '../../domain/pet/Vaccination';
import { vaccinationApi } from '../../infrastructure/http/vaccination.api';

export function useVaccinations(petId: string) {
  const [vaccinations, setVaccinations] = useState<Vaccination[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchVaccinations = useCallback(async () => {
    if (!petId) return;
    try {
      setLoading(true);
      setError(null);
      const data = await vaccinationApi.listVaccinations(petId);
      setVaccinations(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar vacinas');
    } finally {
      setLoading(false);
    }
  }, [petId]);

  const addVaccination = async (data: CreateVaccinationData) => {
    try {
      setLoading(true);
      setError(null);
      await vaccinationApi.addVaccination(petId, data);
      await fetchVaccinations();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao adicionar vacina');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const updateVaccination = async (vaccineId: string, data: CreateVaccinationData) => {
    try {
      setLoading(true);
      setError(null);
      await vaccinationApi.updateVaccination(petId, vaccineId, data);
      await fetchVaccinations();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao atualizar vacina');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const uploadProof = async (vaccineId: string, file: File) => {
    try {
      setLoading(true);
      setError(null);
      await vaccinationApi.uploadProof(petId, vaccineId, file);
      await fetchVaccinations();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao fazer upload do comprovante');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    vaccinations,
    loading,
    error,
    fetchVaccinations,
    addVaccination,
    updateVaccination,
    uploadProof
  };
}
