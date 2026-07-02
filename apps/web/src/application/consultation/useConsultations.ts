import { useState, useCallback } from 'react';
import type { Consultation, CreateConsultationData } from '../../domain/pet/Consultation';
import { consultationApi } from '../../infrastructure/http/consultation.api';

export function useConsultations(petId: string) {
  const [consultations, setConsultations] = useState<Consultation[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchConsultations = useCallback(async () => {
    if (!petId) return;
    try {
      setLoading(true);
      setError(null);
      const data = await consultationApi.list(petId);
      setConsultations(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar consultas');
    } finally {
      setLoading(false);
    }
  }, [petId]);

  const addConsultation = async (data: CreateConsultationData) => {
    try {
      setLoading(true);
      setError(null);
      const newConsultation = await consultationApi.create(petId, data);
      setConsultations(prev => [newConsultation, ...prev]);
      return newConsultation;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao adicionar consulta');
      return null;
    } finally {
      setLoading(false);
    }
  };

  const uploadAttachments = async (consultationId: string, files: File[]) => {
    try {
      setLoading(true);
      setError(null);
      const updated = await consultationApi.uploadAttachments(petId, consultationId, files);
      setConsultations(prev => prev.map(c => c.id === consultationId ? updated : c));
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao enviar anexos');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const deleteAttachment = async (consultationId: string, index: number) => {
    try {
      setLoading(true);
      setError(null);
      const updated = await consultationApi.deleteAttachment(petId, consultationId, index);
      setConsultations(prev => prev.map(c => c.id === consultationId ? updated : c));
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao deletar anexo');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { consultations, loading, error, fetchConsultations, addConsultation, uploadAttachments, deleteAttachment };
}
