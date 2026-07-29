import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { vi } from 'vitest';
import { PetCard } from './index';

describe('PetCard', () => {
  const mockPet = {
    id: '1',
    name: 'Rex',
    species: 'DOG',
    gender: 'MALE',
    photoUrl: 'http://example.com/rex.jpg',
  };

  it('renders correctly with pet details', () => {
    const handleToggle = vi.fn();
    render(<PetCard pet={mockPet as any} isActive={false} onClick={vi.fn()} onToggleStatus={handleToggle} />);
    
    expect(screen.getByText('Rex')).toBeInTheDocument();
    const image = screen.getByAltText('Rex');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'http://example.com/rex.jpg');
  });

  it('renders fallback icon when photoUrl is missing', () => {
    const petWithoutPhoto = { ...mockPet, photoUrl: undefined };
    const { container } = render(
      <PetCard pet={petWithoutPhoto as any} isActive={false} onClick={vi.fn()} onToggleStatus={vi.fn()} />
    );
    
    expect(screen.getByText('Rex')).toBeInTheDocument();
    expect(screen.queryByAltText('Rex')).not.toBeInTheDocument();
    expect(screen.getByText('pets')).toBeInTheDocument(); // material symbols icon
  });

  it('applies active class and renders dot when isActive is true', () => {
    const { container } = render(
      <PetCard pet={mockPet as any} isActive={true} onClick={vi.fn()} onToggleStatus={vi.fn()} />
    );
    
    const card = container.querySelector('.molecule-pet-card');
    expect(card).toHaveClass('active');
    expect(container.querySelector('.molecule-pet-card__status-dot')).toBeInTheDocument();
  });

  it('calls onClick when card is clicked', () => {
    const handleClick = vi.fn();
    render(<PetCard pet={mockPet as any} isActive={false} onClick={handleClick} onToggleStatus={vi.fn()} />);
    
    fireEvent.click(screen.getByRole('button', { name: /selecionar pet rex/i }));
    expect(handleClick).toHaveBeenCalledWith(mockPet);
    expect(handleClick).toHaveBeenCalledTimes(1);
  });
});
