import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { PetsPageContent } from './index';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useDeletePet } from '../../../application/pet/useDeletePet';
import { useToast } from '../../molecules/Toast';

vi.mock('../../../application/pet/useGetPets', () => ({
  useGetPets: vi.fn(),
}));

vi.mock('../../../application/pet/useDeletePet', () => ({
  useDeletePet: vi.fn(),
}));

vi.mock('../../molecules/Toast', () => ({
  useToast: vi.fn(),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<any>('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// Mock PetCard
vi.mock('../../molecules/PetCard', () => ({
  PetCard: ({ pet, onClick }: any) => (
    <div data-testid={`pet-card-${pet.id}`} onClick={onClick}>
      {pet.name}
    </div>
  ),
}));

describe('PetsPageContent', () => {
  const mockFetchPets = vi.fn();
  const mockDeletePet = vi.fn();
  const mockShowToast = vi.fn();

  const mockPets = [
    { id: '1', name: 'Rex', species: 'DOG' },
    { id: '2', name: 'Luna', species: 'CAT' },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({ showToast: mockShowToast });
    (useGetPets as any).mockReturnValue({
      pets: [],
      isLoading: false,
      fetchPets: mockFetchPets,
    });
    (useDeletePet as any).mockReturnValue({
      deletePet: mockDeletePet,
      loading: false,
    });

    // Mock window.confirm
    global.confirm = vi.fn(() => true);
  });

  it('should render loading state', () => {
    (useGetPets as any).mockReturnValue({
      pets: [],
      isLoading: true,
      fetchPets: mockFetchPets,
    });

    render(<MemoryRouter><PetsPageContent /></MemoryRouter>);
    expect(screen.getByText('Carregando pets...')).toBeDefined();
    expect(mockFetchPets).toHaveBeenCalled();
  });

  it('should render empty state', () => {
    render(<MemoryRouter><PetsPageContent /></MemoryRouter>);
    expect(screen.getByText('Você ainda não tem nenhum pet cadastrado.')).toBeDefined();
  });

  it('should render pets list and handle navigation', () => {
    (useGetPets as any).mockReturnValue({
      pets: mockPets,
      isLoading: false,
      fetchPets: mockFetchPets,
    });

    render(<MemoryRouter><PetsPageContent /></MemoryRouter>);
    
    expect(screen.getByText('Rex')).toBeDefined();
    expect(screen.getByText('Luna')).toBeDefined();

    // Navigate to add pet
    fireEvent.click(screen.getByRole('button', { name: /adicionar pet/i }));
    expect(mockNavigate).toHaveBeenCalledWith('/pets/new');

    // Navigate back
    fireEvent.click(screen.getByTestId('btn-back'));
    expect(mockNavigate).toHaveBeenCalledWith('/');

    // Edit pet via card click
    fireEvent.click(screen.getByTestId('pet-card-1'));
    expect(mockNavigate).toHaveBeenCalledWith('/pets/new', { state: { pet: mockPets[0] } });
  });

  it('should delete pet successfully', async () => {
    (useGetPets as any).mockReturnValue({
      pets: mockPets,
      isLoading: false,
      fetchPets: mockFetchPets,
    });
    mockDeletePet.mockResolvedValueOnce(undefined);

    render(<MemoryRouter><PetsPageContent /></MemoryRouter>);

    const deleteButtons = screen.getAllByText(/Excluir/i);
    fireEvent.click(deleteButtons[0]);

    expect(global.confirm).toHaveBeenCalled();
    expect(mockDeletePet).toHaveBeenCalledWith('1');

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Pet excluído com sucesso.', 'success');
      expect(mockFetchPets).toHaveBeenCalled(); // refetch after delete
    });
  });

  it('should handle cancel delete', async () => {
    (useGetPets as any).mockReturnValue({
      pets: mockPets,
      isLoading: false,
      fetchPets: mockFetchPets,
    });
    global.confirm = vi.fn(() => false);

    render(<MemoryRouter><PetsPageContent /></MemoryRouter>);

    const deleteButtons = screen.getAllByText(/Excluir/i);
    fireEvent.click(deleteButtons[0]);

    expect(mockDeletePet).not.toHaveBeenCalled();
  });

  it('should handle delete error', async () => {
    (useGetPets as any).mockReturnValue({
      pets: mockPets,
      isLoading: false,
      fetchPets: mockFetchPets,
    });
    mockDeletePet.mockRejectedValueOnce(new Error('Erro'));

    render(<MemoryRouter><PetsPageContent /></MemoryRouter>);

    const deleteButtons = screen.getAllByText(/Excluir/i);
    fireEvent.click(deleteButtons[0]);

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Erro ao excluir pet.', 'error');
    });
  });
});
