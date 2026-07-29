import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { VetCard } from './index';

describe('VetCard', () => {
  const mockVet = {
    id: 'vet-123',
    fullName: 'Dr. John Doe',
    crmvNumber: '12345',
    crmvState: 'SP',
    specialties: ['Cardiology', 'Surgery', 'Dermatology', 'Oncology'],
    availabilityStatus: 'AVAILABLE',
    emergencyOnDuty: true,
    profilePhotoUrl: 'http://example.com/photo.jpg',
  };

  it('renders correctly with all details', () => {
    render(<MemoryRouter><VetCard veterinarian={mockVet as any} /></MemoryRouter>);
    
    expect(screen.getByText('Dr. John Doe')).toBeInTheDocument();
    expect(screen.getByText('CRMV-SP 12345')).toBeInTheDocument();
    
    // Check specialties
    expect(screen.getByText('Cardiology')).toBeInTheDocument();
    expect(screen.getByText('Surgery')).toBeInTheDocument();
    expect(screen.getByText('Dermatology')).toBeInTheDocument();
    expect(screen.getByText('+1')).toBeInTheDocument(); // 4 specialties, only 3 shown
    
    // Check status
    expect(screen.getByText('Disponível')).toBeInTheDocument();
    expect(screen.getByText('Plantão Emergência')).toBeInTheDocument();
    
    // Check image
    const image = screen.getByAltText('Dr. John Doe');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'http://example.com/photo.jpg');
  });

  it('renders fallback photo and unavailable status', () => {
    const vetWithoutPhoto = { 
      ...mockVet, 
      profilePhotoUrl: undefined,
      availabilityStatus: 'UNAVAILABLE',
      emergencyOnDuty: false,
      specialties: ['Cardiology']
    };
    
    const { container } = render(<MemoryRouter><VetCard veterinarian={vetWithoutPhoto as any} /></MemoryRouter>);
    
    expect(screen.getByText('Dr. John Doe')).toBeInTheDocument();
    expect(screen.queryByAltText('Dr. John Doe')).not.toBeInTheDocument();
    expect(container.querySelector('.vet-card__photo-placeholder')).toBeInTheDocument();
    
    expect(screen.getByText('Indisponível')).toBeInTheDocument();
    expect(screen.queryByText('Plantão Emergência')).not.toBeInTheDocument();
    
    expect(screen.queryByText('+1')).not.toBeInTheDocument();
  });
});
