import { vi, describe, it, expect, beforeEach } from 'vitest';
import { timelineApi } from './timeline.api';
import api from './api';

vi.mock('./api', () => ({
  default: {
    get: vi.fn(),
  },
}));

describe('timelineApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should call getTimeline API with standard parameters', async () => {
    const mockResponse = { data: { data: [] } };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await timelineApi.getTimeline('pet-123');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/timeline?page=0&size=20');
    expect(result).toEqual(mockResponse);
  });

  it('should call getTimeline API with type filters', async () => {
    const mockResponse = { data: { data: [] } };
    (api.get as any).mockResolvedValue(mockResponse);

    await timelineApi.getTimeline('pet-123', ['VACCINE', 'CONSULTATION']);
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/timeline?page=0&size=20&types=VACCINE,CONSULTATION');
  });

  it('should call exportPdf API with dates', async () => {
    const mockResponse = { data: new Blob() };
    (api.get as any).mockResolvedValue(mockResponse);

    await timelineApi.exportPdf('pet-123', '2026-01-01', '2026-12-31');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/export?startDate=2026-01-01&endDate=2026-12-31', {
      responseType: 'blob',
    });
  });
});
