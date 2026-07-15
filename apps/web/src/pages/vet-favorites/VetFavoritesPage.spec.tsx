import { render, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { VetFavoritesPage } from './VetFavoritesPage';

const mockRemoveFavorite = vi.fn();

vi.mock('../../application/veterinarian/useVetFavorites', () => ({
  useVetFavorites: () => ({
    listFavorites: vi.fn(),
    removeFavorite: mockRemoveFavorite,
    favorites: [{ id: '1', fullName: 'Dr. Teste', crmvState: 'SP', crmvNumber: '123', specialties: [], availabilityStatus: 'AVAILABLE' }],
    loading: false,
    error: null,
  })
}));

describe('VetFavoritesPage', () => {
  it('should render and interact successfully', async () => {
    const { getByText, findByText } = render(
      <MemoryRouter>
        <VetFavoritesPage />
      </MemoryRouter>
    );
    
    expect(getByText('Meus Veterinários Favoritos')).toBeDefined();
    
    // Testa o clique no remover favorito
    const removeBtn = await findByText('Remover Favorito');
    fireEvent.click(removeBtn);
    await waitFor(() => expect(mockRemoveFavorite).toHaveBeenCalledWith('1'));
  });
});
