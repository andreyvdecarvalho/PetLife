import { render, screen, fireEvent } from '@testing-library/react';
import { PetFormPage } from '.';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { MemoryRouter, useNavigate, useLocation } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import React from 'react';

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: vi.fn(),
    useLocation: vi.fn(),
  };
});

vi.mock('../../molecules/Toast', () => ({
  useToast: vi.fn(),
}));

vi.mock('../../organisms/PetForm', () => ({
  PetForm: ({ onSuccess, onCancel }: any) => (
    <div data-testid="mock-pet-form">
      <button onClick={onSuccess}>Simulate Success</button>
      <button onClick={onCancel}>Simulate Cancel</button>
    </div>
  ),
}));

describe('PetFormPage Content Component', () => {
  const mockNavigate = vi.fn();
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useNavigate as any).mockReturnValue(mockNavigate);
    (useLocation as any).mockReturnValue({ state: null });
    (useToast as any).mockReturnValue({ showToast: mockShowToast });
  });

  const renderComponent = () => {
    return render(
      <MemoryRouter>
        <PetFormPage />
      </MemoryRouter>
    );
  };

  it('should render create form when no pet in state', () => {
    renderComponent();
    expect(screen.getByText('Cadastrar Novo Pet')).toBeDefined();
    expect(screen.getByTestId('mock-pet-form')).toBeDefined();
  });

  it('should render edit form when pet is passed in state', () => {
    (useLocation as any).mockReturnValue({ state: { pet: { id: '1', name: 'Rex' } } });
    renderComponent();
    expect(screen.getByText('Editar Pet')).toBeDefined();
  });

  it('should navigate back on back button click', () => {
    renderComponent();
    fireEvent.click(screen.getByRole('button', { name: /voltar/i }));
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('should navigate and show toast on form success (create)', () => {
    renderComponent();
    fireEvent.click(screen.getByText('Simulate Success'));
    expect(mockShowToast).toHaveBeenCalledWith('Pet cadastrado com sucesso! ✨', 'success');
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('should navigate and show toast on form success (edit)', () => {
    (useLocation as any).mockReturnValue({ state: { pet: { id: '1', name: 'Rex' } } });
    renderComponent();
    fireEvent.click(screen.getByText('Simulate Success'));
    expect(mockShowToast).toHaveBeenCalledWith('Pet atualizado com sucesso! ✨', 'success');
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('should navigate on form cancel', () => {
    renderComponent();
    fireEvent.click(screen.getByText('Simulate Cancel'));
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });
});
