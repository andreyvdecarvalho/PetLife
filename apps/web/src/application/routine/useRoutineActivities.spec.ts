import { renderHook, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { useRoutineActivities } from './useRoutineActivities';
import api from '../../infrastructure/http/api';

vi.mock('../../infrastructure/http/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    patch: vi.fn(),
  }
}));

describe('useRoutineActivities', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should not fetch if petId is empty', async () => {
    const { result } = renderHook(() => useRoutineActivities(''));
    
    await act(async () => {
      await result.current.fetchActivities();
    });

    expect(api.get).not.toHaveBeenCalled();
  });

  it('should fetch activities successfully', async () => {
    const mockActivities = [{ id: '1', title: 'Walk', activityTime: '08:00' }];
    (api.get as any).mockResolvedValueOnce({ data: { data: mockActivities } });

    const { result } = renderHook(() => useRoutineActivities('123'));
    
    await act(async () => {
      await result.current.fetchActivities();
    });

    expect(api.get).toHaveBeenCalledWith('/pets/123/activities');
    expect(result.current.activities).toEqual(mockActivities);
  });

  it('should fetch activities with date', async () => {
    (api.get as any).mockResolvedValueOnce({ data: { data: [] } });

    const { result } = renderHook(() => useRoutineActivities('123'));
    
    await act(async () => {
      await result.current.fetchActivities('2023-01-01');
    });

    expect(api.get).toHaveBeenCalledWith('/pets/123/activities?date=2023-01-01');
  });

  it('should handle fetch activities error', async () => {
    (api.get as any).mockRejectedValueOnce({ response: { data: { message: 'Erro fetch' } } });

    const { result } = renderHook(() => useRoutineActivities('123'));
    
    await act(async () => {
      await result.current.fetchActivities();
    });

    expect(result.current.error).toBe('Erro fetch');
  });

  it('should add activity successfully', async () => {
    const newActivity = { id: '1', title: 'Walk', activityTime: '08:00', type: 'WALK', status: 'PENDING', activityDate: '2023-01-01' };
    (api.post as any).mockResolvedValueOnce({ data: { data: newActivity } });

    const { result } = renderHook(() => useRoutineActivities('123'));
    
    let res;
    await act(async () => {
      res = await result.current.addActivity(newActivity as any);
    });

    expect(api.post).toHaveBeenCalledWith('/pets/123/activities', newActivity);
    expect(res).toEqual(newActivity);
    expect(result.current.activities).toEqual([newActivity]);
  });

  it('should handle add activity error', async () => {
    (api.post as any).mockRejectedValueOnce({ response: { data: { message: 'Erro add' } } });

    const { result } = renderHook(() => useRoutineActivities('123'));
    
    let res;
    await act(async () => {
      res = await result.current.addActivity({} as any);
    });

    expect(res).toBeNull();
    expect(result.current.error).toBe('Erro add');
  });

  it('should update status successfully', async () => {
    const newActivity = { id: '1', title: 'Walk', activityTime: '08:00', type: 'WALK', status: 'COMPLETED', activityDate: '2023-01-01' };
    (api.patch as any).mockResolvedValueOnce({ data: { data: newActivity } });

    const { result } = renderHook(() => useRoutineActivities('123'));
    
    let res;
    await act(async () => {
      res = await result.current.updateStatus('1', 'COMPLETED');
    });

    expect(api.patch).toHaveBeenCalledWith('/activities/1/status', { status: 'COMPLETED' });
    expect(res).toEqual(newActivity);
  });

  it('should handle update status error', async () => {
    (api.patch as any).mockRejectedValueOnce({ response: { data: { message: 'Erro update' } } });

    const { result } = renderHook(() => useRoutineActivities('123'));
    
    let res;
    await act(async () => {
      res = await result.current.updateStatus('1', 'COMPLETED');
    });

    expect(res).toBeNull();
    expect(result.current.error).toBe('Erro update');
  });
});
