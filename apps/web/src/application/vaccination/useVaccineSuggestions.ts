import { useState, useCallback } from 'react';
import { vaccinationApi } from '../../infrastructure/http/vaccination.api';

export function useVaccineSuggestions(species?: string) {
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchSuggestions = useCallback(async () => {
    if (!species) return;
    try {
      setLoading(true);
      const data = await vaccinationApi.getSuggestions(species);
      setSuggestions(data);
    } catch (err) {
      console.error('Erro ao buscar sugestões de vacinas', err);
    } finally {
      setLoading(false);
    }
  }, [species]);

  return {
    suggestions,
    loading,
    fetchSuggestions
  };
}
