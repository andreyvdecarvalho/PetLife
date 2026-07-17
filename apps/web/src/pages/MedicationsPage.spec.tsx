import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MedicationsPage } from './MedicationsPage';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../components/molecules/Toast';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';

const mockCreateMedication = vi.fn();
const mockUpdateAdministration = vi.fn();
const mockStopMedication = vi.fn();
const mockFetchMedications = vi.fn();
const mockFetchAdherence = vi.fn();

vi.mock('../contexts/AuthContext');
vi.mock('../components/molecules/Toast');

vi.mock('../application/pet/useGetPets', () => ({
  useGetPets: () => ({
    pets: [
      { id: 'pet-123', name: 'Max', species: 'DOG', breed: 'Golden Retriever', photoUrl: '', status: 'ACTIVE', userId: '1' }
    ],
    isLoading: false,
    error: null,
    fetchPets: vi.fn(),
  }),
}));

vi.mock('../application/medications/useMedications', () => ({
  useMedications: () => ({
    medications: [
      {
        id: 'med-1',
        name: 'Antibiótico Amoxicilina',
        dosage: '1 comprimido',
        frequency: 'DAILY',
        status: 'ACTIVE',
        startDate: '2026-07-07',
        timesOfDay: ['08:00'],
        administrations: [
          {
            id: 'adm-1',
            medicationId: 'med-1',
            medicationName: 'Antibiótico Amoxicilina',
            scheduledTime: '2026-07-08T08:00:00Z',
            status: 'PENDING',
          },
          {
            id: 'adm-2',
            medicationId: 'med-1',
            medicationName: 'Antibiótico Amoxicilina',
            scheduledTime: '2026-07-07T08:00:00Z',
            status: 'TAKEN',
          }
        ]
      }
    ],
    adherence: {
      adherenceRate: 50.0,
      totalDoses: 2,
      takenDoses: 1,
      skippedDoses: 0,
      lateDoses: 0,
      pendingDoses: 1,
    },
    loading: false,
    fetchMedications: mockFetchMedications,
    fetchAdherence: mockFetchAdherence,
    createMedication: mockCreateMedication,
    updateAdministration: mockUpdateAdministration,
    stopMedication: mockStopMedication,
  }),
}));

describe('MedicationsPage', () => {
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
    (useAuth as any).mockReturnValue({
      user: {
        id: '1',
        name: 'Camila',
        email: 'camila@example.com',
        plan: 'FREE',
        emailVerified: true
      },
      logout: vi.fn()
    });
  });

  it('should render correctly in real mode with treatments list and history', () => {
    render(
      <MemoryRouter initialEntries={['/medications']}>
        <Routes>
          <Route path="/medications" element={<MedicationsPage />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Controle de Medicamentos')).toBeDefined();
    expect(screen.getByText('Selecione o Pet:')).toBeDefined();
    expect(screen.getByText('Max')).toBeDefined();
    expect(screen.getByText('Aderência Geral ao Tratamento')).toBeDefined();
    expect(screen.getByText('1 de 2 doses administradas')).toBeDefined();
    expect(screen.getByText('50%')).toBeDefined();
    expect(screen.getAllByText('Antibiótico Amoxicilina').length).toBeGreaterThan(0);
    expect(screen.getByText('1 Ativos')).toBeDefined();
  });

  it('should allow marking a pending dose as taken', async () => {
    mockUpdateAdministration.mockResolvedValue(true);

    render(
      <MemoryRouter initialEntries={['/medications']}>
        <Routes>
          <Route path="/medications" element={<MedicationsPage />} />
        </Routes>
      </MemoryRouter>
    );

    const takeButton = screen.getByRole('button', { name: /tomar/i });
    expect(takeButton).toBeDefined();

    fireEvent.click(takeButton);

    await waitFor(() => {
      expect(mockUpdateAdministration).toHaveBeenCalledWith('adm-1', { status: 'TAKEN' });
      expect(mockShowToast).toHaveBeenCalledWith('Dose registrada com sucesso! ✨', 'success');
    });
  });

  it('should allow skipping a pending dose with reason', async () => {
    mockUpdateAdministration.mockResolvedValue(true);

    render(
      <MemoryRouter initialEntries={['/medications']}>
        <Routes>
          <Route path="/medications" element={<MedicationsPage />} />
        </Routes>
      </MemoryRouter>
    );

    const skipButton = screen.getByRole('button', { name: /pular/i });
    expect(skipButton).toBeDefined();

    fireEvent.click(skipButton);

    // Skip dose modal should open
    expect(screen.getByText('Justificar Dose Pulada')).toBeDefined();

    const textarea = screen.getByPlaceholderText(/ex: pet vomitou/i);
    fireEvent.change(textarea, { target: { value: 'Pet rejeitou comprimido' } });

    const confirmButton = screen.getByRole('button', { name: /confirmar/i });
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(mockUpdateAdministration).toHaveBeenCalledWith('adm-1', {
        status: 'SKIPPED',
        skippedReason: 'Pet rejeitou comprimido'
      });
      expect(mockShowToast).toHaveBeenCalledWith('Dose registrada como pulada.', 'info');
    });
  });

  it('should allow stopping a treatment manually', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    mockStopMedication.mockResolvedValue(true);

    render(
      <MemoryRouter initialEntries={['/medications']}>
        <Routes>
          <Route path="/medications" element={<MedicationsPage />} />
        </Routes>
      </MemoryRouter>
    );

    const stopButton = screen.getByRole('button', { name: /parar/i });
    expect(stopButton).toBeDefined();

    fireEvent.click(stopButton);

    await waitFor(() => {
      expect(window.confirm).toHaveBeenCalledWith('Tem certeza que deseja interromper este tratamento manualmente?');
      expect(mockStopMedication).toHaveBeenCalledWith('med-1');
      expect(mockShowToast).toHaveBeenCalledWith('Tratamento interrompido.', 'info');
    });
  });

  it('should allow registering a new treatment via modal', async () => {
    mockCreateMedication.mockResolvedValue(true);

    render(
      <MemoryRouter initialEntries={['/medications']}>
        <Routes>
          <Route path="/medications" element={<MedicationsPage />} />
        </Routes>
      </MemoryRouter>
    );

    const openModalBtn = screen.getByRole('button', { name: /adicionar medicamento/i });
    fireEvent.click(openModalBtn);

    expect(screen.getByText('Cadastrar Tratamento')).toBeDefined();

    const nameInput = screen.getByLabelText(/nome do medicamento/i);
    fireEvent.change(nameInput, { target: { value: 'Vitamina C' } });

    const dosageInput = screen.getByLabelText(/dosagem/i);
    fireEvent.change(dosageInput, { target: { value: '5ml' } });

    const frequencySelect = screen.getByLabelText(/frequência/i);
    fireEvent.change(frequencySelect, { target: { value: 'DAILY' } });

    const startDateInput = screen.getByLabelText(/data de início/i);
    fireEvent.change(startDateInput, { target: { value: '2026-07-20' } });

    const durationInput = screen.getByLabelText(/duração do tratamento \(dias\)/i);
    fireEvent.change(durationInput, { target: { value: '10' } });

    const submitButton = screen.getByRole('button', { name: /confirmar/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockCreateMedication).toHaveBeenCalledWith({
        name: 'Vitamina C',
        dosage: '5ml',
        frequency: 'DAILY',
        medicationType: 'MEDICINE',
        customFrequencyHours: undefined,
        startDate: '2026-07-20',
        endDate: '2026-07-30',
        timesOfDay: ['08:00']
      });
      expect(mockShowToast).toHaveBeenCalledWith('Tratamento cadastrado com sucesso! ✨', 'success');
    });
  });
});
