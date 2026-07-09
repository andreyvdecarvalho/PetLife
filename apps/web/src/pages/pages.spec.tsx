import { render, screen } from '@testing-library/react';
import { ProfilePage } from './ProfilePage';
import { RegisterPage } from './RegisterPage';
import { ForgotPasswordPage } from './ForgotPasswordPage';
import { ResetPasswordPage } from './ResetPasswordPage';
import { useAuth } from '../contexts/AuthContext';
import { MemoryRouter } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast', () => ({
  useToast: () => ({
    showToast: vi.fn(),
  }),
}));

describe('Wrapper Pages', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    (useAuth as any).mockReturnValue({
      user: {
        id: '1',
        name: 'Camila',
        email: 'camila@example.com',
        plan: 'FREE',
        emailVerified: true
      },
      logout: vi.fn(),
      loading: false,
    });
  });

  it('should render ProfilePage correctly', () => {
    render(
      <MemoryRouter>
        <ProfilePage />
      </MemoryRouter>
    );
    expect(screen.getAllByText('Meu Perfil').length).toBeGreaterThan(0);
  });

  it('should render RegisterPage correctly', () => {
    render(
      <MemoryRouter>
        <RegisterPage />
      </MemoryRouter>
    );
    expect(screen.getByText('Crie sua conta')).toBeDefined();
  });

  it('should render ForgotPasswordPage correctly', () => {
    render(
      <MemoryRouter>
        <ForgotPasswordPage />
      </MemoryRouter>
    );
    expect(screen.getByText('Recuperar Senha')).toBeDefined();
  });

  it('should render ResetPasswordPage correctly', () => {
    render(
      <MemoryRouter>
        <ResetPasswordPage />
      </MemoryRouter>
    );
    expect(screen.getAllByText('Nova Senha').length).toBeGreaterThan(0);
  });
});
