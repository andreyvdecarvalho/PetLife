import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ProfileForm } from './ProfileForm';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { useToast } from '../../molecules/Toast/Toast';
import { useAuth } from '../../../contexts/AuthContext';
import React from 'react';

vi.mock('../../molecules/Toast/Toast', () => ({
  useToast: vi.fn(),
}));

vi.mock('../../../contexts/AuthContext', () => ({
  useAuth: vi.fn(),
}));

describe('ProfileForm Component', () => {
  const mockShowToast = vi.fn();
  const mockUpdateProfile = vi.fn();
  const mockDeleteAccount = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({ showToast: mockShowToast });
    (useAuth as any).mockReturnValue({
      user: { name: 'Test User', email: 'test@test.com', timezone: 'America/Sao_Paulo', avatarUrl: '' },
      updateProfile: mockUpdateProfile,
      deleteAccount: mockDeleteAccount,
    });
  });

  const renderComponent = () => {
    return render(
      <MemoryRouter>
        <ProfileForm />
      </MemoryRouter>
    );
  };

  it('should render form fields', () => {
    renderComponent();
    expect(screen.getByLabelText(/^Nome$/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /Salvar Alterações/i })).toBeDefined();
  });

  it('should submit successfully', async () => {
    mockUpdateProfile.mockResolvedValueOnce({});
    renderComponent();
    
    fireEvent.change(screen.getByLabelText(/^Nome$/i), { target: { value: 'New Name' } });
    fireEvent.click(screen.getByRole('button', { name: /Salvar Alterações/i }));
    
    await waitFor(() => {
      expect(mockUpdateProfile).toHaveBeenCalledWith('New Name', 'test@test.com', '', 'America/Sao_Paulo');
      expect(mockShowToast).toHaveBeenCalledWith('Perfil atualizado com sucesso! ✨', 'success');
    });
  });
});
