import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { RoutinePage } from './RoutinePage';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../components/molecules/Toast';
import { useGetPets } from '../application/pet/useGetPets';
import { useRoutineActivities } from '../application/routine/useRoutineActivities';
import { useMedications } from '../application/medications/useMedications';
import { useConsultations } from '../application/consultation/useConsultations';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast');
vi.mock('../application/pet/useGetPets');
vi.mock('../application/routine/useRoutineActivities');
vi.mock('../application/medications/useMedications');
vi.mock('../application/consultation/useConsultations');

describe('RoutinePage', () => {
  const mockShowToast = vi.fn();
  const mockUpdateStatus = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
    
    (useGetPets as any).mockReturnValue({
      pets: [{ id: 'pet-1', name: 'Max' }],
      fetchPets: vi.fn(),
    });

    (useRoutineActivities as any).mockReturnValue({
      activities: [
        {
          id: 'act-1',
          title: 'Passeio matinal',
          description: 'Passeio no parque',
          activityTime: '08:00:00',
          type: 'WALK',
          status: 'PENDING'
        }
      ],
      fetchActivities: vi.fn(),
      updateStatus: mockUpdateStatus.mockResolvedValue(true),
      addActivity: vi.fn()
    });

    (useMedications as any).mockReturnValue({
      medications: [],
      fetchMedications: vi.fn()
    });

    (useConsultations as any).mockReturnValue({
      consultations: [],
      fetchConsultations: vi.fn(),
      addConsultation: vi.fn()
    });
  });

  it('should render correctly with calendar and activities', () => {
    (useAuth as any).mockReturnValue({
      user: { id: '1', name: 'Camila', email: 'camila@example.com', plan: 'FREE', emailVerified: true },
      logout: vi.fn()
    });

    render(
      <MemoryRouter initialEntries={['/routine']}>
        <Routes>
          <Route path="/routine" element={<RoutinePage />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Minha Rotina')).toBeDefined();
    expect(screen.getByText('Acompanhe e planeje as atividades do seu pet.')).toBeDefined();
    
    // We expect the mocked activity to be there
    expect(screen.getByText('Passeio matinal')).toBeDefined();
  });

  it('should toggle pending activity to completed on click', async () => {
    (useAuth as any).mockReturnValue({
      user: { id: '1', name: 'Camila', email: 'camila@example.com', plan: 'FREE', emailVerified: true },
      logout: vi.fn()
    });

    render(
      <MemoryRouter initialEntries={['/routine']}>
        <Routes>
          <Route path="/routine" element={<RoutinePage />} />
        </Routes>
      </MemoryRouter>
    );

    const pendingActivity = screen.getByText('Passeio matinal');
    expect(pendingActivity).toBeDefined();

    const clickableItem = pendingActivity.closest('.routine-page__activity-item');
    expect(clickableItem).not.toBeNull();

    fireEvent.click(clickableItem!);

    await waitFor(() => {
      expect(mockUpdateStatus).toHaveBeenCalledWith('act-1', 'COMPLETED');
      expect(mockShowToast).toHaveBeenCalledWith('Status atualizado! 🎉', 'success');
    });
  });
});
