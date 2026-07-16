import { vi, describe, it, expect, beforeEach } from 'vitest';
import { consultationApi } from './consultation.api';
import api from './api';

vi.mock('./api', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    delete: vi.fn(),
  },
}));

describe('consultationApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should call create API', async () => {
    const mockResponse = { data: { data: { id: 'c-1', reason: 'Routine' } } };
    (api.post as any).mockResolvedValue(mockResponse);

    const result = await consultationApi.create('pet-123', { reason: 'Routine' } as any);
    expect(api.post).toHaveBeenCalledWith('/pets/pet-123/consultations', { reason: 'Routine' });
    expect(result).toEqual({ id: 'c-1', reason: 'Routine' });
  });

  it('should call list API', async () => {
    const mockResponse = { data: { data: [{ id: 'c-1', reason: 'Routine' }] } };
    (api.get as any).mockResolvedValue(mockResponse);

    const result = await consultationApi.list('pet-123');
    expect(api.get).toHaveBeenCalledWith('/pets/pet-123/consultations');
    expect(result).toEqual([{ id: 'c-1', reason: 'Routine' }]);
  });

  it('should call uploadAttachments API', async () => {
    const mockResponse = { data: { data: { id: 'c-1', attachments: ['url'] } } };
    (api.post as any).mockResolvedValue(mockResponse);

    const file = new File(['proof'], 'proof.jpg', { type: 'image/jpeg' });
    const result = await consultationApi.uploadAttachments('pet-123', 'c-1', [file]);
    
    expect(api.post).toHaveBeenCalledWith(
      '/pets/pet-123/consultations/c-1/attachments',
      expect.any(FormData)
    );
    expect(result).toEqual({ id: 'c-1', attachments: ['url'] });
  });

  it('should call deleteAttachment API', async () => {
    const mockResponse = { data: { data: { id: 'c-1', attachments: [] } } };
    (api.delete as any).mockResolvedValue(mockResponse);

    const result = await consultationApi.deleteAttachment('pet-123', 'c-1', 0);
    expect(api.delete).toHaveBeenCalledWith('/pets/pet-123/consultations/c-1/attachments/0');
    expect(result).toEqual({ id: 'c-1', attachments: [] });
  });
});
