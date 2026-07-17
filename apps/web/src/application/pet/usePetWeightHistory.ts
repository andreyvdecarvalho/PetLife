import { useState, useEffect } from 'react';
import { petApi } from '../../infrastructure/http/pet.api';
import type { WeightRecordResponse } from '../../infrastructure/dto/WeightRecordResponse';

/**
 * Hook to fetch weight history for a pet.
 * Returns data, loading flag and error.
 */
export function usePetWeightHistory(petId: string) {
  const [data, setData] = useState<WeightRecordResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchHistory = () => {
    setLoading(true);
    petApi
      .getWeightHistory(petId)
      .then((response) => {
        setData(response.data.data);
        setError(null);
      })
      .catch((err) => {
        setError(err.response?.data?.error?.message || 'Failed to fetch weight history.');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchHistory();
  }, [petId]);

  const deleteWeight = async (weightId: string) => {
    try {
      await petApi.deleteWeightHistory(petId, weightId);
      setData(prev => prev.filter(w => w.id !== weightId));
    } catch (err: any) {
      throw new Error(err.response?.data?.error?.message || 'Failed to delete weight record.');
    }
  };

  const updateWeight = async (weightId: string, weightKg: number, recordedAt: string) => {
    try {
      await petApi.updateWeightHistory(petId, weightId, { weightKg, recordedAt });
      fetchHistory(); // Refresh to get sorted data
    } catch (err: any) {
      throw new Error(err.response?.data?.error?.message || 'Failed to update weight record.');
    }
  };

  return { data, loading, error, refresh: fetchHistory, deleteWeight, updateWeight };
}
