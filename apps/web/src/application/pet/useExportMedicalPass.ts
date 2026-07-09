import { useState, useCallback } from 'react';
import { timelineApi } from '../../infrastructure/http/timeline.api';

export function useExportMedicalPass() {
  const [isExporting, setIsExporting] = useState(false);
  const [exportError, setExportError] = useState<string | null>(null);

  const exportMedicalPass = useCallback(async (
    petId: string,
    startDate?: string,
    endDate?: string
  ) => {
    setIsExporting(true);
    setExportError(null);
    try {
      const response = await timelineApi.exportPdf(petId, startDate, endDate);
      const blob = response.data;
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `prontuario_${petId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode?.removeChild(link);
      window.URL.revokeObjectURL(url);
      return true;
    } catch (err: any) {
      if (err.response?.data instanceof Blob && err.response.data.type === 'application/json') {
        const text = await err.response.data.text();
        try {
          const parsed = JSON.parse(text);
          setExportError(parsed.error?.message || 'Apenas usuários Premium podem exportar o prontuário.');
        } catch {
          setExportError('Apenas usuários Premium podem exportar o prontuário.');
        }
      } else {
        const errMsg = err.response?.data?.error?.message || 'Falha ao exportar PDF.';
        setExportError(errMsg);
      }
      return false;
    } finally {
      setIsExporting(false);
    }
  }, []);

  return {
    isExporting,
    exportError,
    exportMedicalPass,
  };
}
