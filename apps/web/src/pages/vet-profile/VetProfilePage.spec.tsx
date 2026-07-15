import { render, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { VetProfilePage } from './VetProfilePage';

const mockUpdateAvailability = vi.fn();
const mockUpdateEmergency = vi.fn();

vi.mock('../../application/veterinarian/useVeterinarianProfile', () => ({
  useVeterinarianProfile: () => ({
    getMyProfile: vi.fn().mockResolvedValue({
      fullName: 'Dr. Teste',
      crmvState: 'SP',
      crmvNumber: '12345',
      availabilityStatus: 'AVAILABLE',
      emergencyOnDuty: false
    }),
    updateAvailability: mockUpdateAvailability,
    updateEmergency: mockUpdateEmergency,
    loading: false,
    error: null,
  })
}));

describe('VetProfilePage', () => {
  it('should render and interact successfully', async () => {
    const { getByText, findByText } = render(<VetProfilePage />);
    
    // Espera carregar o perfil
    const title = await findByText('Dashboard do Veterinário');
    expect(title).toBeDefined();

    // Testa cliques para aumentar cobertura
    const availabilityBtn = getByText('Mudar Disponibilidade (Atual: AVAILABLE)');
    fireEvent.click(availabilityBtn);
    await waitFor(() => expect(mockUpdateAvailability).toHaveBeenCalledWith('UNAVAILABLE'));

    const emergencyBtn = getByText('Mudar Plantão (Atual: Não)');
    fireEvent.click(emergencyBtn);
    await waitFor(() => expect(mockUpdateEmergency).toHaveBeenCalledWith(true));
  });
});
