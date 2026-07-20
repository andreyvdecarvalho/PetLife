import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoriesPage } from './MemoriesPage';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../components/molecules/Toast';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast');

describe('MemoriesPage', () => {
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
  });

  it('should render correctly with diary details and vertical timeline', () => {
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
      <MemoryRouter initialEntries={['/memories']}>
        <Routes>
          <Route path="/memories" element={<MemoriesPage />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Diário')).toBeDefined();
    expect(screen.getByText('Capture os momentos inesquecíveis da vida do seu pet.')).toBeDefined();
    expect(screen.getByText('Veja o quanto Bella cresceu!')).toBeDefined();
    expect(screen.getByText('Aniversário de 1 ano')).toBeDefined();
    expect(screen.getByText('Primeira ida à praia')).toBeDefined();
    expect(screen.getByText('Primeiro dia em casa')).toBeDefined();
  });

  it('should trigger photo upload modal on Add Photo button click', () => {
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
      <MemoryRouter initialEntries={['/memories']}>
        <Routes>
          <Route path="/memories" element={<MemoriesPage />} />
        </Routes>
      </MemoryRouter>
    );

    const addPhotoBtn = screen.getByRole('button', { name: /adicionar foto/i });
    expect(addPhotoBtn).toBeDefined();

    fireEvent.click(addPhotoBtn);

    expect(mockShowToast).toHaveBeenCalledWith('Funcionalidade de upload de fotos em breve! 📸', 'info');
  });

  it('should add a new memory card and trigger success toast when clicking the FAB', async () => {
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
      <MemoryRouter initialEntries={['/memories']}>
        <Routes>
          <Route path="/memories" element={<MemoriesPage />} />
        </Routes>
      </MemoryRouter>
    );

    const fab = screen.getByRole('button', { name: /adicionar nova memória/i });
    expect(fab).toBeDefined();

    fireEvent.click(fab);

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Diário de Memórias: Nova memória adicionada! ✨', 'success');
      expect(screen.getByText('Novo Momento Feliz')).toBeDefined();
    });
  });
});
