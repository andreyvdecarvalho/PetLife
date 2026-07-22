import { describe, it, expect, vi } from 'vitest';
import { veterinarianApi } from './veterinarian.api';
import api from './api';

vi.mock('./api');

describe('veterinarianApi', () => {
  it('getProfile should fetch veterinarian profile by id', async () => {
    const mockData = { id: 'vet-123', name: 'Dr. Pet' };
    vi.mocked(api.get).mockResolvedValueOnce({ data: mockData });

    const result = await veterinarianApi.getProfile('vet-123');
    expect(api.get).toHaveBeenCalledWith('/veterinarians/vet-123');
    expect(result).toEqual(mockData);
  });

  it('search should fetch paginated veterinarians', async () => {
    const mockData = { content: [{ id: 'vet-1' }], totalElements: 1 };
    vi.mocked(api.get).mockResolvedValueOnce({ data: mockData });

    const params = { lat: 10, lng: 20 };
    const result = await veterinarianApi.search(params);
    expect(api.get).toHaveBeenCalledWith('/veterinarians/search', { params });
    expect(result).toEqual(mockData);
  });

  it('listFavorites should fetch favorites list', async () => {
    const mockData = [{ id: 'vet-1' }];
    vi.mocked(api.get).mockResolvedValueOnce({ data: mockData });

    const result = await veterinarianApi.listFavorites();
    expect(api.get).toHaveBeenCalledWith('/veterinarians/favorites');
    expect(result).toEqual(mockData);
  });

  it('addFavorite should post favorite vet', async () => {
    vi.mocked(api.post).mockResolvedValueOnce({});
    await veterinarianApi.addFavorite('vet-123');
    expect(api.post).toHaveBeenCalledWith('/veterinarians/vet-123/favorite');
  });

  it('removeFavorite should delete favorite vet', async () => {
    vi.mocked(api.delete).mockResolvedValueOnce({});
    await veterinarianApi.removeFavorite('vet-123');
    expect(api.delete).toHaveBeenCalledWith('/veterinarians/vet-123/favorite');
  });

  it('createProfile should create vet profile', async () => {
    const mockData = { id: 'vet-123', name: 'New Vet' };
    vi.mocked(api.post).mockResolvedValueOnce({ data: mockData });

    const result = await veterinarianApi.createProfile({ name: 'New Vet' });
    expect(api.post).toHaveBeenCalledWith('/veterinarians', { name: 'New Vet' });
    expect(result).toEqual(mockData);
  });

  it('getMyProfile should fetch my vet profile', async () => {
    const mockData = { id: 'me', name: 'Dr. Me' };
    vi.mocked(api.get).mockResolvedValueOnce({ data: mockData });

    const result = await veterinarianApi.getMyProfile();
    expect(api.get).toHaveBeenCalledWith('/veterinarians/me');
    expect(result).toEqual(mockData);
  });

  it('updateProfile should update vet profile', async () => {
    const mockData = { id: 'me', name: 'Updated Dr.' };
    vi.mocked(api.put).mockResolvedValueOnce({ data: mockData });

    const result = await veterinarianApi.updateProfile({ name: 'Updated Dr.' });
    expect(api.put).toHaveBeenCalledWith('/veterinarians/me', { name: 'Updated Dr.' });
    expect(result).toEqual(mockData);
  });

  it('updateAvailability should patch availability', async () => {
    vi.mocked(api.patch).mockResolvedValueOnce({});
    await veterinarianApi.updateAvailability('AVAILABLE');
    expect(api.patch).toHaveBeenCalledWith('/veterinarians/me/availability', { status: 'AVAILABLE' });
  });

  it('updateEmergency should patch emergency status', async () => {
    vi.mocked(api.patch).mockResolvedValueOnce({});
    await veterinarianApi.updateEmergency(true);
    expect(api.patch).toHaveBeenCalledWith('/veterinarians/me/emergency', { emergencyOnDuty: true });
  });
});
