import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { AppointmentsPage } from './AppointmentsPage';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../components/molecules/Toast';
import { useGetPets } from '../application/pet/useGetPets';
import { consultationApi } from '../infrastructure/http/consultation.api';
import { vaccinationApi } from '../infrastructure/http/vaccination.api';
import { groomingApi } from '../infrastructure/http/grooming.api';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast');
vi.mock('../application/pet/useGetPets');
vi.mock('../infrastructure/http/consultation.api');
vi.mock('../infrastructure/http/vaccination.api');
vi.mock('../infrastructure/http/grooming.api');

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

    (consultationApi.list as any).mockResolvedValue([
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
    ]);
    (vaccinationApi.listVaccinations as any).mockResolvedValue([]);
    (groomingApi.listGroomings as any).mockResolvedValue([]);
  });

  it('should render correctly with appointments list and professional details', async () => {
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

    await waitFor(() => {
      expect(screen.getByText('Meus Agendamentos')).toBeDefined();
      expect(screen.getByText('Próximos')).toBeDefined();
      expect(screen.getByText('Histórico')).toBeDefined();
      expect(screen.getByText('Consulta com Dr. Ricardo Silva')).toBeDefined();
      expect(screen.getByText('Clínica Vida Pet')).toBeDefined();
    });
  });

  it('should trigger info toast when clicking Ver Detalhes button', async () => {
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

    await waitFor(() => {
      expect(screen.queryAllByRole('button', { name: /ver detalhes/i }).length).toBeGreaterThan(0);
    });

    const detailsButtons = screen.getAllByRole('button', { name: /ver detalhes/i });
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

    await waitFor(() => {
      expect(screen.queryAllByRole('button', { name: /reagendar/i }).length).toBe(1);
    });

    const rescheduleButtons = screen.getAllByRole('button', { name: /reagendar/i });
    fireEvent.click(rescheduleButtons[0]);

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Solicitação de reagendamento enviada! 🗓️', 'success');
    });
  });
});
