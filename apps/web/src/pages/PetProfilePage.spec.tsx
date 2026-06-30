import { render, screen } from '@testing-library/react';
import { PetProfilePage } from './PetProfilePage';
import { useAuth } from '../contexts/AuthContext';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');

describe('PetProfilePage', () => {
  it('should render correctly with pet profile details', () => {
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
      <MemoryRouter initialEntries={['/pets/1']}>
        <Routes>
          <Route path="/pets/:id" element={<PetProfilePage />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Perfil do Pet')).toBeDefined();
    expect(screen.getByText('Max')).toBeDefined();
    expect(screen.getByText('Golden Retriever')).toBeDefined();
    expect(screen.getByText('3 Anos')).toBeDefined();
    expect(screen.getByText('32 kg')).toBeDefined();
    expect(screen.getByText('Macho')).toBeDefined();
    expect(screen.getByText('Consulta de Rotina')).toBeDefined();
    expect(screen.getByText('Registro de Peso')).toBeDefined();
    expect(screen.getByText('Exame de Sangue')).toBeDefined();
  });
});
