import { renderHook, act } from '@testing-library/react';
import { useVaccinations } from './useVaccinations';
import { vaccinationApi } from '../../infrastructure/http/vaccination.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/vaccination.api', () => ({
  vaccinationApi: {
    listVaccinations: vi.fn(),
    addVaccination: vi.fn(),
    updateVaccination: vi.fn(),
    uploadProof: vi.fn(),
  },
}));

describe('useVaccinations Hook', () => {
  const petId = 'pet-123';

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch vaccinations list successfully', async () => {
    const mockVaccinations = [
      { id: 'v1', name: 'Anti-rábica', applicationDate: '2026-01-01', status: 'COMPLETED' },
      { id: 'v2', name: 'V10', applicationDate: '2026-06-01', status: 'SCHEDULED' },
    ];

    (vaccinationApi.listVaccinations as any).mockResolvedValue(mockVaccinations);

    const { result } = renderHook(() => useVaccinations(petId));

    expect(result.current.vaccinations).toEqual([]);
    expect(result.current.loading).toBe(false);

    await act(async () => {
      await result.current.fetchVaccinations();
    });

    expect(vaccinationApi.listVaccinations).toHaveBeenCalledWith(petId);
    expect(result.current.vaccinations).toEqual(mockVaccinations);
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should handle API fetch failure correctly', async () => {
    const errorMessage = 'Erro ao buscar vacinas';
    (vaccinationApi.listVaccinations as any).mockRejectedValue({
      response: {
        data: {
          message: errorMessage,
        },
      },
    });

    const { result } = renderHook(() => useVaccinations(petId));

    await act(async () => {
      await result.current.fetchVaccinations();
    });

    expect(result.current.vaccinations).toEqual([]);
    expect(result.current.error).toBe(errorMessage);
    expect(result.current.loading).toBe(false);
  });

  it('should add vaccination successfully and refresh list', async () => {
    const mockVaccinations = [
      { id: 'v1', name: 'Anti-rábica', applicationDate: '2026-01-01', status: 'COMPLETED' },
    ];
    const newVaccineData = { name: 'Gripe', applicationDate: '2026-07-02', status: 'COMPLETED' as const };

    (vaccinationApi.addVaccination as any).mockResolvedValue({ id: 'v2', ...newVaccineData });
    (vaccinationApi.listVaccinations as any).mockResolvedValue([...mockVaccinations, { id: 'v2', ...newVaccineData }]);

    const { result } = renderHook(() => useVaccinations(petId));

    let success = false;
    await act(async () => {
      success = await result.current.addVaccination(newVaccineData);
    });

    expect(success).toBe(true);
    expect(vaccinationApi.addVaccination).toHaveBeenCalledWith(petId, newVaccineData);
    expect(vaccinationApi.listVaccinations).toHaveBeenCalledWith(petId);
    expect(result.current.vaccinations).toHaveLength(2);
  });

  it('should handle add vaccination failure', async () => {
    const newVaccineData = { name: 'Gripe', applicationDate: '2026-07-02', status: 'COMPLETED' as const };
    const errorMessage = 'Erro ao cadastrar';

    (vaccinationApi.addVaccination as any).mockRejectedValue({
      response: {
        data: {
          message: errorMessage,
        },
      },
    });

    const { result } = renderHook(() => useVaccinations(petId));

    let success = true;
    await act(async () => {
      success = await result.current.addVaccination(newVaccineData);
    });

    expect(success).toBe(false);
    expect(result.current.error).toBe(errorMessage);
  });

  it('should update vaccination successfully and refresh list', async () => {
    const updatedData = { name: 'Anti-rábica Reforço', applicationDate: '2026-01-02', status: 'COMPLETED' as const };

    (vaccinationApi.updateVaccination as any).mockResolvedValue({ id: 'v1', ...updatedData });
    (vaccinationApi.listVaccinations as any).mockResolvedValue([{ id: 'v1', ...updatedData }]);

    const { result } = renderHook(() => useVaccinations(petId));

    let success = false;
    await act(async () => {
      success = await result.current.updateVaccination('v1', updatedData);
    });

    expect(success).toBe(true);
    expect(vaccinationApi.updateVaccination).toHaveBeenCalledWith(petId, 'v1', updatedData);
    expect(vaccinationApi.listVaccinations).toHaveBeenCalledWith(petId);
  });

  it('should handle update vaccination failure', async () => {
    const updatedData = { name: 'Anti-rábica Reforço', applicationDate: '2026-01-02', status: 'COMPLETED' as const };
    const errorMessage = 'Erro ao atualizar';

    (vaccinationApi.updateVaccination as any).mockRejectedValue({
      response: {
        data: {
          message: errorMessage,
        },
      },
    });

    const { result } = renderHook(() => useVaccinations(petId));

    let success = true;
    await act(async () => {
      success = await result.current.updateVaccination('v1', updatedData);
    });

    expect(success).toBe(false);
    expect(result.current.error).toBe(errorMessage);
  });

  it('should upload proof successfully and refresh list', async () => {
    const fakeFile = new File(['proof'], 'comprovante.pdf', { type: 'application/pdf' });

    (vaccinationApi.uploadProof as any).mockResolvedValue({ id: 'v1', proofUrl: 'http://proof.com' });
    (vaccinationApi.listVaccinations as any).mockResolvedValue([{ id: 'v1', proofUrl: 'http://proof.com' }]);

    const { result } = renderHook(() => useVaccinations(petId));

    let success = false;
    await act(async () => {
      success = await result.current.uploadProof('v1', fakeFile);
    });

    expect(success).toBe(true);
    expect(vaccinationApi.uploadProof).toHaveBeenCalledWith(petId, 'v1', fakeFile);
    expect(vaccinationApi.listVaccinations).toHaveBeenCalledWith(petId);
  });

  it('should handle upload proof failure', async () => {
    const fakeFile = new File(['proof'], 'comprovante.pdf', { type: 'application/pdf' });
    const errorMessage = 'Erro no arquivo';

    (vaccinationApi.uploadProof as any).mockRejectedValue({
      response: {
        data: {
          message: errorMessage,
        },
      },
    });

    const { result } = renderHook(() => useVaccinations(petId));

    let success = true;
    await act(async () => {
      success = await result.current.uploadProof('v1', fakeFile);
    });

    expect(success).toBe(false);
    expect(result.current.error).toBe(errorMessage);
  });
});
