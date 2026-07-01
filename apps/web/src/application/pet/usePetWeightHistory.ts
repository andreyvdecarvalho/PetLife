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

  useEffect(() => {
    let isMounted = true;
    setLoading(true);
    petApi
      .getWeightHistory(petId)
      .then((response) => {
        if (isMounted) {
          setData(response.data.data);
          setError(null);
        }
      })
      .catch((err) => {
        if (isMounted) {
          setError(err.response?.data?.error?.message || 'Failed to fetch weight history.');
        }
      })
      .finally(() => {
        if (isMounted) {
          setLoading(false);
        }
      });
    return () => {
      isMounted = false;
    };
  }, [petId]);

  return { data, loading, error };
}
