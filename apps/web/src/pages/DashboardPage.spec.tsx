import { render, screen, fireEvent } from '@testing-library/react';
import { DashboardPage } from './DashboardPage';
import { useAuth } from '../contexts/AuthContext';
import { MemoryRouter } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast', () => ({
  useToast: () => ({
    showToast: vi.fn(),
  }),
}));

const mockFetchPets = vi.fn();
vi.mock('../application/pet/useGetPets', () => ({
  useGetPets: () => ({
    pets: [
      { id: '1', name: 'Max', species: 'DOG', breed: 'Golden Retriever', photoUrl: '', status: 'ACTIVE', userId: '1' },
      { id: '2', name: 'Luna', species: 'CAT', breed: 'Siamês', photoUrl: '', status: 'ACTIVE', userId: '1' }
    ],
    isLoading: false,
    error: null,
    fetchPets: mockFetchPets
  })
}));

vi.mock('../application/pet/useUpdatePetStatus', () => ({
  useUpdatePetStatus: () => ({
    updatePetStatus: vi.fn(),
    loading: false,
    error: null
  })
}));

vi.mock('../application/pet/usePetWeightHistory', () => ({
  usePetWeightHistory: () => ({
    data: [],
    loading: false,
    error: null
  })
}));

describe('DashboardPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render correctly with verified email', () => {
    (useAuth as any).mockReturnValue({
      user: {
        id: '1',
        name: 'Camila',
        email: 'camila@example.com',
        plan: 'FREE',
        emailVerified: true
      },
      logout: vi.fn()
    });

    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );

    expect(screen.getByText('Olá, Camila! 👋')).toBeDefined();
    expect(screen.getByText('camila@example.com')).toBeDefined();
    expect(screen.getByText('✨ E-mail verificado com sucesso.')).toBeDefined();
    expect(screen.getByText('FREE')).toBeDefined();
  });

  it('should render correctly with unverified email', () => {
    (useAuth as any).mockReturnValue({
      user: {
        id: '1',
        name: 'Camila',
        email: 'camila@example.com',
        plan: 'FREE',
        emailVerified: false
      },
      logout: vi.fn()
    });

    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );

    expect(screen.getByText('⚠️ Por favor, confirme seu e-mail.')).toBeDefined();
  });

  it('should open edit modal when clicking edit button', () => {
    (useAuth as any).mockReturnValue({
      user: {
        id: '1',
        name: 'Camila',
        email: 'camila@example.com',
        plan: 'FREE',
        emailVerified: true
      },
      logout: vi.fn()
    });

    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );

    const editBtn = screen.getByTestId('btn-editar-pet');
    expect(editBtn).toBeDefined();

    fireEvent.click(editBtn);

    // O modal deve ser aberto e exibir "Editar Pet" com o nome pré-preenchido
    expect(screen.getByText('Editar Pet')).toBeDefined();
    expect((screen.getByLabelText('Nome do Pet') as HTMLInputElement).value).toBe('Max');
  });
});
