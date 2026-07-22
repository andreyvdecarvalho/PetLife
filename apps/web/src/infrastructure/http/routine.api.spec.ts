import { describe, it, expect, vi } from 'vitest';
import { routineApi } from './routine.api';
import api from './api';

vi.mock('./api');

describe('routineApi', () => {
  it('fetchActivities should fetch activities for a pet without date', async () => {
    const mockData = { data: [{ id: 'act-1' }] };
    vi.mocked(api.get).mockResolvedValueOnce({ data: mockData });

    const result = await routineApi.fetchActivities('pet-123');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/activities');
    expect(result).toEqual(mockData.data);
  });

  it('fetchActivities should fetch activities for a pet with date', async () => {
    const mockData = { data: [{ id: 'act-1' }] };
    vi.mocked(api.get).mockResolvedValueOnce({ data: mockData });

    const result = await routineApi.fetchActivities('pet-123', '2023-10-10');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/activities?date=2023-10-10');
    expect(result).toEqual(mockData.data);
  });

  it('addActivity should create an activity for a pet', async () => {
    const mockData = { data: { id: 'act-new' } };
    vi.mocked(api.post).mockResolvedValueOnce({ data: mockData });

    const activityData = { title: 'Walk', activityDate: '2023-10-10', type: 'WALK' as const, status: 'PENDING' as const };
    const result = await routineApi.addActivity('pet-123', activityData);
    expect(api.post).toHaveBeenCalledWith('/pets/pet-123/activities', activityData);
    expect(result).toEqual(mockData.data);
  });

  it('updateStatus should update an activity status', async () => {
    const mockData = { data: { id: 'act-1', status: 'COMPLETED' } };
    vi.mocked(api.patch).mockResolvedValueOnce({ data: mockData });

    const result = await routineApi.updateStatus('act-1', 'COMPLETED');
    expect(api.patch).toHaveBeenCalledWith('/activities/act-1/status', { status: 'COMPLETED' });
    expect(result).toEqual(mockData.data);
  });
});
