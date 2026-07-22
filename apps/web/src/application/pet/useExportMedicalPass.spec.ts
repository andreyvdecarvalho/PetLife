import { renderHook, act } from '@testing-library/react';
import { useExportMedicalPass } from './useExportMedicalPass';
import { timelineApi } from '../../infrastructure/http/timeline.api';
import { downloadBlob } from '../../infrastructure/browser/DownloadAdapter';
import { vi, describe, it, expect, beforeEach, afterEach } from 'vitest';

vi.mock('../../infrastructure/http/timeline.api', () => ({
  timelineApi: {
    exportPdf: vi.fn(),
  },
}));

vi.mock('../../infrastructure/browser/DownloadAdapter', () => ({
  downloadBlob: vi.fn(),
}));

describe('useExportMedicalPass Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should handle exportPdf successfully', async () => {
    const mockBlob = new Blob(['pdf-bytes'], { type: 'application/pdf' });
    (timelineApi.exportPdf as any).mockResolvedValue({
      data: mockBlob
    });

    const { result } = renderHook(() => useExportMedicalPass());

    let success;
    await act(async () => {
      success = await result.current.exportMedicalPass('pet-123', '2026-01-01', '2026-12-31');
    });

    expect(timelineApi.exportPdf).toHaveBeenCalledWith('pet-123', '2026-01-01', '2026-12-31');
    expect(success).toBe(true);
    expect(result.current.isExporting).toBe(false);
    expect(result.current.exportError).toBeNull();
    expect(downloadBlob).toHaveBeenCalledWith(mockBlob, 'prontuario_pet-123.pdf');
  });

  it('should handle export error and set message', async () => {
    const mockError = {
      response: {
        data: {
          error: {
            message: 'Erro na exportação'
          }
        }
      }
    };
    (timelineApi.exportPdf as any).mockRejectedValue(mockError);

    const { result } = renderHook(() => useExportMedicalPass());

    let success;
    await act(async () => {
      success = await result.current.exportMedicalPass('pet-123');
    });

    expect(success).toBe(false);
    expect(result.current.isExporting).toBe(false);
    expect(result.current.exportError).toBe('Erro na exportação');
  });
});
