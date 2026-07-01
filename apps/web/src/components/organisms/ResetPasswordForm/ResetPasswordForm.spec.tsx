import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ResetPasswordForm } from '.';
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
  setUnauthorizedCallback: vi.fn()
}));

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => vi.fn(),
    useSearchParams: () => [new URLSearchParams('?token=test-token')],
  };
});

describe('ResetPasswordForm Component', () => {
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({ showToast: mockShowToast });
  });

  const renderComponent = () => {
    return render(
      <MemoryRouter>
        <ResetPasswordForm />
      </MemoryRouter>
    );
  };

  it('should render form fields', () => {
    renderComponent();
    expect(screen.getByLabelText(/Nova Senha/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /Alterar Senha/i })).toBeDefined();
  });

  it('should submit successfully', async () => {
    (api.post as any).mockResolvedValueOnce({ data: {} });
    renderComponent();
    
    fireEvent.change(screen.getByLabelText(/Nova Senha/i), { target: { value: 'NovaSenha@123' } });
    fireEvent.click(screen.getByRole('button', { name: /Alterar Senha/i }));
    
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/reset-password', { token: 'test-token', newPassword: 'NovaSenha@123' });
      expect(mockShowToast).toHaveBeenCalledWith('Senha redefinida com sucesso! ✨', 'success');
    });
  });
});
