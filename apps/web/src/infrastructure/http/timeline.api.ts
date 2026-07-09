import api from './api';
import type { TimelineEvent, TimelineEventType } from '../../domain/pet/Timeline';

export const timelineApi = {
  getTimeline: (petId: string, types?: TimelineEventType[], page = 0, size = 20) => {
    let url = `/pets/${petId}/timeline?page=${page}&size=${size}`;
    if (types && types.length > 0) {
      url += `&types=${types.join(',')}`;
    }
    return api.get<{ data: TimelineEvent[] }>(url);
  },

  exportPdf: (petId: string, startDate?: string, endDate?: string) => {
    let url = `/pets/${petId}/export?`;
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    return api.get<Blob>(url + params.toString(), {
      responseType: 'blob',
    });
  },
};
