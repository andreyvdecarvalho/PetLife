import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { RoutinePage } from './index';
import { BrowserRouter } from 'react-router-dom';
import * as getPetsHooks from '../../../application/pet/useGetPets';
import * as routineHooks from '../../../application/routine/useRoutineActivities';
import * as medicationsHooks from '../../../application/medications/useMedications';
import * as consultationsHooks from '../../../application/consultation/useConsultations';
import * as groomingHooks from '../../../application/grooming/useGrooming';
import * as vaccinationsHooks from '../../../application/vaccination/useVaccinations';
import { ToastProvider } from '../../molecules/Toast';

const mockPets = [{ id: 'pet-1', name: 'Rex' }];
const fetchPets = vi.fn();
const fetchActivities = vi.fn();
const updateActivityStatus = vi.fn().mockResolvedValue(true);
const addActivity = vi.fn().mockResolvedValue(true);
const fetchMedications = vi.fn();
const fetchConsultations = vi.fn();
const addConsultation = vi.fn().mockResolvedValue(true);
const fetchGroomings = vi.fn();
const addGrooming = vi.fn().mockResolvedValue(true);
const fetchVaccinations = vi.fn();

vi.spyOn(getPetsHooks, 'useGetPets').mockReturnValue({ pets: mockPets as any, loading: false, error: null, fetchPets });
vi.spyOn(routineHooks, 'useRoutineActivities').mockReturnValue({ activities: [], loading: false, error: null, fetchActivities, updateStatus: updateActivityStatus, addActivity });
vi.spyOn(medicationsHooks, 'useMedications').mockReturnValue({ medications: [], loading: false, error: null, fetchMedications, addMedication: vi.fn(), toggleMedicationDose: vi.fn(), endMedication: vi.fn() });
vi.spyOn(consultationsHooks, 'useConsultations').mockReturnValue({ consultations: [], loading: false, error: null, fetchConsultations, addConsultation });
vi.spyOn(groomingHooks, 'useGrooming').mockReturnValue({ groomings: [], loading: false, error: null, fetchGroomings, addGrooming });
vi.spyOn(vaccinationsHooks, 'useVaccinations').mockReturnValue({ vaccinations: [], loading: false, error: null, fetchVaccinations, addVaccination: vi.fn() });

describe('RoutinePage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderComponent = () => {
    return render(
      <BrowserRouter>
        <ToastProvider>
          <RoutinePage />
        </ToastProvider>
      </BrowserRouter>
    );
  };

  it('renders title and calls fetch methods on mount', async () => {
    renderComponent();
    expect(screen.getByText('Minha Rotina')).toBeInTheDocument();
    
    await waitFor(() => {
      expect(fetchPets).toHaveBeenCalled();
      expect(fetchActivities).toHaveBeenCalled();
      expect(fetchMedications).toHaveBeenCalled();
      expect(fetchConsultations).toHaveBeenCalled();
      expect(fetchGroomings).toHaveBeenCalled();
      expect(fetchVaccinations).toHaveBeenCalled();
    });
  });

  it('can open and submit the activity modal', async () => {
    renderComponent();
    const addActivityBtn = screen.getByTestId('btn-add-activity');
    fireEvent.click(addActivityBtn);

    expect(await screen.findByRole('heading', { name: 'Planejar Atividade' })).toBeInTheDocument();
    
    fireEvent.change(screen.getByTestId('input-act-title'), { target: { value: 'Passeio matinal' } });
    fireEvent.change(screen.getByTestId('input-act-time'), { target: { value: '07:30' } });
    fireEvent.change(screen.getByTestId('input-act-desc'), { target: { value: 'Passeio no parque' } });
    
    const saveBtn = screen.getByText('Salvar');
    fireEvent.click(saveBtn);
    
    await waitFor(() => {
      expect(addActivity).toHaveBeenCalledWith(expect.objectContaining({
        title: 'Passeio matinal',
        description: 'Passeio no parque'
      }));
    });
  });

  it('can open and submit the appointment modal', async () => {
    renderComponent();
    const addAppointmentBtn = screen.getByTestId('btn-add-appointment');
    fireEvent.click(addAppointmentBtn);

    expect(await screen.findByRole('heading', { name: 'Agendar Retorno' })).toBeInTheDocument();
    
    fireEvent.change(screen.getByTestId('input-app-vet'), { target: { value: 'Dr. João' } });
    fireEvent.change(screen.getByTestId('input-app-time'), { target: { value: '14:00' } });
    
    const saveBtn = screen.getByText('Salvar');
    fireEvent.click(saveBtn);
    
    await waitFor(() => {
      expect(addConsultation).toHaveBeenCalledWith(expect.objectContaining({
        veterinarian: 'Dr. João'
      }));
    });
  });

  it('can open and submit the grooming modal', async () => {
    renderComponent();
    const addGroomingBtn = screen.getByTestId('btn-add-grooming');
    fireEvent.click(addGroomingBtn);

    expect(await screen.findByRole('heading', { name: 'Agendar Banho e Tosa' })).toBeInTheDocument();
    
    fireEvent.change(screen.getByTestId('input-groom-provider'), { target: { value: 'Pet Feliz' } });
    fireEvent.change(screen.getByTestId('input-groom-time'), { target: { value: '09:00' } });
    
    const saveBtn = screen.getByText('Salvar');
    fireEvent.click(saveBtn);
    
    await waitFor(() => {
      expect(addGrooming).toHaveBeenCalledWith(expect.objectContaining({
        provider: 'Pet Feliz',
      }));
    });
  });
});
