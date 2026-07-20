import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { OnboardingPage } from './OnboardingPage';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import React from 'react';
import { useCreatePet } from '../application/pet/useCreatePet';

vi.mock('../application/pet/useCreatePet', () => ({
  useCreatePet: vi.fn(),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<any>('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('OnboardingPage', () => {
  const mockCreatePet = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useCreatePet as any).mockReturnValue({
      createPet: mockCreatePet,
      loading: false,
      error: null,
    });
    
    window.URL.createObjectURL = vi.fn(() => 'blob:mock-url');
  });

  it('should complete the entire onboarding flow successfully', async () => {
    mockCreatePet.mockResolvedValueOnce(true);

    render(
      <MemoryRouter initialEntries={['/onboarding']}>
        <Routes>
          <Route path="/onboarding" element={<OnboardingPage />} />
        </Routes>
      </MemoryRouter>
    );

    // Step 1: Welcome
    expect(screen.getByText('Vamos conhecer o seu melhor amigo?')).toBeDefined();
    fireEvent.click(screen.getByRole('button', { name: /começar/i }));

    // Step 2: Basic Info
    await waitFor(() => expect(screen.getByText('Dados Básicos')).toBeDefined());
    
    // Fill Name
    const nameInput = screen.getByLabelText(/Nome \*/i);
    fireEvent.change(nameInput, { target: { value: 'Rex' } });
    
    // Fill Breed
    const breedInput = screen.getByLabelText(/Raça/i);
    fireEvent.change(breedInput, { target: { value: 'Poodle' } });
    
    // Select Species
    const speciesSelect = screen.getByLabelText(/Espécie \*/i);
    fireEvent.change(speciesSelect, { target: { value: 'DOG' } });

    fireEvent.click(screen.getByRole('button', { name: /avançar/i }));

    // Step 3: Health Info
    await waitFor(() => expect(screen.getByText('Saúde e Perfil')).toBeDefined());
    
    // Fill Weight
    const weightInput = screen.getByLabelText(/Peso \(kg\)/i);
    fireEvent.change(weightInput, { target: { value: '5.5' } });
    
    // Fill Birth Date
    const birthInput = screen.getByLabelText(/Data de Nascimento/i);
    fireEvent.change(birthInput, { target: { value: '2020-01-01' } });
    
    // Select Sex
    const sexSelect = screen.getByLabelText(/Gênero/i);
    fireEvent.change(sexSelect, { target: { value: 'MALE' } });

    fireEvent.click(screen.getByRole('button', { name: /avançar/i }));

    // Step 4: Photo
    await waitFor(() => expect(screen.getByText('Foto do Pet')).toBeDefined());
    
    // Upload photo
    const fileInput = screen.getByLabelText(/Escolher foto/i) as HTMLInputElement;
    const file = new File(['dummy content'], 'photo.png', { type: 'image/png' });
    fireEvent.change(fileInput, { target: { files: [file] } });
    
    await waitFor(() => {
      expect(screen.getByAltText('Preview')).toBeDefined();
    });

    // Submit
    fireEvent.click(screen.getByRole('button', { name: /finalizar cadastro/i }));

    await waitFor(() => {
      expect(mockCreatePet).toHaveBeenCalledWith({
        name: 'Rex',
        species: 'DOG',
        breed: 'Poodle',
        sex: 'MALE',
        birthDate: '2020-01-01',
        weightKg: 5.5,
        neutered: false,
      }, file);
    });

    // Step 5: Success
    await waitFor(() => {
      expect(screen.getByText('Pet adicionado com sucesso!')).toBeDefined();
    });

    fireEvent.click(screen.getByRole('button', { name: /ir para o início/i }));
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('should navigate back correctly', async () => {
    render(
      <MemoryRouter initialEntries={['/onboarding']}>
        <Routes>
          <Route path="/onboarding" element={<OnboardingPage />} />
        </Routes>
      </MemoryRouter>
    );

    // Step 1 -> 2
    fireEvent.click(screen.getByRole('button', { name: /começar/i }));
    await waitFor(() => expect(screen.getByText('Dados Básicos')).toBeDefined());

    // Step 2 -> 1
    fireEvent.click(screen.getByLabelText('Voltar'));
    await waitFor(() => expect(screen.getByText('Vamos conhecer o seu melhor amigo?')).toBeDefined());
  });

  it('should handle API errors', async () => {
    mockCreatePet.mockRejectedValueOnce(new Error('API error'));
    (useCreatePet as any).mockReturnValue({
      createPet: mockCreatePet,
      loading: false,
      error: 'Erro ao criar pet',
    });

    render(
      <MemoryRouter initialEntries={['/onboarding']}>
        <Routes>
          <Route path="/onboarding" element={<OnboardingPage />} />
        </Routes>
      </MemoryRouter>
    );

    // Go to step 4
    fireEvent.click(screen.getByRole('button', { name: /começar/i }));
    await waitFor(() => expect(screen.getByText('Dados Básicos')).toBeDefined());
    
    fireEvent.change(screen.getByLabelText(/Nome \*/i), { target: { value: 'Rex' } });
    fireEvent.click(screen.getByRole('button', { name: /avançar/i }));
    
    await waitFor(() => expect(screen.getByText('Saúde e Perfil')).toBeDefined());
    fireEvent.click(screen.getByRole('button', { name: /avançar/i }));

    await waitFor(() => expect(screen.getByText('Foto do Pet')).toBeDefined());
    
    expect(screen.getByText('Erro ao criar pet')).toBeDefined();
    
    // Submit
    fireEvent.click(screen.getByRole('button', { name: /finalizar cadastro/i }));

    await waitFor(() => {
      expect(mockCreatePet).toHaveBeenCalled();
      // Should stay on step 4
      expect(screen.getByText('Foto do Pet')).toBeDefined();
    });
  });
});
