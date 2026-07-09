import { useState, useCallback } from 'react';
import { timelineApi } from '../../infrastructure/http/timeline.api';
import type { TimelineEvent, TimelineEventType } from '../../domain/pet/Timeline';

export function useTimeline() {
  const [events, setEvents] = useState<TimelineEvent[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState(true);

  const fetchTimeline = useCallback(async (
    petId: string,
    types?: TimelineEventType[],
    page = 0,
    size = 20,
    append = false
  ) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await timelineApi.getTimeline(petId, types, page, size);
      const fetchedEvents = response.data.data;
      if (append) {
        setEvents((prev) => [...prev, ...fetchedEvents]);
      } else {
        setEvents(fetchedEvents);
      }
      setHasMore(fetchedEvents.length === size);
    } catch (err: any) {
      const errMsg = err.response?.data?.error?.message || 'Falha ao carregar linha do tempo.';
      setError(errMsg);
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    events,
    isLoading,
    error,
    hasMore,
    fetchTimeline,
  };
}
