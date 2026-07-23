import { useState, useCallback } from 'react';
import { routineApi, RoutineActivity, CreateRoutineActivityData } from '../../infrastructure/http/routine.api';

export type { RoutineActivity, CreateRoutineActivityData };

export function useRoutineActivities(petId: string) {
  const [activities, setActivities] = useState<RoutineActivity[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchActivities = useCallback(async (date?: string) => {
    if (!petId) return;
    try {
      setLoading(true);
      setError(null);
      const data = await routineApi.fetchActivities(petId, date);
      setActivities(data);
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
      const newActivity = await routineApi.addActivity(petId, data);
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
      const updatedActivity = await routineApi.updateStatus(id, status);
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
