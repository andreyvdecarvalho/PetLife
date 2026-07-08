import { vi, describe, it, expect, beforeEach } from 'vitest';
import { petApi } from './pet.api';
import api from './api';

vi.mock('./api', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
  },
}));

describe('petApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should call create API', async () => {
    const mockResponse = { data: { data: { id: 'pet-1', name: 'Max' } } };
    (api.post as any).mockResolvedValue(mockResponse);

    const result = await petApi.create({ name: 'Max' } as any);
    expect(api.post).toHaveBeenCalledWith('/pets', { name: 'Max' });
    expect(result).toEqual(mockResponse);
  });

  it('should call uploadPhoto API', async () => {
    const mockResponse = { data: { data: { id: 'pet-1', photoUrl: 'url' } } };
    (api.post as any).mockResolvedValue(mockResponse);

    const file = new File(['photo'], 'photo.jpg', { type: 'image/jpeg' });
    const result = await petApi.uploadPhoto('pet-1', file);
    
    expect(api.post).toHaveBeenCalledWith('/pets/pet-1/photo', expect.any(FormData), {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    expect(result).toEqual(mockResponse);
  });

  it('should call list API with custom pagination and status', async () => {
    const mockResponse = { data: { data: [{ id: 'pet-1' }] } };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await petApi.list(1, 5, 'ARCHIVED');
    expect(api.get).toHaveBeenCalledWith('/pets?page=1&size=5&status=ARCHIVED');
    expect(result).toEqual(mockResponse);
  });

  it('should call getWeightHistory API', async () => {
    const mockResponse = { data: [{ weightKg: 10, recordedAt: '2026-07-08' }] };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await petApi.getWeightHistory('pet-1');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-1/weight-history');
    expect(result).toEqual(mockResponse);
  });

  it('should call getById API', async () => {
    const mockResponse = { data: { id: 'pet-1' } };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await petApi.getById('pet-1');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-1');
    expect(result).toEqual(mockResponse);
  });

  it('should call update API', async () => {
    const mockResponse = { data: { id: 'pet-1' } };
    (api.put as any).mockResolvedValue(mockResponse);

    const result = await petApi.update('pet-1', { name: 'Max' } as any);
    expect(api.put).toHaveBeenCalledWith('/pets/pet-1', { name: 'Max' });
    expect(result).toEqual(mockResponse);
  });

  it('should call updateStatus API', async () => {
    const mockResponse = { data: { id: 'pet-1' } };
    (api.patch as any).mockResolvedValue(mockResponse);

    const result = await petApi.updateStatus('pet-1', 'ARCHIVED');
    expect(api.patch).toHaveBeenCalledWith('/pets/pet-1/status', { status: 'ARCHIVED' });
    expect(result).toEqual(mockResponse);
  });
});
