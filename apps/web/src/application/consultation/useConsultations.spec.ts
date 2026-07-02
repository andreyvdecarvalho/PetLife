import { renderHook, act } from '@testing-library/react';
import { useConsultations } from './useConsultations';
import { consultationApi } from '../../infrastructure/http/consultation.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/consultation.api', () => ({
  consultationApi: {
    create: vi.fn(),
    list: vi.fn(),
    uploadAttachments: vi.fn(),
    deleteAttachment: vi.fn(),
  },
}));

describe('useConsultations Hook', () => {
  const petId = 'pet-999';

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch consultations successfully', async () => {
    const mockConsultations = [
      { id: 'c1', petId, reason: 'Rotina', date: '2026-07-02T15:00:00Z', attachments: [] },
    ];
    (consultationApi.list as any).mockResolvedValue(mockConsultations);

    const { result } = renderHook(() => useConsultations(petId));

    expect(result.current.consultations).toEqual([]);
    expect(result.current.loading).toBe(false);

    await act(async () => {
      await result.current.fetchConsultations();
    });

    expect(consultationApi.list).toHaveBeenCalledWith(petId);
    expect(result.current.consultations).toEqual(mockConsultations);
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should handle fetch consultations failure', async () => {
    const errorMessage = 'Erro ao buscar consultas';
    (consultationApi.list as any).mockRejectedValue({
      response: {
        data: {
          message: errorMessage,
        },
      },
    });

    const { result } = renderHook(() => useConsultations(petId));

    await act(async () => {
      await result.current.fetchConsultations();
    });

    expect(result.current.consultations).toEqual([]);
    expect(result.current.error).toBe(errorMessage);
    expect(result.current.loading).toBe(false);
  });

  it('should add consultation successfully and prepend it to the list', async () => {
    const initialConsultations = [
      { id: 'c1', petId, reason: 'Antiga', date: '2026-07-01T15:00:00Z', attachments: [] },
    ];
    const newConsultationData = { reason: 'Nova', date: '2026-07-02T15:00:00Z' };
    const mockCreated = { id: 'c2', petId, ...newConsultationData, attachments: [] };

    (consultationApi.create as any).mockResolvedValue(mockCreated);
    (consultationApi.list as any).mockResolvedValue(initialConsultations);

    const { result } = renderHook(() => useConsultations(petId));

    // Carrega a lista inicial
    await act(async () => {
      await result.current.fetchConsultations();
    });

    let created = null;
    await act(async () => {
      created = await result.current.addConsultation(newConsultationData);
    });

    expect(created).toEqual(mockCreated);
    expect(consultationApi.create).toHaveBeenCalledWith(petId, newConsultationData);
    expect(result.current.consultations[0]).toEqual(mockCreated);
    expect(result.current.consultations).toHaveLength(2);
  });

  it('should handle add consultation failure', async () => {
    const newConsultationData = { reason: 'Nova', date: '2026-07-02T15:00:00Z' };
    const errorMessage = 'Erro ao criar';

    (consultationApi.create as any).mockRejectedValue({
      response: {
        data: {
          message: errorMessage,
        },
      },
    });

    const { result } = renderHook(() => useConsultations(petId));

    let created = null;
    await act(async () => {
      created = await result.current.addConsultation(newConsultationData);
    });

    expect(created).toBeNull();
    expect(result.current.error).toBe(errorMessage);
  });

  it('should upload attachments successfully', async () => {
    const consultation = { id: 'c1', petId, reason: 'Rotina', date: '2026-07-02T15:00:00Z', attachments: [] };
    const updatedConsultation = { ...consultation, attachments: ['http://exame.pdf'] };
    const files = [new File([''], 'exame.pdf')];

    (consultationApi.list as any).mockResolvedValue([consultation]);
    (consultationApi.uploadAttachments as any).mockResolvedValue(updatedConsultation);

    const { result } = renderHook(() => useConsultations(petId));

    await act(async () => {
      await result.current.fetchConsultations();
    });

    let success = false;
    await act(async () => {
      success = await result.current.uploadAttachments('c1', files);
    });

    expect(success).toBe(true);
    expect(consultationApi.uploadAttachments).toHaveBeenCalledWith(petId, 'c1', files);
    expect(result.current.consultations[0].attachments).toEqual(['http://exame.pdf']);
  });

  it('should delete attachment successfully', async () => {
    const consultation = { id: 'c1', petId, reason: 'Rotina', date: '2026-07-02T15:00:00Z', attachments: ['http://exame.pdf'] };
    const updatedConsultation = { ...consultation, attachments: [] };

    (consultationApi.list as any).mockResolvedValue([consultation]);
    (consultationApi.deleteAttachment as any).mockResolvedValue(updatedConsultation);

    const { result } = renderHook(() => useConsultations(petId));

    await act(async () => {
      await result.current.fetchConsultations();
    });

    let success = false;
    await act(async () => {
      success = await result.current.deleteAttachment('c1', 0);
    });

    expect(success).toBe(true);
    expect(consultationApi.deleteAttachment).toHaveBeenCalledWith(petId, 'c1', 0);
    expect(result.current.consultations[0].attachments).toEqual([]);
  });
});
