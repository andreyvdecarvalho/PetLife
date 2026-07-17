import React from 'react';
import type { Pet } from '../../../domain/pet/Pet';
import './styles.css';

interface PetCardProps {
  pet: Pet;
  isActive: boolean;
  onClick: (pet: Pet) => void;
  onToggleStatus: (pet: Pet) => void;
}

export const PetCard: React.FC<PetCardProps> = ({ pet, isActive, onClick }) => {
  return (
    <div
      className={`molecule-pet-card ${isActive ? 'active' : ''}`}
      onClick={() => onClick(pet)}
      role="button"
      tabIndex={0}
      aria-label={`Selecionar pet ${pet.name}`}
    >
      <div className="molecule-pet-card__avatar-wrapper">
        {pet.photoUrl ? (
          <img 
            src={pet.photoUrl} 
            alt={pet.name} 
            className="molecule-pet-card__img" 
          />
        ) : (
          <div className="molecule-pet-card__img" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: 'var(--color-surface-container-lowest)', color: 'var(--color-on-surface)' }}>
            <span className="material-symbols-outlined" style={{ fontSize: '32px' }}>pets</span>
          </div>
        )}
        {isActive && <div className="molecule-pet-card__status-dot"></div>}
      </div>
      <span className="molecule-pet-card__name">{pet.name}</span>
    </div>
  );
};
