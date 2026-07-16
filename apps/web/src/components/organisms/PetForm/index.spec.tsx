import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { PetForm } from './index';
import { useCreatePet } from '../../../application/pet/useCreatePet';
import { useUpdatePet } from '../../../application/pet/useUpdatePet';

// Mocks
vi.mock('../../../application/pet/useCreatePet');
vi.mock('../../../application/pet/useUpdatePet');

describe('PetForm', () => {
  const mockCreatePet = vi.fn();
  const mockUpdatePet = vi.fn();
  const mockOnSuccess = vi.fn();
  const mockOnCancel = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useCreatePet as any).mockReturnValue({
      createPet: mockCreatePet,
      loading: false,
      error: null,
    });
    (useUpdatePet as any).mockReturnValue({
      updatePet: mockUpdatePet,
      loading: false,
      error: null,
    });
  });

  it('should render form fields correctly for new pet', () => {
    render(<PetForm onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);
    
    expect(screen.getByText('Cadastrar Novo Pet')).toBeInTheDocument();
    expect(screen.getByTestId('input-nome-pet')).toBeInTheDocument();
    expect(screen.getByTestId('select-especie-pet')).toBeInTheDocument();
  });

  it('should validate required fields before submit', async () => {
    render(<PetForm onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);
    
    // Attempt to submit without filling name
    fireEvent.click(screen.getByTestId('btn-salvar-pet'));
    
    await waitFor(() => {
      expect(screen.getByText('O nome é obrigatório.')).toBeInTheDocument();
    });
    expect(mockCreatePet).not.toHaveBeenCalled();
  });

  it('should submit successfully when fields are filled', async () => {
    render(<PetForm onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);
    
    const nameInput = screen.getByTestId('input-nome-pet');
    fireEvent.change(nameInput, { target: { value: 'Buddy' } });
    
    fireEvent.click(screen.getByTestId('btn-salvar-pet'));
    
    await waitFor(() => {
      expect(mockCreatePet).toHaveBeenCalledWith(
        expect.objectContaining({ name: 'Buddy', species: 'DOG' }),
        undefined
      );
      expect(mockOnSuccess).toHaveBeenCalled();
    });
  });

  it('should trigger onCancel when cancel button is clicked', () => {
    render(<PetForm onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);
    
    fireEvent.click(screen.getByTestId('btn-cancelar-pet'));
    
    expect(mockOnCancel).toHaveBeenCalled();
  });
});
