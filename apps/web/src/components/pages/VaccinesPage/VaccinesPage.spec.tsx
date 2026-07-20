import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { VaccinesPage } from './index';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useMedications } from '../../../application/medications/useMedications';
import { useToast } from '../../molecules/Toast';

vi.mock('../../../application/pet/useGetPets', () => ({
  useGetPets: vi.fn(),
}));

vi.mock('../../../application/medications/useMedications', () => ({
  useMedications: vi.fn(),
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

describe('VaccinesPage', () => {
  const mockFetchPets = vi.fn();
  const mockFetchMedications = vi.fn();
  const mockCreateMedication = vi.fn();
  const mockStopMedication = vi.fn();
  const mockShowToast = vi.fn();

  const mockPets = [
    { id: '1', name: 'Rex' },
    { id: '2', name: 'Luna' },
  ];

  const mockMedications = [
    {
      id: 'm1',
      medicationType: 'VACCINE',
      name: 'V10',
      status: 'ACTIVE',
      startDate: '2023-01-01',
      endDate: '2024-01-01',
    },
    {
      id: 'm2',
      medicationType: 'DEWORMER',
      name: 'Drontal',
      status: 'ACTIVE',
      startDate: '2023-06-01',
    }
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({ showToast: mockShowToast });
    (useGetPets as any).mockReturnValue({
      pets: mockPets,
      fetchPets: mockFetchPets,
    });
    (useMedications as any).mockReturnValue({
      medications: mockMedications,
      fetchMedications: mockFetchMedications,
      createMedication: mockCreateMedication,
      stopMedication: mockStopMedication,
    });
    window.confirm = vi.fn(() => true);
  });

  it('should render page with pets and records', () => {
    render(<MemoryRouter><VaccinesPage /></MemoryRouter>);

    expect(screen.getByText('Vacinas e Vermífugos')).toBeDefined();
    expect(mockFetchPets).toHaveBeenCalled();
    
    // Select has pets
    expect(screen.getByDisplayValue('Rex')).toBeDefined();
    
    // Renders records
    expect(screen.getByText(/V10/i)).toBeDefined();
    expect(screen.getByText(/Drontal/i)).toBeDefined();
  });

  it('should allow creating a new record', async () => {
    mockCreateMedication.mockResolvedValueOnce(true);

    render(<MemoryRouter><VaccinesPage /></MemoryRouter>);

    // Open form
    fireEvent.click(screen.getByLabelText('Adicionar registro'));
    expect(screen.getByText('Novo Registro')).toBeDefined();

    // Fill form
    fireEvent.change(screen.getByLabelText(/Nome/i), { target: { value: 'Nova Vacina' } });
    fireEvent.click(screen.getByRole('button', { name: /Confirmar/i }));

    await waitFor(() => {
      expect(mockCreateMedication).toHaveBeenCalled();
      expect(mockShowToast).toHaveBeenCalledWith(expect.stringContaining('sucesso'), 'success');
    });
  });

  it('should require name to create record', async () => {
    render(<MemoryRouter><VaccinesPage /></MemoryRouter>);

    fireEvent.click(screen.getByLabelText('Adicionar registro'));
    fireEvent.click(screen.getByRole('button', { name: /Confirmar/i }));

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Por favor, preencha o nome.', 'error');
      expect(mockCreateMedication).not.toHaveBeenCalled();
    });
  });

  it('should allow deleting a record', async () => {
    mockStopMedication.mockResolvedValueOnce(true);

    render(<MemoryRouter><VaccinesPage /></MemoryRouter>);

    const deleteButtons = screen.getAllByText('delete'); // icon text
    fireEvent.click(deleteButtons[0]);

    expect(window.confirm).toHaveBeenCalled();
    await waitFor(() => {
      expect(mockStopMedication).toHaveBeenCalledWith('m2');
      expect(mockShowToast).toHaveBeenCalledWith('Registro excluído.', 'info');
    });
  });

  it('should change selected pet', () => {
    render(<MemoryRouter><VaccinesPage /></MemoryRouter>);

    const select = screen.getByLabelText('Selecione o Pet:');
    fireEvent.change(select, { target: { value: '2' } });

    expect(mockFetchMedications).toHaveBeenCalled();
  });

  it('should render empty list', () => {
    (useMedications as any).mockReturnValue({
      medications: [],
      fetchMedications: mockFetchMedications,
      createMedication: mockCreateMedication,
      stopMedication: mockStopMedication,
    });

    render(<MemoryRouter><VaccinesPage /></MemoryRouter>);
    expect(screen.getByText('Nenhum registro encontrado para este pet.')).toBeDefined();
  });
});
