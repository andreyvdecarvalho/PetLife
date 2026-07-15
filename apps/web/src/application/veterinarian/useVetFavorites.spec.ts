import { renderHook, act } from '@testing-library/react';
import { useVetFavorites } from './useVetFavorites';
import { vi } from 'vitest';
import api from '../../infrastructure/http/api';

vi.mock('../../infrastructure/http/api');

describe('useVetFavorites', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('deve listar os veterinarios favoritos', async () => {
    const mockFavorites = [{ id: '1', fullName: 'Dr. João' }];
    (api.get as any).mockResolvedValue({ data: mockFavorites });

    const { result } = renderHook(() => useVetFavorites());

    await act(async () => {
      await result.current.listFavorites();
    });

    expect(result.current.favorites).toEqual(mockFavorites);
    expect(api.get).toHaveBeenCalledWith('/veterinarians/favorites');
  });

  it('deve adicionar um favorito', async () => {
    (api.post as any).mockResolvedValue({});

    const { result } = renderHook(() => useVetFavorites());

    await act(async () => {
      await result.current.addFavorite('1');
    });

    expect(api.post).toHaveBeenCalledWith('/veterinarians/1/favorite');
  });

  it('deve remover um favorito', async () => {
    (api.delete as any).mockResolvedValue({});
    const mockFavorites = [{ id: '1', fullName: 'Dr. João' }];
    (api.get as any).mockResolvedValue({ data: mockFavorites });

    const { result } = renderHook(() => useVetFavorites());

    await act(async () => {
      await result.current.listFavorites();
    });

    await act(async () => {
      await result.current.removeFavorite('1');
    });

    expect(api.delete).toHaveBeenCalledWith('/veterinarians/1/favorite');
    expect(result.current.favorites).toEqual([]);
  });
});
