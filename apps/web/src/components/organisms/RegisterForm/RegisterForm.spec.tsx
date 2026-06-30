import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { RegisterForm } from '.';
import { useAuth } from '../../../contexts/AuthContext';
import { useToast } from '../../molecules/Toast';
import React from 'react';

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

describe('RegisterForm Component', () => {
  const mockRegister = vi.fn();
  const mockLoginWithGoogle = vi.fn();
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useAuth as any).mockReturnValue({
      register: mockRegister,
      loginWithGoogle: mockLoginWithGoogle,
    });
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
  });

  it('should render correctly', () => {
    render(<MemoryRouter><RegisterForm /></MemoryRouter>);
    expect(screen.getByLabelText(/nome/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/e-mail/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/^senha/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /^cadastrar$/i })).toBeInTheDocument();
  });

  it('should validate empty fields on submit', async () => {
    render(<MemoryRouter><RegisterForm /></MemoryRouter>);
    fireEvent.click(screen.getByRole('button', { name: /^cadastrar$/i }));

    expect(await screen.findByText('O nome é obrigatório.')).toBeInTheDocument();
    expect(await screen.findByText('O e-mail é obrigatório.')).toBeInTheDocument();
    expect(await screen.findByText('A senha é obrigatória.')).toBeInTheDocument();
    expect(mockRegister).not.toHaveBeenCalled();
  });

  it('should call register on valid submit', async () => {
    mockRegister.mockResolvedValueOnce(undefined);
    render(<MemoryRouter><RegisterForm /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText(/nome/i), { target: { value: 'Test User' } });
    fireEvent.change(screen.getByLabelText(/e-mail/i), { target: { value: 'test@test.com' } });
    fireEvent.change(screen.getByLabelText(/^senha/i), { target: { value: 'Senha@123' } });
    fireEvent.click(screen.getByRole('button', { name: /^cadastrar$/i }));

    await waitFor(() => {
      expect(mockRegister).toHaveBeenCalledWith('Test User', 'test@test.com', 'Senha@123');
      expect(mockShowToast).toHaveBeenCalledWith('Conta criada com sucesso! Seja bem-vindo(a) ✨', 'success');
      expect(mockNavigate).toHaveBeenCalledWith('/');
    });
  });
});
