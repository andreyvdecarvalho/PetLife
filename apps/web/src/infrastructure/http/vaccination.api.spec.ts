import { vi, describe, it, expect, beforeEach } from 'vitest';
import { vaccinationApi } from './vaccination.api';
import api from './api';

vi.mock('./api', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
  },
}));

describe('vaccinationApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should call addVaccination API', async () => {
    const mockResponse = { data: { data: { id: 'v-1', vaccineName: 'Anti' } } };
    (api.post as any).mockResolvedValue(mockResponse);

    const result = await vaccinationApi.addVaccination('pet-123', { vaccineName: 'Anti' } as any);
    expect(api.post).toHaveBeenCalledWith('/pets/pet-123/vaccines', { vaccineName: 'Anti' });
    expect(result).toEqual({ id: 'v-1', vaccineName: 'Anti' });
  });

  it('should call listVaccinations API', async () => {
    const mockResponse = { data: { data: [{ id: 'v-1', vaccineName: 'Anti' }] } };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await vaccinationApi.listVaccinations('pet-123');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/vaccines');
    expect(result).toEqual([{ id: 'v-1', vaccineName: 'Anti' }]);
  });

  it('should call updateVaccination API', async () => {
    const mockResponse = { data: { data: { id: 'v-1', vaccineName: 'Anti' } } };
    (api.put as any).mockResolvedValue(mockResponse);

    const result = await vaccinationApi.updateVaccination('pet-123', 'v-1', { vaccineName: 'Anti' } as any);
    expect(api.put).toHaveBeenCalledWith('/pets/pet-123/vaccines/v-1', { vaccineName: 'Anti' });
    expect(result).toEqual({ id: 'v-1', vaccineName: 'Anti' });
  });

  it('should call uploadProof API', async () => {
    const mockResponse = { data: { data: { id: 'v-1', proofUrl: 'http://proof' } } };
    (api.post as any).mockResolvedValue(mockResponse);

    const file = new File(['proof'], 'proof.jpg', { type: 'image/jpeg' });
    const result = await vaccinationApi.uploadProof('pet-123', 'v-1', file);
    
    expect(api.post).toHaveBeenCalledWith('/pets/pet-123/vaccines/v-1/proof', expect.any(FormData), {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    expect(result).toEqual({ id: 'v-1', proofUrl: 'http://proof' });
  });

  it('should call getSuggestions API', async () => {
    const mockResponse = { data: { data: ['Sugg1', 'Sugg2'] } };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await vaccinationApi.getSuggestions('DOG');
    expect(api.get).toHaveBeenCalledWith('/vaccines/suggestions', {
      params: { species: 'DOG' }
    });
    expect(result).toEqual(['Sugg1', 'Sugg2']);
  });
});
