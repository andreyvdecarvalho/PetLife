import { renderHook, act } from '@testing-library/react';
import { useCreatePet } from './useCreatePet';
import { petApi } from '../../infrastructure/http/pet.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/pet.api', () => ({
  petApi: {
    create: vi.fn(),
    uploadPhoto: vi.fn(),
  },
}));

describe('useCreatePet Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should create pet successfully without photo', async () => {
    const mockPet = {
      id: 'pet-123',
      userId: 'user-456',
      name: 'Max',
      species: 'DOG',
      sex: 'MALE',
      neutered: false,
      status: 'ACTIVE',
    };

    (petApi.create as any).mockResolvedValue({
      data: { data: mockPet },
    });

    const { result } = renderHook(() => useCreatePet());

    let created;
    await act(async () => {
      created = await result.current.createPet({
        name: 'Max',
        species: 'DOG',
        sex: 'MALE',
      });
    });

    expect(petApi.create).toHaveBeenCalledWith({
      name: 'Max',
      species: 'DOG',
      sex: 'MALE',
    });
    expect(petApi.uploadPhoto).not.toHaveBeenCalled();
    expect(created).toEqual(mockPet);
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should create pet and upload photo if photo is provided', async () => {
    const mockPet = {
      id: 'pet-123',
      userId: 'user-456',
      name: 'Max',
      species: 'DOG',
      sex: 'MALE',
      neutered: false,
      status: 'ACTIVE',
    };

    const mockPetWithPhoto = {
      ...mockPet,
      photoUrl: 'https://s3.amazonaws.com/petlife/pets/photo.jpg',
    };

    (petApi.create as any).mockResolvedValue({
      data: { data: mockPet },
    });

    (petApi.uploadPhoto as any).mockResolvedValue({
      data: { data: mockPetWithPhoto },
    });

    const { result } = renderHook(() => useCreatePet());
    const fakeFile = new File(['fake'], 'pet.jpg', { type: 'image/jpeg' });

    let created;
    await act(async () => {
      created = await result.current.createPet(
        {
          name: 'Max',
          species: 'DOG',
          sex: 'MALE',
        },
        fakeFile
      );
    });

    expect(petApi.create).toHaveBeenCalled();
    expect(petApi.uploadPhoto).toHaveBeenCalledWith('pet-123', fakeFile);
    expect(created).toEqual(mockPetWithPhoto);
    expect(result.current.error).toBeNull();
  });

  it('should set error message if API fails', async () => {
    const errorMessage = 'O limite de pets foi atingido.';
    (petApi.create as any).mockRejectedValue({
      response: {
        data: {
          error: {
            message: errorMessage,
          },
        },
      },
    });

    const { result } = renderHook(() => useCreatePet());

    await act(async () => {
      await expect(
        result.current.createPet({
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
