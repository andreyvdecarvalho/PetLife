import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { AppointmentsPage } from './AppointmentsPage';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../components/molecules/Toast';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast');

describe('AppointmentsPage', () => {
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
  });

  it('should render correctly with appointments list and professional details', () => {
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
      <MemoryRouter initialEntries={['/appointments']}>
        <Routes>
          <Route path="/appointments" element={<AppointmentsPage />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Meus Agendamentos')).toBeDefined();
    expect(screen.getByText('Próximos')).toBeDefined();
    expect(screen.getByText('Histórico')).toBeDefined();
    expect(screen.getByText('Dr. Ricardo Silva')).toBeDefined();
    expect(screen.getByText('Clínica Vida Pet')).toBeDefined();
    expect(screen.getByText('Pet Shop Estilo')).toBeDefined();
    expect(screen.getByText('Banho & Tosa')).toBeDefined();
  });

  it('should trigger info toast when clicking Ver Detalhes button', () => {
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
      <MemoryRouter initialEntries={['/appointments']}>
        <Routes>
          <Route path="/appointments" element={<AppointmentsPage />} />
        </Routes>
      </MemoryRouter>
    );

    const detailsButtons = screen.getAllByRole('button', { name: /ver detalhes/i });
    expect(detailsButtons.length).toBeGreaterThan(0);

    fireEvent.click(detailsButtons[0]);

    expect(mockShowToast).toHaveBeenCalledWith('Exibindo detalhes do agendamento para Max', 'info');
  });

  it('should trigger success toast and confirm appointment when clicking Reagendar button on a pending appointment', async () => {
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
      <MemoryRouter initialEntries={['/appointments']}>
        <Routes>
          <Route path="/appointments" element={<AppointmentsPage />} />
        </Routes>
      </MemoryRouter>
    );

    // Get Reagendar button for Luna (pending)
    // The second card belongs to Luna (Pet Shop Estilo)
    const rescheduleButtons = screen.getAllByRole('button', { name: /reagendar/i });
    expect(rescheduleButtons.length).toBe(2);

    fireEvent.click(rescheduleButtons[1]); // Reagendar for Luna

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Solicitação de reagendamento para Luna enviada! 🗓️', 'success');
      // The status badge for Luna should turn to "Confirmado"
      const confirmedBadges = screen.getAllByText('Confirmado');
      expect(confirmedBadges.length).toBe(2); // Both Max and Luna are now confirmed
    });
  });
});
