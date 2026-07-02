import api from './api';
import { Consultation, CreateConsultationData } from '../../domain/pet/Consultation';

export const consultationApi = {
  create: async (petId: string, data: CreateConsultationData): Promise<Consultation> => {
    const response = await api.post<{ data: Consultation }>(`/pets/${petId}/consultations`, data);
    return response.data.data;
  },

  list: async (petId: string): Promise<Consultation[]> => {
    const response = await api.get<{ data: Consultation[] }>(`/pets/${petId}/consultations`);
    return response.data.data;
  },

  uploadAttachments: async (petId: string, consultationId: string, files: File[]): Promise<Consultation> => {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));
    const response = await api.post<{ data: Consultation }>(
      `/pets/${petId}/consultations/${consultationId}/attachments`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data.data;
  },

  deleteAttachment: async (petId: string, consultationId: string, index: number): Promise<Consultation> => {
    const response = await api.delete<{ data: Consultation }>(
      `/pets/${petId}/consultations/${consultationId}/attachments/${index}`
    );
    return response.data.data;
  },
};
