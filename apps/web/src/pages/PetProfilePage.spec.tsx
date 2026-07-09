import { render, screen } from '@testing-library/react';
import { PetProfilePage } from './PetProfilePage';
import { useAuth } from '../contexts/AuthContext';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');

vi.mock('../components/molecules/Toast', () => ({
  useToast: () => ({
    showToast: vi.fn(),
  }),
}));

vi.mock('../application/pet/useTimeline', () => ({
  useTimeline: () => ({
    events: [
      { id: 'rec-1', type: 'CONSULTATION', date: '2026-07-08T00:00:00Z', title: 'Consulta de Rotina', description: 'Exame geral', icon: 'local_hospital', color: '#10B981' },
      { id: 'rec-2', type: 'WEIGHT', date: '2026-07-08T00:00:00Z', title: 'Registro de Peso', description: 'Acompanhamento do peso corporal', icon: 'monitoring', color: '#3B82F6' },
      { id: 'rec-3', type: 'CONSULTATION', date: '2026-07-08T00:00:00Z', title: 'Exame de Sangue', description: 'Hemograma', icon: 'science', color: '#006b55' },
    ],
    isLoading: false,
    error: null,
    hasMore: false,
    fetchTimeline: vi.fn(),
  }),
}));

vi.mock('../application/pet/useExportMedicalPass', () => ({
  useExportMedicalPass: () => ({
    isExporting: false,
    exportError: null,
    exportMedicalPass: vi.fn(),
  }),
}));

vi.mock('../components/organisms/VaccinationsTab', () => ({
  VaccinationsTab: () => null,
}));

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
