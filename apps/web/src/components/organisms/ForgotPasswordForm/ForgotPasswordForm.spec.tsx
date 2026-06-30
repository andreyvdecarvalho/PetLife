import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ForgotPasswordForm } from '.';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import api from '../../../services/api';
import React from 'react';

vi.mock('../../molecules/Toast', () => ({
  useToast: vi.fn(),
}));

vi.mock('../../../services/api', () => ({
  default: {
    post: vi.fn(),
  },
}));

describe('ForgotPasswordForm Component', () => {
  const mockShowToast = vi.fn();
  const mockNavigate = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({ showToast: mockShowToast });
  });

  const renderComponent = () => {
    return render(
      <MemoryRouter>
        <ForgotPasswordForm />
      </MemoryRouter>
    );
  };

  it('should render form fields', () => {
    renderComponent();
    expect(screen.getByLabelText(/E-mail/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /Enviar Link/i })).toBeDefined();
  });

  it('should display validation errors if empty submit', async () => {
    renderComponent();
    fireEvent.click(screen.getByRole('button', { name: /Enviar Link/i }));
    
    await waitFor(() => {
      expect(screen.getByText('O e-mail é obrigatório.')).toBeDefined();
    });
  });

  it('should submit successfully', async () => {
    (api.post as any).mockResolvedValueOnce({ data: {} });
    renderComponent();
    
    fireEvent.change(screen.getByLabelText(/E-mail/i), { target: { value: 'test@test.com' } });
    fireEvent.click(screen.getByRole('button', { name: /Enviar Link/i }));
    
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/forgot-password', { email: 'test@test.com' });
      expect(mockShowToast).toHaveBeenCalledWith('Solicitação processada com sucesso! ✨', 'success');
    });
  });

  it('should display error toast on failure', async () => {
    (api.post as any).mockRejectedValueOnce({
      response: { data: { error: { message: 'Erro no servidor' } } }
    });
    renderComponent();
    
    fireEvent.change(screen.getByLabelText(/E-mail/i), { target: { value: 'test@test.com' } });
    fireEvent.click(screen.getByRole('button', { name: /Enviar Link/i }));
    
    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Ocorreu um erro ao processar. Tente novamente.', 'error');
    });
  });
});
