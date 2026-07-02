import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ConsultationForm } from './index';
import { useConsultations } from '../../../application/consultation/useConsultations';
import { useToast } from '../../molecules/Toast';
import { vi, describe, it, expect, beforeEach, Mock } from 'vitest';
import React from 'react';

vi.mock('../../../application/consultation/useConsultations', () => ({
  useConsultations: vi.fn(),
}));

vi.mock('../../molecules/Toast', () => ({
  useToast: vi.fn(),
}));

describe('ConsultationForm Component', () => {
  const petId = 'pet-123';
  const mockAddConsultation = vi.fn();
  const mockUploadAttachments = vi.fn();
  const mockShowToast = vi.fn();
  const mockOnSuccess = vi.fn();
  const mockOnCancel = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();

    (useConsultations as Mock).mockReturnValue({
      addConsultation: mockAddConsultation,
      uploadAttachments: mockUploadAttachments,
      consultations: [],
      loading: false,
      error: null,
    });

    (useToast as Mock).mockReturnValue({
      showToast: mockShowToast,
    });
  });

  it('should render form fields correctly', () => {
    render(<ConsultationForm petId={petId} onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    expect(screen.getByTestId('input-motivo-consulta')).toBeInTheDocument();
    expect(screen.getByTestId('input-data-consulta')).toBeInTheDocument();
    expect(screen.getByTestId('input-vet-consulta')).toBeInTheDocument();
    expect(screen.getByTestId('input-clinica-consulta')).toBeInTheDocument();
    expect(screen.getByTestId('input-peso-consulta')).toBeInTheDocument();
    expect(screen.getByTestId('input-custo-consulta')).toBeInTheDocument();
    expect(screen.getByTestId('input-retorno-consulta')).toBeInTheDocument();
    expect(screen.getByTestId('input-diag-consulta')).toBeInTheDocument();
    expect(screen.getByTestId('input-pres-consulta')).toBeInTheDocument();
    expect(screen.getByTestId('input-notes-consulta')).toBeInTheDocument();
  });

  it('should display validation error when reason is empty', async () => {
    render(<ConsultationForm petId={petId} onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    const submitBtn = screen.getByTestId('btn-salvar-consulta');
    fireEvent.click(submitBtn);

    expect(await screen.findByText('O motivo é obrigatório.')).toBeInTheDocument();
    expect(mockAddConsultation).not.toHaveBeenCalled();
  });

  it('should submit form data successfully', async () => {
    mockAddConsultation.mockResolvedValue({ id: 'c-1' });

    render(<ConsultationForm petId={petId} onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    const reasonInput = screen.getByTestId('input-motivo-consulta');
    const vetInput = screen.getByTestId('input-vet-consulta');
    const submitBtn = screen.getByTestId('btn-salvar-consulta');

    fireEvent.change(reasonInput, { target: { value: 'Rotina Anual' } });
    fireEvent.change(vetInput, { target: { value: 'Dr. House' } });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(mockAddConsultation).toHaveBeenCalledWith(expect.objectContaining({
        reason: 'Rotina Anual',
        veterinarian: 'Dr. House',
      }));
      expect(mockShowToast).toHaveBeenCalledWith('Consulta médica registrada com sucesso!', 'success');
      expect(mockOnSuccess).toHaveBeenCalled();
    });
  });

  it('should call onCancel when cancel button is clicked', () => {
    render(<ConsultationForm petId={petId} onSuccess={mockOnSuccess} onCancel={mockOnCancel} />);

    const cancelBtn = screen.getByTestId('btn-cancelar-consulta');
    fireEvent.click(cancelBtn);

    expect(mockOnCancel).toHaveBeenCalled();
  });
});
