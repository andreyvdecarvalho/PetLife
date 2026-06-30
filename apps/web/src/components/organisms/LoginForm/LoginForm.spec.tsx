import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { LoginForm } from '.';
import { useAuth } from '../../../contexts/AuthContext';
import { useToast } from '../../molecules/Toast';

// Mock dos contexts e react-router
vi.mock('../../../contexts/AuthContext', () => ({
  useAuth: vi.fn(),
}));

vi.mock('../../molecules/Toast', () => ({
  useToast: vi.fn(),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<any>('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('LoginForm Component', () => {
  const mockLogin = vi.fn();
  const mockLoginWithGoogle = vi.fn();
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useAuth as any).mockReturnValue({
      login: mockLogin,
      loginWithGoogle: mockLoginWithGoogle,
    });
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
  });

  it('should render form fields and submit button', () => {
    render(
      <MemoryRouter>
        <LoginForm />
      </MemoryRouter>
    );

    expect(screen.getByLabelText(/e-mail/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/senha/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /entrar/i })).toBeInTheDocument();
  });

  it('should validate empty fields on submit', async () => {
    render(
      <MemoryRouter>
        <LoginForm />
      </MemoryRouter>
    );

    const submitBtn = screen.getByRole('button', { name: /entrar/i });
    fireEvent.click(submitBtn);

    expect(await screen.findByText('O e-mail é obrigatório.')).toBeInTheDocument();
    expect(await screen.findByText('A senha é obrigatória.')).toBeInTheDocument();
    expect(mockLogin).not.toHaveBeenCalled();
  });

  it('should display error for invalid email format', async () => {
    render(
      <MemoryRouter>
        <LoginForm />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/e-mail/i), { target: { value: 'emailinvalido' } });
    fireEvent.click(screen.getByRole('button', { name: /entrar/i }));

    expect(await screen.findByText('Formato de e-mail inválido.')).toBeInTheDocument();
    expect(mockLogin).not.toHaveBeenCalled();
  });

  it('should call login and navigate to dashboard on success', async () => {
    mockLogin.mockResolvedValueOnce(undefined);

    render(
      <MemoryRouter>
        <LoginForm />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/e-mail/i), { target: { value: 'tutor@petlife.com' } });
    fireEvent.change(screen.getByLabelText(/senha/i), { target: { value: 'Senha@123' } });
    fireEvent.click(screen.getByRole('button', { name: /entrar/i }));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('tutor@petlife.com', 'Senha@123');
      expect(mockShowToast).toHaveBeenCalledWith('Login realizado com sucesso! ✨', 'success');
      expect(mockNavigate).toHaveBeenCalledWith('/');
    });
  });

  it('should display error toast on login failure', async () => {
    const errorResponse = {
      response: {
        data: {
          error: {
            message: 'Credenciais inválidas.',
          },
        },
      },
    };
    mockLogin.mockRejectedValueOnce(errorResponse);

    render(
      <MemoryRouter>
        <LoginForm />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/e-mail/i), { target: { value: 'tutor@petlife.com' } });
    fireEvent.change(screen.getByLabelText(/senha/i), { target: { value: 'Senha@123' } });
    fireEvent.click(screen.getByRole('button', { name: /entrar/i }));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalled();
      expect(mockShowToast).toHaveBeenCalledWith('Credenciais inválidas.', 'error');
      expect(mockNavigate).not.toHaveBeenCalled();
    });
  });

  it('should call loginWithGoogle and navigate on Google login success', async () => {
    mockLoginWithGoogle.mockResolvedValueOnce(undefined);

    render(
      <MemoryRouter>
        <LoginForm />
      </MemoryRouter>
    );

    const googleBtn = screen.getByRole('button', { name: /google/i });
    fireEvent.click(googleBtn);

    await waitFor(() => {
      expect(mockLoginWithGoogle).toHaveBeenCalledWith(
        "dummyHeader.eyJlbWFpbCI6Imdvb2dsZS50dXRvckBwZXRsaWZlLmNvbSIsIm5hbWUiOiJHb29nbGUgVHV0b3IiLCJwaWN0dXJlIjoiaHR0cDovL2dvb2dsZS51cmwvYXZhdGFyIn0.dummySignature"
      );
      expect(mockShowToast).toHaveBeenCalledWith('Autenticado via Google com sucesso! ✨', 'success');
      expect(mockNavigate).toHaveBeenCalledWith('/');
    });
  });

  it('should display error toast on Google login failure', async () => {
    const errorResponse = {
      response: {
        data: {
          error: {
            message: 'Falha ao autenticar com o Google.',
          },
        },
      },
    };
    mockLoginWithGoogle.mockRejectedValueOnce(errorResponse);

    render(
      <MemoryRouter>
        <LoginForm />
      </MemoryRouter>
    );

    const googleBtn = screen.getByRole('button', { name: /google/i });
    fireEvent.click(googleBtn);

    await waitFor(() => {
      expect(mockLoginWithGoogle).toHaveBeenCalled();
      expect(mockShowToast).toHaveBeenCalledWith('Falha ao autenticar com o Google.', 'error');
      expect(mockNavigate).not.toHaveBeenCalled();
    });
  });
});
