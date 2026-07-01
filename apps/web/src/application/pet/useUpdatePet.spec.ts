import { renderHook, act } from '@testing-library/react';
import { useUpdatePet } from './useUpdatePet';
import { petApi } from '../../infrastructure/http/pet.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/pet.api', () => ({
  petApi: {
    update: vi.fn(),
  },
}));

describe('useUpdatePet Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should update pet successfully', async () => {
    const mockPet = { id: 'pet-123', name: 'Max Updated', species: 'DOG', sex: 'MALE', userId: 'user-456' };
    (petApi.update as any).mockResolvedValue({
      data: { data: mockPet },
    });

    const { result } = renderHook(() => useUpdatePet());

    let updated;
    await act(async () => {
      updated = await result.current.updatePet('pet-123', {
        name: 'Max Updated',
        species: 'DOG',
        sex: 'MALE',
      });
    });

    expect(petApi.update).toHaveBeenCalledWith('pet-123', {
      name: 'Max Updated',
      species: 'DOG',
      sex: 'MALE',
    });
    expect(updated).toEqual(mockPet);
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should set error message if update fails', async () => {
    const errorMessage = 'Pet nao encontrado.';
    (petApi.update as any).mockRejectedValue({
      response: {
        data: {
          error: {
            message: errorMessage,
          },
        },
      },
    });

    const { result } = renderHook(() => useUpdatePet());

    await act(async () => {
      await expect(
        result.current.updatePet('pet-123', {
          name: 'Max',
          species: 'DOG',
          sex: 'MALE',
        })
      ).rejects.toThrow(errorMessage);
    });

    expect(result.current.error).toBe(errorMessage);
    expect(result.current.loading).toBe(false);
  });
});
