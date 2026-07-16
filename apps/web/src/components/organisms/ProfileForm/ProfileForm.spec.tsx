import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { ProfileForm } from '.';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import { useAuth } from '../../../contexts/AuthContext';
import React from 'react';

vi.mock('../../molecules/Toast', () => ({
  useToast: vi.fn(),
}));

vi.mock('../../../contexts/AuthContext', () => ({
  useAuth: vi.fn(),
}));

vi.mock('../../../utils/imageCompressor', () => ({
  compressImage: vi.fn().mockResolvedValue(new File([''], 'photo.jpg', { type: 'image/jpeg' })),
}));

describe('ProfileForm Component', () => {
  const mockShowToast = vi.fn();
  const mockUpdateProfile = vi.fn();
  const mockDeleteAccount = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    mockUpdateProfile.mockReset();
    mockDeleteAccount.mockReset();
    mockShowToast.mockReset();
    
    mockUpdateProfile.mockResolvedValue({});
    mockDeleteAccount.mockResolvedValue({});

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
    expect(screen.getByLabelText(/^Nome Completo$/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /Salvar Alterações/i })).toBeDefined();
  });

  it('should handle photo upload and submission', async () => {
    renderComponent();
    
    // Simulate photo upload
    const fileInput = screen.getByTestId('input-foto-pet');
    const file = new File(['dummy content'], 'test.png', { type: 'image/png' });
    await act(async () => {
      fireEvent.change(fileInput, { target: { files: [file] } });
    });

    act(() => {
      fireEvent.change(screen.getByLabelText(/^Nome Completo$/i), { target: { value: 'New Name' } });
    });
    
    act(() => {
      fireEvent.submit(screen.getByRole('button', { name: /Salvar Alterações/i }).closest('form')!);
    });
    
    await waitFor(() => {
      // It should have passed the mock File to updateProfile
      expect(mockUpdateProfile).toHaveBeenCalledWith(
        expect.any(Object),
        expect.any(File)
      );
      expect(mockShowToast).toHaveBeenCalledWith('Perfil atualizado com sucesso! ✨', 'success');
    });
  });

  it('should submit successfully', async () => {
    renderComponent();
    
    act(() => {
      fireEvent.change(screen.getByLabelText(/^Nome Completo$/i), { target: { value: 'New Name' } });
    });

    act(() => {
      fireEvent.submit(screen.getByRole('button', { name: /Salvar Alterações/i }).closest('form')!);
    });
    
    await waitFor(() => {
      expect(mockUpdateProfile).toHaveBeenCalledWith({
        name: 'New Name',
        nickname: undefined,
        email: 'test@test.com',
        phone: undefined,
        avatarUrl: '',
        timezone: 'America/Sao_Paulo'
      }, undefined);
      expect(mockShowToast).toHaveBeenCalledWith('Perfil atualizado com sucesso! ✨', 'success');
    });
  });

  it('should validate empty form fields before submitting', async () => {
    renderComponent();
    
    // Test empty name and email
    act(() => {
      fireEvent.change(screen.getByLabelText(/^Nome Completo$/i), { target: { value: '' } });
      fireEvent.change(screen.getByLabelText(/^E-mail$/i), { target: { value: '' } });
    });
    
    act(() => {
      fireEvent.submit(screen.getByRole('button', { name: /Salvar Alterações/i }).closest('form')!);
    });
    
    await waitFor(() => {
      expect(screen.getByText('O nome é obrigatório.')).toBeDefined();
      expect(screen.getByText('O e-mail é obrigatório.')).toBeDefined();
      expect(mockUpdateProfile).not.toHaveBeenCalled();
    });
  });

  it('should validate short name and invalid email', async () => {
    renderComponent();

    act(() => {
      fireEvent.change(screen.getByLabelText(/^Nome Completo$/i), { target: { value: 'A' } });
      fireEvent.change(screen.getByLabelText(/^E-mail$/i), { target: { value: 'invalid-email' } });
    });

    act(() => {
      fireEvent.submit(screen.getByRole('button', { name: /Salvar Alterações/i }).closest('form')!);
    });

    await waitFor(() => {
      expect(screen.getByText('O nome deve ter no mínimo 2 caracteres.')).toBeDefined();
      expect(screen.getByText('Formato de e-mail inválido.')).toBeDefined();
      expect(mockUpdateProfile).not.toHaveBeenCalled();
    });
  });

  it('should handle submit failure', async () => {
    mockUpdateProfile.mockRejectedValueOnce({ response: { data: { error: { message: 'Erro na API' } } } });
    renderComponent();
    
    act(() => {
      fireEvent.change(screen.getByLabelText(/^Nome Completo$/i), { target: { value: 'Valid Name' } });
      fireEvent.change(screen.getByLabelText(/^E-mail$/i), { target: { value: 'valid@test.com' } });
    });
    
    act(() => {
      fireEvent.submit(screen.getByRole('button', { name: /Salvar Alterações/i }).closest('form')!);
    });
    
    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Erro na API', 'error');
    });
  });

  it('should handle account deletion flow', async () => {
    renderComponent();
    
    // Click initial delete button
    fireEvent.click(screen.getByRole('button', { name: /Excluir Conta/i }));
    expect(screen.getByText(/Tem certeza absoluta/i)).toBeDefined();

    // Click cancel
    fireEvent.click(screen.getByRole('button', { name: /Cancelar/i }));
    expect(screen.queryByText(/Tem certeza absoluta/i)).toBeNull();

    // Click delete again and confirm
    fireEvent.click(screen.getByRole('button', { name: /Excluir Conta/i }));
    fireEvent.click(screen.getByRole('button', { name: /Sim, excluir minha conta permanentemente/i }));

    await waitFor(() => {
      expect(mockDeleteAccount).toHaveBeenCalled();
      expect(mockShowToast).toHaveBeenCalledWith('Sua conta foi excluída com sucesso.', 'success');
    });
  });

  it('should handle account deletion failure', async () => {
    mockDeleteAccount.mockRejectedValueOnce(new Error('Network error'));
    renderComponent();
    
    fireEvent.click(screen.getByRole('button', { name: /Excluir Conta/i }));
    fireEvent.click(screen.getByRole('button', { name: /Sim, excluir minha conta permanentemente/i }));

    await waitFor(() => {
      expect(mockDeleteAccount).toHaveBeenCalled();
      expect(mockShowToast).toHaveBeenCalledWith('Erro ao excluir conta. Tente novamente.', 'error');
    });
  });
});
