import { renderHook, act } from '@testing-library/react';
import { useGetPets } from './useGetPets';
import { petApi } from '../../infrastructure/http/pet.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/pet.api', () => ({
  petApi: {
    list: vi.fn(),
  },
}));

describe('useGetPets Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch pets list successfully', async () => {
    const mockPets = [
      { id: '1', name: 'Max', species: 'DOG', status: 'ACTIVE', userId: '1' },
      { id: '2', name: 'Luna', species: 'CAT', status: 'ACTIVE', userId: '1' },
    ];
    const mockMeta = { page: 0, perPage: 10, total: 2, totalPages: 1 };

    (petApi.list as any).mockResolvedValue({
      data: {
        data: mockPets,
        meta: mockMeta,
      },
    });

    const { result } = renderHook(() => useGetPets());

    expect(result.current.pets).toEqual([]);
    expect(result.current.isLoading).toBe(false);

    await act(async () => {
      await result.current.fetchPets(0, 10);
    });

    expect(petApi.list).toHaveBeenCalledWith(0, 10);
    expect(result.current.pets).toEqual(mockPets);
    expect(result.current.meta).toEqual(mockMeta);
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should handle API fetch failure correctly', async () => {
    const errorMessage = 'Falha de conexão com o banco.';
    (petApi.list as any).mockRejectedValue({
      response: {
        data: {
          error: {
            message: errorMessage,
          },
        },
      },
    });

    const { result } = renderHook(() => useGetPets());

    await act(async () => {
      await result.current.fetchPets();
    });

    expect(result.current.pets).toEqual([]);
    expect(result.current.error).toBe(errorMessage);
    expect(result.current.isLoading).toBe(false);
  });
});
