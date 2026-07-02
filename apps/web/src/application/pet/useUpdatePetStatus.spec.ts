import { renderHook, act } from '@testing-library/react';
import { useUpdatePetStatus } from './useUpdatePetStatus';
import { petApi } from '../../infrastructure/http/pet.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/pet.api', () => ({
  petApi: {
    updateStatus: vi.fn(),
  },
}));

describe('useUpdatePetStatus Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should update pet status successfully', async () => {
    const mockPet = { id: 'pet-123', name: 'Max', species: 'DOG', sex: 'MALE', status: 'ARCHIVED', userId: 'user-456' };
    (petApi.updateStatus as any).mockResolvedValue({
      data: { data: mockPet },
    });

    const { result } = renderHook(() => useUpdatePetStatus());

    let updated;
    await act(async () => {
      updated = await result.current.updatePetStatus('pet-123', 'ARCHIVED');
    });

    expect(petApi.updateStatus).toHaveBeenCalledWith('pet-123', 'ARCHIVED');
    expect(updated).toEqual(mockPet);
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should set error message if update status fails', async () => {
    const errorMessage = 'Pet nao encontrado.';
    (petApi.updateStatus as any).mockRejectedValue({
      response: {
        data: {
          error: {
            message: errorMessage,
          },
        },
      },
    });

    const { result } = renderHook(() => useUpdatePetStatus());

    await act(async () => {
      await expect(
        result.current.updatePetStatus('pet-123', 'ARCHIVED')
      ).rejects.toThrow(errorMessage);
    });

    expect(result.current.error).toBe(errorMessage);
    expect(result.current.loading).toBe(false);
  });
});
