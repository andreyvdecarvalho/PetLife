import { renderHook, act } from '@testing-library/react';
import { useMedications } from './useMedications';
import { medicationApi } from '../../infrastructure/http/medication.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/medication.api', () => ({
  medicationApi: {
    createMedication: vi.fn(),
    listMedications: vi.fn(),
    updateAdministration: vi.fn(),
    stopMedication: vi.fn(),
    getAdherence: vi.fn(),
  },
}));

describe('useMedications Hook', () => {
  const petId = 'pet-123';

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch medications successfully', async () => {
    const mockMeds = [
      { id: 'm1', name: 'Dipirona', dosage: '5 gotas', frequency: 'DAILY', status: 'ACTIVE', timesOfDay: ['08:00'] },
    ];
    (medicationApi.listMedications as any).mockResolvedValue(mockMeds);

    const { result } = renderHook(() => useMedications(petId));

    await act(async () => {
      await result.current.fetchMedications();
    });

    expect(medicationApi.listMedications).toHaveBeenCalledWith(petId);
    expect(result.current.medications).toEqual(mockMeds);
  });

  it('should fetch adherence metrics successfully', async () => {
    const mockAdherence = {
      adherenceRate: 100.0,
      totalDoses: 10,
      takenDoses: 10,
      skippedDoses: 0,
      lateDoses: 0,
      pendingDoses: 0,
    };
    (medicationApi.getAdherence as any).mockResolvedValue(mockAdherence);

    const { result } = renderHook(() => useMedications(petId));

    await act(async () => {
      await result.current.fetchAdherence();
    });

    expect(medicationApi.getAdherence).toHaveBeenCalledWith(petId);
    expect(result.current.adherence).toEqual(mockAdherence);
  });

  it('should create medication and refresh data', async () => {
    const newMed = { name: 'Antibiótico', dosage: '1 comprimido', frequency: 'TWICE_DAILY' as const, startDate: '2026-07-07', timesOfDay: ['08:00', '20:00'] };
    (medicationApi.createMedication as any).mockResolvedValue({ id: 'm2', ...newMed });

    const { result } = renderHook(() => useMedications(petId));

    let success = false;
    await act(async () => {
      success = await result.current.createMedication(newMed);
    });

    expect(success).toBe(true);
    expect(medicationApi.createMedication).toHaveBeenCalledWith(petId, newMed);
  });
});
