import React from 'react';
import type { Pet } from '../../../domain/pet/Pet';
import './styles.css';

interface PetCardProps {
  pet: Pet;
  isActive: boolean;
  onClick: (pet: Pet) => void;
  onToggleStatus: (pet: Pet) => void;
}

export const PetCard: React.FC<PetCardProps> = ({ pet, isActive, onClick, onToggleStatus }) => {
  return (
    <div
      className={`molecule-pet-card ${isActive ? 'active' : ''}`}
      onClick={() => onClick(pet)}
      role="button"
      tabIndex={0}
      aria-label={`Selecionar pet ${pet.name}`}
    >
      <button
        data-testid={pet.status === 'ACTIVE' ? 'btn-archive-pet' : 'btn-unarchive-pet'}
        onClick={e => {
          e.stopPropagation();
          onToggleStatus(pet);
        }}
        className="molecule-pet-card__status-toggle"
        aria-label={pet.status === 'ACTIVE' ? 'Arquivar pet' : 'Desarquivar pet'}
      >
        <span className="material-symbols-outlined" style={{ color: pet.status === 'ACTIVE' ? 'var(--color-error)' : 'var(--color-primary)' }}>
          {pet.status === 'ACTIVE' ? 'archive' : 'unarchive'}
        </span>
      </button>
      <div className="molecule-pet-card__avatar-wrapper">
        <img 
          src={pet.photoUrl || 'https://lh3.googleusercontent.com/aida-public/AB6AXuCnHN_9BTUOKpIb36hpFqE25LwIGylq8VtlrHjbSrAhgWcSBgVoSTXH_BMmBraiV93TqeZ2ZdWgYjVg7-fUZGPf16xvHpZ1gPOoaBajWM-dc79Xh3ETkTvT0uKw6_LbbTAI7P1n1FCrkGvgvYi-4WVKYEqmihw85_IDBmKe8RphLlmRkpBmiLcHOnESAVKHJYV78g1ZQNwdwNApTfakidYekZzgfDrEj-Pn9OiAs79HAY7c_WsQ9Eo8vsMeD9OmxSAc5nMX25sEWIw'} 
          alt={pet.name} 
          className="molecule-pet-card__img" 
        />
        {isActive && <div className="molecule-pet-card__status-dot"></div>}
      </div>
      <span className="molecule-pet-card__name">{pet.name}</span>
    </div>
  );
};
