import { useState, useCallback } from 'react';
import type { Medication, CreateMedicationData, UpdateAdministrationData, MedicationAdherence } from '../../domain/pet/Medication';
import { medicationApi } from '../../infrastructure/http/medication.api';

export function useMedications(petId: string) {
  const [medications, setMedications] = useState<Medication[]>([]);
  const [adherence, setAdherence] = useState<MedicationAdherence | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchMedications = useCallback(async () => {
    if (!petId) return;
    setLoading(true); setError(null);
    try { setMedications(await medicationApi.listMedications(petId)); }
    catch (err: any) { setError(err.response?.data?.message || 'Erro ao buscar medicamentos'); }
    finally { setLoading(false); }
  }, [petId]);

  const fetchAdherence = useCallback(async () => {
    if (!petId) return;
    setLoading(true); setError(null);
    try { setAdherence(await medicationApi.getAdherence(petId)); }
    catch (err: any) { setError(err.response?.data?.message || 'Erro ao buscar aderência'); }
    finally { setLoading(false); }
  }, [petId]);

  const runWithRefresh = async (apiCall: () => Promise<any>, errorMsg: string) => {
    setLoading(true); setError(null);
    try {
      await apiCall();
      await fetchMedications();
      await fetchAdherence();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || errorMsg);
      return false;
    } finally { setLoading(false); }
  };

  const createMedication = (data: CreateMedicationData) =>
    runWithRefresh(() => medicationApi.createMedication(petId, data), 'Erro ao criar tratamento');

  const updateAdministration = (doseId: string, data: UpdateAdministrationData) =>
    runWithRefresh(() => medicationApi.updateAdministration(doseId, data), 'Erro ao registrar dose');

  const stopMedication = (id: string) =>
    runWithRefresh(() => medicationApi.stopMedication(id), 'Erro ao interromper tratamento');

  return {
    medications, adherence, loading, error,
    fetchMedications, fetchAdherence, createMedication, updateAdministration, stopMedication
  };
}
