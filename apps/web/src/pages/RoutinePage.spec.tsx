import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { RoutinePage } from './RoutinePage';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../components/molecules/Toast';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast');

describe('RoutinePage', () => {
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
  });

  it('should render correctly with calendar and activities', () => {
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
      <MemoryRouter initialEntries={['/routine']}>
        <Routes>
          <Route path="/routine" element={<RoutinePage />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Minha Rotina')).toBeDefined();
    expect(screen.getByText('Acompanhe e planeje as atividades do seu pet.')).toBeDefined();
    expect(screen.getByText('Outubro 2023')).toBeDefined();
    expect(screen.getByText('12 de Outubro, Quinta')).toBeDefined();
    expect(screen.getByText('Passeio matinal')).toBeDefined();
    expect(screen.getByText('Administração de colírio')).toBeDefined();
    expect(screen.getByText('Banho')).toBeDefined();
  });

  it('should toggle pending activity to completed on click', async () => {
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
      <MemoryRouter initialEntries={['/routine']}>
        <Routes>
          <Route path="/routine" element={<RoutinePage />} />
        </Routes>
      </MemoryRouter>
    );

    const pendingActivity = screen.getByText('Administração de colírio');
    expect(pendingActivity).toBeDefined();

    // The activity item itself is clickable to toggle status
    const clickableItem = pendingActivity.closest('.routine-page__activity-item');
    expect(clickableItem).not.toBeNull();

    fireEvent.click(clickableItem!);

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Atividade concluída com sucesso! 🎉', 'success');
    });
  });
});
