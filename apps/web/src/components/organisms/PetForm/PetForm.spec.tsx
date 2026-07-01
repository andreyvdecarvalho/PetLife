import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { PetForm } from './index';
import { useCreatePet } from '../../../application/pet/useCreatePet';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../../../application/pet/useCreatePet');

describe('PetForm Component', () => {
  const mockCreatePet = vi.fn();
  const mockOnSuccess = vi.fn();
  const mockOnCancel = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useCreatePet as any).mockReturnValue({
      createPet: mockCreatePet,
      loading: false,
      error: null,
    });
  });

  it('should render form fields correctly', () => {
    render(<PetForm onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    expect(screen.getByText('Cadastrar Novo Pet')).toBeDefined();
    expect(screen.getByLabelText('Nome do Pet')).toBeDefined();
    expect(screen.getByLabelText('Espécie')).toBeDefined();
    expect(screen.getByLabelText('Raça')).toBeDefined();
    expect(screen.getByLabelText('Sexo')).toBeDefined();
    expect(screen.getByLabelText('Data de Nascimento')).toBeDefined();
    expect(screen.getByLabelText('Peso (kg)')).toBeDefined();
    expect(screen.getByLabelText('Porte')).toBeDefined();
    expect(screen.getByLabelText('Nº do Microchip')).toBeDefined();
    expect(screen.getByLabelText('O pet é castrado')).toBeDefined();
    expect(screen.getByLabelText('Alergias')).toBeDefined();
    expect(screen.getByLabelText('Observações / Notas de Cuidados')).toBeDefined();
  });

  it('should show validation error when submitting with empty name', async () => {
    render(<PetForm onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    const submitBtn = screen.getByRole('button', { name: 'Cadastrar Pet' });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(screen.getByText('O nome é obrigatório.')).toBeDefined();
    });
    expect(mockCreatePet).not.toHaveBeenCalled();
  });

  it('should call onCancel when clicking cancel button', () => {
    render(<PetForm onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    const cancelBtn = screen.getByRole('button', { name: 'Cancelar' });
    fireEvent.click(cancelBtn);

    expect(mockOnCancel).toHaveBeenCalled();
  });

  it('should call createPet and onSuccess when form is valid', async () => {
    mockCreatePet.mockResolvedValue({ id: 'pet-123', name: 'Luna' });
    
    render(<PetForm onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    const nameInput = screen.getByLabelText('Nome do Pet');
    const speciesSelect = screen.getByLabelText('Espécie');
    const breedInput = screen.getByLabelText('Raça');
    const submitBtn = screen.getByRole('button', { name: 'Cadastrar Pet' });

    // Preenche dados
    fireEvent.change(nameInput, { target: { value: 'Luna' } });
    fireEvent.change(speciesSelect, { target: { value: 'CAT' } });
    fireEvent.change(breedInput, { target: { value: 'Siamês' } });

    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(mockCreatePet).toHaveBeenCalledWith(
        expect.objectContaining({
          name: 'Luna',
          species: 'CAT',
          breed: 'Siamês',
        }),
        undefined
      );
    });

    await waitFor(() => {
      expect(mockOnSuccess).toHaveBeenCalled();
    });
  });
});
