import { renderHook, act } from '@testing-library/react';
import { useTimeline } from './useTimeline';
import { timelineApi } from '../../infrastructure/http/timeline.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../infrastructure/http/timeline.api', () => ({
  timelineApi: {
    getTimeline: vi.fn(),
  },
}));

describe('useTimeline Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch timeline successfully', async () => {
    const mockEvents = [
      { id: '1', type: 'VACCINE', date: '2026-07-08', title: 'Vacina', description: 'Aplicada', icon: 'vaccines', color: '#4F46E5' }
    ];

    (timelineApi.getTimeline as any).mockResolvedValue({
      data: { data: mockEvents }
    });

    const { result } = renderHook(() => useTimeline());

    await act(async () => {
      await result.current.fetchTimeline('pet-123', undefined, 0, 20);
    });

    expect(timelineApi.getTimeline).toHaveBeenCalledWith('pet-123', undefined, 0, 20);
    expect(result.current.events).toEqual(mockEvents);
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should append timeline events when specified', async () => {
    const initialEvents = [
      { id: '1', type: 'VACCINE', date: '2026-07-08', title: 'Vacina', description: 'Aplicada', icon: 'vaccines', color: '#4F46E5' }
    ];
    const newEvents = [
      { id: '2', type: 'CONSULTATION', date: '2026-07-07', title: 'Consulta', description: 'Rotina', icon: 'medical_services', color: '#10B981' }
    ];

    (timelineApi.getTimeline as any).mockResolvedValueOnce({
      data: { data: initialEvents }
    });

    const { result } = renderHook(() => useTimeline());

    await act(async () => {
      await result.current.fetchTimeline('pet-123', undefined, 0, 1, false);
    });

    expect(result.current.events).toEqual(initialEvents);

    (timelineApi.getTimeline as any).mockResolvedValueOnce({
      data: { data: newEvents }
    });

    await act(async () => {
      await result.current.fetchTimeline('pet-123', undefined, 1, 1, true);
    });

    expect(result.current.events).toEqual([...initialEvents, ...newEvents]);
  });
});
