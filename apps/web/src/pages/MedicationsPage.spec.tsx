import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MedicationsPage } from './MedicationsPage';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../components/molecules/Toast';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast');

describe('MedicationsPage', () => {
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
  });

  it('should render correctly with treatments list and history', () => {
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
      <MemoryRouter initialEntries={['/medications']}>
        <Routes>
          <Route path="/medications" element={<MedicationsPage />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Controle de Medicamentos')).toBeDefined();
    expect(screen.getByText('Tratamentos Ativos')).toBeDefined();
    expect(screen.getByText('2 Ativos')).toBeDefined();
    expect(screen.getAllByText('Antibiótico Amoxicilina').length).toBeGreaterThan(0);
    expect(screen.getAllByText('Colírio Optivet').length).toBeGreaterThan(0);
    expect(screen.getByText('Histórico Recente')).toBeDefined();
  });

  it('should allow marking a pending treatment as taken', async () => {
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
      <MemoryRouter initialEntries={['/medications']}>
        <Routes>
          <Route path="/medications" element={<MedicationsPage />} />
        </Routes>
      </MemoryRouter>
    );

    const takeButton = screen.getByRole('button', { name: /marcar como tomado/i });
    expect(takeButton).toBeDefined();

    fireEvent.click(takeButton);

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Dose registrada com sucesso! ✨', 'success');
    });
  });
});
