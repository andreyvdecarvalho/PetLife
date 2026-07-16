import { useState, useCallback } from 'react';
import api from '../../infrastructure/http/api';

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

export function useRoutineActivities(petId: string) {
  const [activities, setActivities] = useState<RoutineActivity[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchActivities = useCallback(async (date?: string) => {
    if (!petId) return;
    try {
      setLoading(true);
      setError(null);
      const url = date ? `/pets/${petId}/activities?date=${date}` : `/pets/${petId}/activities`;
      const response = await api.get(url);
      setActivities(response.data.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar atividades');
    } finally {
      setLoading(false);
    }
  }, [petId]);

  const addActivity = async (data: CreateRoutineActivityData) => {
    try {
      setLoading(true);
      setError(null);
      const response = await api.post(`/pets/${petId}/activities`, data);
      const newActivity = response.data.data;
      setActivities(prev => [...prev, newActivity].sort((a, b) => a.activityTime.localeCompare(b.activityTime)));
      return newActivity;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao criar atividade');
      return null;
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (id: string, status: 'PENDING' | 'COMPLETED' | 'SCHEDULED') => {
    try {
      setLoading(true);
      setError(null);
      const response = await api.patch(`/activities/${id}/status`, { status });
      const updatedActivity = response.data.data;
      setActivities(prev => prev.map(a => a.id === id ? updatedActivity : a));
      return updatedActivity;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao atualizar status da atividade');
      return null;
    } finally {
      setLoading(false);
    }
  };

  return { activities, loading, error, fetchActivities, addActivity, updateStatus };
}
