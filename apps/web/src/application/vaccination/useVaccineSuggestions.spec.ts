import { renderHook, act } from '@testing-library/react';
import { useVaccineSuggestions } from './useVaccineSuggestions';
import { vaccinationApi } from '../../infrastructure/http/vaccination.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/vaccination.api', () => ({
  vaccinationApi: {
    getSuggestions: vi.fn(),
  },
}));

describe('useVaccineSuggestions Hook', () => {
  const species = 'DOG';

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch suggestions successfully', async () => {
    const mockSuggestions = ['Antirrábica', 'V10', 'Gripe Canina'];
    (vaccinationApi.getSuggestions as any).mockResolvedValue(mockSuggestions);

    const { result } = renderHook(() => useVaccineSuggestions(species));

    expect(result.current.suggestions).toEqual([]);
    expect(result.current.loading).toBe(false);

    await act(async () => {
      await result.current.fetchSuggestions();
    });

    expect(vaccinationApi.getSuggestions).toHaveBeenCalledWith(species);
    expect(result.current.suggestions).toEqual(mockSuggestions);
    expect(result.current.loading).toBe(false);
  });

  it('should not fetch if species is not provided', async () => {
    const { result } = renderHook(() => useVaccineSuggestions(undefined));

    await act(async () => {
      await result.current.fetchSuggestions();
    });

    expect(vaccinationApi.getSuggestions).not.toHaveBeenCalled();
    expect(result.current.suggestions).toEqual([]);
  });

  it('should handle error gracefully on suggestions fetch failure', async () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
    (vaccinationApi.getSuggestions as any).mockRejectedValue(new Error('API error'));

    const { result } = renderHook(() => useVaccineSuggestions(species));

    await act(async () => {
      await result.current.fetchSuggestions();
    });

    expect(vaccinationApi.getSuggestions).toHaveBeenCalledWith(species);
    expect(result.current.suggestions).toEqual([]);
    expect(result.current.loading).toBe(false);
    expect(consoleSpy).toHaveBeenCalledWith('Erro ao buscar sugestões de vacinas', expect.any(Error));

    consoleSpy.mockRestore();
  });
});
