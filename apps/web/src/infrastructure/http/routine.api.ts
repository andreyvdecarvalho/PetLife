import api from './api';

export interface RoutineActivity {
  id: string;
  petId: string;
  title: string;
  description: string;
  activityDate: string;
  activityTime: string;
  type: 'WALK' | 'FEEDING' | 'GENERIC';
  status: 'PENDING' | 'COMPLETED' | 'SCHEDULED';
  createdAt: string;
}

export interface CreateRoutineActivityData {
  title: string;
  description?: string;
  activityDate: string;
  activityTime?: string;
  type: 'WALK' | 'FEEDING' | 'GENERIC';
  status: 'PENDING' | 'COMPLETED' | 'SCHEDULED';
}

export const routineApi = {
  fetchActivities: async (petId: string, date?: string): Promise<RoutineActivity[]> => {
    const url = date ? `/pets/${petId}/activities?date=${date}` : `/pets/${petId}/activities`;
    const response = await api.get(url);
    return response.data.data;
  },

  addActivity: async (petId: string, data: CreateRoutineActivityData): Promise<RoutineActivity> => {
    const response = await api.post(`/pets/${petId}/activities`, data);
    return response.data.data;
  },

  updateStatus: async (id: string, status: 'PENDING' | 'COMPLETED' | 'SCHEDULED'): Promise<RoutineActivity> => {
    const response = await api.patch(`/activities/${id}/status`, { status });
    return response.data.data;
  }
};
