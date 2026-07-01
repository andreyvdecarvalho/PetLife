import { render, screen } from '@testing-library/react';
import { DashboardPage } from './DashboardPage';
import { useAuth } from '../contexts/AuthContext';
import { MemoryRouter } from 'react-router-dom';
import { vi, describe, it, expect } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast', () => ({
  useToast: () => ({
    showToast: vi.fn(),
  }),
}));
vi.mock('../application/pet/useGetPets', () => ({
  useGetPets: () => ({
    pets: [
      { id: '1', name: 'Max', species: 'DOG', breed: 'Golden Retriever', photoUrl: '', status: 'ACTIVE', userId: '1' },
      { id: '2', name: 'Luna', species: 'CAT', breed: 'Siamês', photoUrl: '', status: 'ACTIVE', userId: '1' }
    ],
    isLoading: false,
    error: null,
    fetchPets: vi.fn()
  })
}));

describe('DashboardPage', () => {
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
});
