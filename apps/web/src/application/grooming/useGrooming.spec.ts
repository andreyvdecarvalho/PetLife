import { renderHook, act } from '@testing-library/react';
import { useGrooming } from './useGrooming';
import { groomingApi } from '../../infrastructure/http/grooming.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/grooming.api', () => ({
  groomingApi: {
    listGroomings: vi.fn(),
    createGrooming: vi.fn(),
    updateGrooming: vi.fn(),
    uploadPhoto: vi.fn(),
  },
}));

describe('useGrooming Hook', () => {
  const petId = 'pet-123';

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch groomings list successfully', async () => {
    const mockGroomings = [
      { id: 'g1', petId, type: 'BATH' as const, date: '2026-07-01', photos: [] },
    ];
    (groomingApi.listGroomings as any).mockResolvedValue(mockGroomings);

    const { result } = renderHook(() => useGrooming(petId));

    expect(result.current.groomings).toEqual([]);
    expect(result.current.loading).toBe(false);

    await act(async () => {
      await result.current.fetchGroomings();
    });

    expect(groomingApi.listGroomings).toHaveBeenCalledWith(petId);
    expect(result.current.groomings).toEqual(mockGroomings);
    expect(result.current.loading).toBe(false);
  });

  it('should handle fetch error correctly', async () => {
    (groomingApi.listGroomings as any).mockRejectedValue({
      response: { data: { message: 'Erro de listagem' } },
    });

    const { result } = renderHook(() => useGrooming(petId));

    await act(async () => {
      await result.current.fetchGroomings();
    });

    expect(result.current.error).toBe('Erro de listagem');
    expect(result.current.loading).toBe(false);
  });

  it('should add grooming successfully', async () => {
    const mockGrooming = { id: 'g2', petId, type: 'GROOMING' as const, date: '2026-07-02', photos: [] };
    const createData = { type: 'GROOMING', date: '2026-07-02' };

    (groomingApi.createGrooming as any).mockResolvedValue(mockGrooming);

    const { result } = renderHook(() => useGrooming(petId));

    let created = null;
    await act(async () => {
      created = await result.current.addGrooming(createData);
    });

    expect(created).toEqual(mockGrooming);
    expect(groomingApi.createGrooming).toHaveBeenCalledWith(petId, createData);
  });

  it('should handle add grooming error', async () => {
    (groomingApi.createGrooming as any).mockRejectedValue({
      response: { data: { message: 'Erro ao criar' } },
    });

    const { result } = renderHook(() => useGrooming(petId));

    let created = null;
    await act(async () => {
      created = await result.current.addGrooming({ type: 'BATH', date: '2026-07-01' });
    });

    expect(created).toBeNull();
    expect(result.current.error).toBe('Erro ao criar');
  });

  it('should update grooming successfully', async () => {
    const updateData = { type: 'GROOMING', date: '2026-07-03' };
    (groomingApi.updateGrooming as any).mockResolvedValue({});

    const { result } = renderHook(() => useGrooming(petId));

    let success = false;
    await act(async () => {
      success = await result.current.updateGrooming('g1', updateData);
    });

    expect(success).toBe(true);
    expect(groomingApi.updateGrooming).toHaveBeenCalledWith(petId, 'g1', updateData);
  });

  it('should upload photo successfully', async () => {
    const file = new File([''], 'file.jpg', { type: 'image/jpeg' });
    (groomingApi.uploadPhoto as any).mockResolvedValue({});

    const { result } = renderHook(() => useGrooming(petId));

    let success = false;
    await act(async () => {
      success = await result.current.uploadPhoto('g1', file, 'BEFORE');
    });

    expect(success).toBe(true);
    expect(groomingApi.uploadPhoto).toHaveBeenCalledWith(petId, 'g1', file, 'BEFORE');
  });
});
