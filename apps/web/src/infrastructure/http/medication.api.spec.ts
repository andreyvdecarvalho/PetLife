import { vi, describe, it, expect, beforeEach } from 'vitest';
import { medicationApi } from './medication.api';
import api from './api';

vi.mock('./api', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    patch: vi.fn(),
  },
}));

describe('medicationApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should call createMedication API', async () => {
    const mockResponse = { data: { data: { id: '1', name: 'Med' } } };
    (api.post as any).mockResolvedValue(mockResponse);

    const result = await medicationApi.createMedication('pet-123', { name: 'Med' } as any);
    expect(api.post).toHaveBeenCalledWith('/pets/pet-123/medications', { name: 'Med' });
    expect(result).toEqual({ id: '1', name: 'Med' });
  });

  it('should call listMedications API', async () => {
    const mockResponse = { data: { data: [{ id: '1', name: 'Med' }] } };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await medicationApi.listMedications('pet-123');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/medications');
    expect(result).toEqual([{ id: '1', name: 'Med' }]);
  });

  it('should call updateAdministration API', async () => {
    const mockResponse = { data: { data: { id: 'dose-1' } } };
    (api.patch as any).mockResolvedValue(mockResponse);

    const result = await medicationApi.updateAdministration('dose-1', { status: 'TAKEN' });
    expect(api.patch).toHaveBeenCalledWith('/medications/doses/dose-1', { status: 'TAKEN' });
    expect(result).toEqual({ id: 'dose-1' });
  });

  it('should call stopMedication API', async () => {
    const mockResponse = { data: { data: { id: 'med-1' } } };
    (api.patch as any).mockResolvedValue(mockResponse);

    const result = await medicationApi.stopMedication('med-1');
    expect(api.patch).toHaveBeenCalledWith('/medications/med-1/stop');
    expect(result).toEqual({ id: 'med-1' });
  });

  it('should call getAdherence API', async () => {
    const mockResponse = { data: { data: { rate: 100 } } };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await medicationApi.getAdherence('pet-123');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/medications/adherence');
    expect(result).toEqual({ rate: 100 });
  });
});
