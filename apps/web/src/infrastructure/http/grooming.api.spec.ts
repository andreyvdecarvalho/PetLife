import { vi, describe, it, expect, beforeEach } from 'vitest';
import { groomingApi } from './grooming.api';
import api from './api';

vi.mock('./api', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
  },
}));

describe('groomingApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should call createGrooming API', async () => {
    const mockResponse = { data: { data: { id: 'g-1', type: 'BATH' } } };
    (api.post as any).mockResolvedValue(mockResponse);

    const result = await groomingApi.createGrooming('pet-123', { type: 'BATH', date: '2026-07-08' });
    expect(api.post).toHaveBeenCalledWith('/pets/pet-123/groomings', { type: 'BATH', date: '2026-07-08' });
    expect(result).toEqual(mockResponse.data.data);
  });

  it('should call listGroomings API', async () => {
    const mockResponse = { data: { data: [{ id: 'g-1', type: 'BATH' }] } };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await groomingApi.listGroomings('pet-123');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/groomings');
    expect(result).toEqual(mockResponse.data.data);
  });

  it('should call updateGrooming API', async () => {
    const mockResponse = { data: { data: { id: 'g-1', type: 'GROOMING' } } };
    (api.put as any).mockResolvedValue(mockResponse);

    const result = await groomingApi.updateGrooming('pet-123', 'g-1', { type: 'GROOMING', date: '2026-07-09' });
    expect(api.put).toHaveBeenCalledWith('/pets/pet-123/groomings/g-1', { type: 'GROOMING', date: '2026-07-09' });
    expect(result).toEqual(mockResponse.data.data);
  });

  it('should call uploadPhoto API', async () => {
    const mockResponse = { data: { data: { id: 'g-1', photos: ['url-before'] } } };
    (api.post as any).mockResolvedValue(mockResponse);

    const file = new File(['image'], 'before.jpg', { type: 'image/jpeg' });
    const result = await groomingApi.uploadPhoto('pet-123', 'g-1', file, 'BEFORE');
    
    expect(api.post).toHaveBeenCalledWith('/pets/pet-123/groomings/g-1/photos', expect.any(FormData));
    expect(result).toEqual(mockResponse.data.data);
  });
});
