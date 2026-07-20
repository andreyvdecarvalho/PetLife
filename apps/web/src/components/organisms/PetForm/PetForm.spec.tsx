import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { PetForm } from './index';
import { useCreatePet } from '../../../application/pet/useCreatePet';
import { useUpdatePet } from '../../../application/pet/useUpdatePet';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import type { Pet } from '../../../domain/pet/Pet';

vi.mock('../../../application/pet/useCreatePet');
vi.mock('../../../application/pet/useUpdatePet');

describe('PetForm Component', () => {
  const mockCreatePet = vi.fn();
  const mockUpdatePet = vi.fn();
  const mockOnSuccess = vi.fn();
  const mockOnCancel = vi.fn();

  const samplePet: Pet = {
    id: 'pet-123',
    userId: 'user-456',
    name: 'Rex',
    species: 'DOG',
    breed: 'Labrador',
    sex: 'MALE',
    birthDate: '2023-01-01',
    weightKg: 25.5,
    size: 'LARGE',
    neutered: true,
    microchipId: '98100',
    allergies: 'Poeira',
    notes: 'Muito dócil',
    status: 'ACTIVE',
    createdAt: '2023-01-01T00:00:00Z',
    updatedAt: '2023-01-01T00:00:00Z',
  };

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

  it('should prefill form fields in edit mode', () => {
    render(<PetForm pet={samplePet} onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    expect(screen.getByText('Editar Pet')).toBeDefined();
    expect((screen.getByLabelText('Nome do Pet') as HTMLInputElement).value).toBe('Rex');
    expect((screen.getByLabelText('Espécie') as HTMLSelectElement).value).toBe('DOG');
    expect((screen.getByLabelText('Raça') as HTMLInputElement).value).toBe('Labrador');
    expect((screen.getByLabelText('Sexo') as HTMLSelectElement).value).toBe('MALE');
    expect((screen.getByLabelText('Data de Nascimento') as HTMLInputElement).value).toBe('2023-01-01');
    expect((screen.getByLabelText('Peso (kg)') as HTMLInputElement).value).toBe('25.5');
    expect((screen.getByLabelText('Porte') as HTMLSelectElement).value).toBe('LARGE');
    expect((screen.getByLabelText('O pet é castrado') as HTMLInputElement).checked).toBe(true);
    expect((screen.getByLabelText('Nº do Microchip') as HTMLInputElement).value).toBe('98100');
    expect((screen.getByLabelText('Alergias') as HTMLTextAreaElement).value).toBe('Poeira');
    expect((screen.getByLabelText('Observações / Notas de Cuidados') as HTMLTextAreaElement).value).toBe('Muito dócil');
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

  it('should call updatePet and onSuccess when editing and submitting valid data', async () => {
    mockUpdatePet.mockResolvedValue({ ...samplePet, name: 'Rex Updated' });

    render(<PetForm pet={samplePet} onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    const nameInput = screen.getByLabelText('Nome do Pet');
    const submitBtn = screen.getByRole('button', { name: 'Salvar Alterações' });

    fireEvent.change(nameInput, { target: { value: 'Rex Updated' } });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(mockUpdatePet).toHaveBeenCalledWith(
        'pet-123',
        expect.objectContaining({
          name: 'Rex Updated',
          species: 'DOG',
          breed: 'Labrador',
        }),
        undefined
      );
    });

    await waitFor(() => {
      expect(mockOnSuccess).toHaveBeenCalled();
    });
  });
});
