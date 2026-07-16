import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { GroomingPageContent } from './index';
import { useGrooming } from '../../../application/grooming/useGrooming';
import { petApi } from '../../../infrastructure/http/pet.api';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import React from 'react';

import { useGetPets } from '../../../application/pet/useGetPets';

// Mock dependencies
vi.mock('../../../application/grooming/useGrooming', () => ({
  useGrooming: vi.fn(),
}));

vi.mock('../../../infrastructure/http/pet.api', () => ({
  petApi: {
    getById: vi.fn(),
  },
}));

vi.mock('../../../application/pet/useGetPets', () => ({
  useGetPets: vi.fn(),
}));

vi.mock('../../molecules/Toast', () => ({
  useToast: () => ({
    showToast: vi.fn(),
  }),
}));

describe('GroomingPageContent Component', () => {
  const mockGroomings = [
    {
      id: 'g-1',
      petId: 'pet-123',
      type: 'BATH' as const,
      date: '2026-07-08',
      provider: 'Pet Shop Spa',
      cost: 70,
      frequencyDays: 15,
      nextDate: '2026-07-23',
      photos: [],
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();

    (useGrooming as any).mockReturnValue({
      groomings: mockGroomings,
      loading: false,
      error: null,
      fetchGroomings: vi.fn(),
      addGrooming: vi.fn(),
      updateGrooming: vi.fn(),
      uploadPhoto: vi.fn(),
    });

    (petApi.getById as any).mockResolvedValue({
      data: { data: { id: 'pet-123', name: 'Bolinha' } },
    });

    (useGetPets as any).mockReturnValue({
      pets: [{ id: 'pet-123', name: 'Bolinha' }],
      fetchPets: vi.fn(),
    });
  });

  it('should render header and history list correctly', async () => {
    render(
      <MemoryRouter initialEntries={['/pets/pet-123/grooming']}>
        <Routes>
          <Route path="/pets/:petId/grooming" element={<GroomingPageContent />} />
        </Routes>
      </MemoryRouter>
    );

    expect(await screen.findByText('Banho & Tosa')).toBeDefined();
    expect(screen.getByText('Pet Shop Spa')).toBeDefined();
    expect(screen.getByText('R$ 70.00')).toBeDefined();
    expect(screen.getByTestId('grooming-card')).toBeDefined();
  });

  it('should open modal when clicking "Novo Banho/Tosa"', async () => {
    render(
      <MemoryRouter initialEntries={['/pets/pet-123/grooming']}>
        <Routes>
          <Route path="/pets/:petId/grooming" element={<GroomingPageContent />} />
        </Routes>
      </MemoryRouter>
    );

    const openBtn = screen.getByTestId('btn-open-grooming-form');
    fireEvent.click(openBtn);

    expect(screen.getAllByText('Registrar Banho e Tosa').length).toBeGreaterThan(0);
  });

  it('should render loading and error states correctly', () => {
    (useGrooming as any).mockReturnValue({
      groomings: [],
      loading: true,
      error: 'Erro de conexão',
      fetchGroomings: vi.fn(),
      addGrooming: vi.fn(),
      updateGrooming: vi.fn(),
      uploadPhoto: vi.fn(),
    });

    render(
      <MemoryRouter initialEntries={['/pets/pet-123/grooming']}>
        <Routes>
          <Route path="/pets/:petId/grooming" element={<GroomingPageContent />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Carregando histórico...')).toBeDefined();
    expect(screen.getByText('Erro de conexão')).toBeDefined();
  });

  it('should display delay badge if grooming is overdue', () => {
    const overdueGroomings = [
      {
        id: 'g-1',
        petId: 'pet-123',
        type: 'BATH' as const,
        date: '2026-06-01',
        provider: 'Pet Shop Spa',
        cost: 70,
        frequencyDays: 5,
        nextDate: '2026-06-06',
        photos: [],
      },
    ];

    (useGrooming as any).mockReturnValue({
      groomings: overdueGroomings,
      loading: false,
      error: null,
      fetchGroomings: vi.fn(),
      addGrooming: vi.fn(),
      updateGrooming: vi.fn(),
      uploadPhoto: vi.fn(),
    });

    render(
      <MemoryRouter initialEntries={['/pets/pet-123/grooming']}>
        <Routes>
          <Route path="/pets/:petId/grooming" element={<GroomingPageContent />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText(/Atrasado há/)).toBeDefined();
  });

  it('should render BeforeAfterViewer if both photos exist', () => {
    const photoGroomings = [
      {
        id: 'g-1',
        petId: 'pet-123',
        type: 'BATH' as const,
        date: '2026-07-08',
        provider: 'Pet Shop Spa',
        cost: 70,
        frequencyDays: 15,
        nextDate: '2026-07-23',
        photos: ['before.jpg', 'after.jpg'],
      },
    ];

    (useGrooming as any).mockReturnValue({
      groomings: photoGroomings,
      loading: false,
      error: null,
      fetchGroomings: vi.fn(),
      addGrooming: vi.fn(),
      updateGrooming: vi.fn(),
      uploadPhoto: vi.fn(),
    });

    render(
      <MemoryRouter initialEntries={['/pets/pet-123/grooming']}>
        <Routes>
          <Route path="/pets/:petId/grooming" element={<GroomingPageContent />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByTestId('before-after-slider')).toBeDefined();
  });

  it('should render single image if only one photo is present', () => {
    const singlePhotoGroomings = [
      {
        id: 'g-1',
        petId: 'pet-123',
        type: 'BATH' as const,
        date: '2026-07-08',
        provider: 'Pet Shop Spa',
        cost: 70,
        frequencyDays: 15,
        nextDate: '2026-07-23',
        photos: ['before.jpg'],
      },
    ];

    (useGrooming as any).mockReturnValue({
      groomings: singlePhotoGroomings,
      loading: false,
      error: null,
      fetchGroomings: vi.fn(),
      addGrooming: vi.fn(),
      updateGrooming: vi.fn(),
      uploadPhoto: vi.fn(),
    });

    render(
      <MemoryRouter initialEntries={['/pets/pet-123/grooming']}>
        <Routes>
          <Route path="/pets/:petId/grooming" element={<GroomingPageContent />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByAltText('Foto do pet')).toBeDefined();
  });

  it('should successfully submit the form to add a new grooming', async () => {
    const mockAddGrooming = vi.fn().mockResolvedValue({
      id: 'g-2',
      petId: 'pet-123',
      type: 'BATH',
      date: '2026-07-08',
      photos: [],
    });

    (useGrooming as any).mockReturnValue({
      groomings: mockGroomings,
      loading: false,
      error: null,
      fetchGroomings: vi.fn(),
      addGrooming: mockAddGrooming,
      updateGrooming: vi.fn(),
      uploadPhoto: vi.fn(),
    });

    render(
      <MemoryRouter initialEntries={['/pets/pet-123/grooming']}>
        <Routes>
          <Route path="/pets/:petId/grooming" element={<GroomingPageContent />} />
        </Routes>
      </MemoryRouter>
    );

    const openBtn = screen.getByTestId('btn-open-grooming-form');
    fireEvent.click(openBtn);

    const dateInput = screen.getByTestId('input-date') as HTMLInputElement;
    fireEvent.change(dateInput, { target: { value: '2026-07-08' } });

    const submitBtn = screen.getByTestId('btn-submit');
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(mockAddGrooming).toHaveBeenCalled();
    });
  });

  it('should upload photos if provided on success', async () => {
    const mockAddGrooming = vi.fn().mockResolvedValue({
      id: 'g-2',
      petId: 'pet-123',
      type: 'BATH',
      date: '2026-07-08',
      photos: [],
    });
    const mockUploadPhoto = vi.fn().mockResolvedValue(true);

    (useGrooming as any).mockReturnValue({
      groomings: mockGroomings,
      loading: false,
      error: null,
      fetchGroomings: vi.fn(),
      addGrooming: mockAddGrooming,
      updateGrooming: vi.fn(),
      uploadPhoto: mockUploadPhoto,
    });

    render(
      <MemoryRouter initialEntries={['/pets/pet-123/grooming']}>
        <Routes>
          <Route path="/pets/:petId/grooming" element={<GroomingPageContent />} />
        </Routes>
      </MemoryRouter>
    );

    const openBtn = screen.getByTestId('btn-open-grooming-form');
    fireEvent.click(openBtn);

    const beforeInput = screen.getByTestId('file-before');
    const afterInput = screen.getByTestId('file-after');
    const file = new File(['dummy content'], 'test.png', { type: 'image/png' });
    
    const originalCreateObjectURL = URL.createObjectURL;
    URL.createObjectURL = vi.fn().mockReturnValue('mock-url');

    fireEvent.change(beforeInput, { target: { files: [file] } });
    fireEvent.change(afterInput, { target: { files: [file] } });

    const submitBtn = screen.getByTestId('btn-submit');
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(mockUploadPhoto).toHaveBeenCalledTimes(2);
    });

    URL.createObjectURL = originalCreateObjectURL;
  });
});
