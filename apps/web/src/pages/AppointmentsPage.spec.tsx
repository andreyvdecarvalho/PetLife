import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { AppointmentsPage } from './AppointmentsPage';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../components/molecules/Toast';
import { useGetPets } from '../application/pet/useGetPets';
import { useConsultations } from '../application/consultation/useConsultations';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast');
vi.mock('../application/pet/useGetPets');
vi.mock('../application/consultation/useConsultations');

describe('AppointmentsPage', () => {
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
    (useGetPets as any).mockReturnValue({
      pets: [{ id: 'pet-1', name: 'Max', species: 'Cachorro' }],
      fetchPets: vi.fn(),
    });
    (useConsultations as any).mockReturnValue({
      consultations: [
        {
          id: 'app-1',
          petId: 'pet-1',
          veterinarianName: 'Dr. Ricardo Silva',
          specialty: 'Clínico Geral',
          clinicName: 'Clínica Vida Pet',
          date: new Date(Date.now() + 86400000).toISOString().split('T')[0], // Tomorrow
          time: '10:00',
          status: 'CONFIRMED'
        }
      ],
      loading: false,
      fetchConsultations: vi.fn()
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

    expect(mockShowToast).toHaveBeenCalledWith('Exibindo detalhes do agendamento', 'info');
  });

  it('should trigger success toast and confirm appointment when clicking Reagendar button', async () => {
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

    const rescheduleButtons = screen.getAllByRole('button', { name: /reagendar/i });
    expect(rescheduleButtons.length).toBe(1);

    fireEvent.click(rescheduleButtons[0]);

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Solicitação de reagendamento enviada! 🗓️', 'success');
    });
  });
});
