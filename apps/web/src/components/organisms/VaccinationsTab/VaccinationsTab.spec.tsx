import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { VaccinationsTab } from './index';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

const mockAddVaccination = vi.fn();
const mockUpdateVaccination = vi.fn();
const mockUploadProof = vi.fn();
const mockFetchVaccinations = vi.fn();
const mockFetchSuggestions = vi.fn();

vi.mock('../../../application/vaccination/useVaccinations', () => ({
  useVaccinations: () => ({
    vaccinations: [
      {
        id: 'v-1',
        vaccineName: 'Antirrábica',
        dateAdministered: '2026-01-01',
        nextDoseDate: '2027-01-01',
        veterinarian: 'Dr. Silva',
        clinic: 'Vet Care',
        batchNumber: 'L-123',
        manufacturer: 'Pfizer',
        notes: 'Dose anual em dia',
        proofUrl: null,
      }
    ],
    loading: false,
    error: null,
    fetchVaccinations: mockFetchVaccinations,
    addVaccination: mockAddVaccination,
    updateVaccination: mockUpdateVaccination,
    uploadProof: mockUploadProof,
  }),
}));

vi.mock('../../../application/vaccination/useVaccineSuggestions', () => ({
  useVaccineSuggestions: () => ({
    suggestions: ['Antirrábica', 'V10', 'Gripe'],
    loading: false,
    error: null,
    fetchSuggestions: mockFetchSuggestions,
  }),
}));

describe('VaccinationsTab', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    if (typeof window.URL.createObjectURL === 'undefined') {
      window.URL.createObjectURL = vi.fn().mockReturnValue('mock-url');
    }
  });

  it('should render vaccinations list correctly', () => {
    render(<VaccinationsTab petId="pet-123" species="DOG" />);

    expect(screen.getByText('Histórico de Vacinas')).toBeDefined();
    expect(screen.getByText('Nova Vacina')).toBeDefined();
    expect(screen.getByText('Antirrábica')).toBeDefined();
  });

  it('should allow opening new vaccine modal, filling form, and submitting', async () => {
    mockAddVaccination.mockResolvedValue(true);

    render(<VaccinationsTab petId="pet-123" species="DOG" />);

    const newBtn = screen.getByRole('button', { name: /nova vacina/i });
    fireEvent.click(newBtn);

    // Modal should be open
    expect(screen.getByText('Registrar Nova Vacina')).toBeDefined();

    const nameInput = screen.getByPlaceholderText(/Ex: Antirrábica/i);
    fireEvent.change(nameInput, { target: { value: 'Gripe' } });

    const vetInput = screen.getByPlaceholderText(/Nome do profissional/i);
    fireEvent.change(vetInput, { target: { value: 'Dr. Marcos' } });

    const submitBtn = screen.getByRole('button', { name: /salvar vacina/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(mockAddVaccination).toHaveBeenCalledWith({
        vaccineName: 'Gripe',
        dateAdministered: expect.any(String),
        nextDoseDate: '',
        veterinarian: 'Dr. Marcos',
        clinic: '',
        batchNumber: '',
        manufacturer: '',
        notes: '',
        reminderActive: true,
      });
    });
  });

  it('should allow clicking a vaccine to edit and uploading a proof file', async () => {
    mockUpdateVaccination.mockResolvedValue(true);
    mockUploadProof.mockResolvedValue(true);

    render(<VaccinationsTab petId="pet-123" species="DOG" />);

    // Click on vaccine card in list to edit
    const vaccineCard = screen.getByText('Antirrábica');
    fireEvent.click(vaccineCard);

    // Modal details should be open
    expect(screen.getByText('Detalhes da Vacina')).toBeDefined();
    expect(screen.getByText('Comprovante de Vacinação')).toBeDefined();

    const file = new File(['proof'], 'proof.jpg', { type: 'image/jpeg' });
    
    // Fallback: search for input type file
    const inputEl = document.querySelector('input[type="file"]') as HTMLInputElement;
    expect(inputEl).toBeDefined();

    fireEvent.change(inputEl, { target: { files: [file] } });

    await waitFor(() => {
      expect(mockUploadProof).toHaveBeenCalledWith('v-1', file);
    });
  });
});
