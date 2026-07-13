import React, from 'react';
import { Veterinarian } from '../../../domain/models/Veterinarian';
import './styles.css';

interface Props {
  veterinarian: Veterinarian;
}

export function VetCard({ veterinarian }: Props) {
  return (
    <div className="vet-card">
      <div className="vet-card__header">
        {veterinarian.profilePhotoUrl ? (
          <img src={veterinarian.profilePhotoUrl} alt={veterinarian.fullName} className="vet-card__photo" />
        ) : (
          <div className="vet-card__photo-placeholder" />
        )}
        <div className="vet-card__info">
          <h3 className="vet-card__name">{veterinarian.fullName}</h3>
          <p className="vet-card__crmv">CRMV-{veterinarian.crmvState} {veterinarian.crmvNumber}</p>
        </div>
      </div>
      <div className="vet-card__specialties">
        {veterinarian.specialties.slice(0, 3).map((spec) => (
          <span key={spec} className="vet-card__specialty-chip">{spec}</span>
        ))}
        {veterinarian.specialties.length > 3 && (
          <span className="vet-card__specialty-chip">+{veterinarian.specialties.length - 3}</span>
        )}
      </div>
      <div className="vet-card__status">
        <span className={`vet-card__availability vet-card__availability--${veterinarian.availabilityStatus.toLowerCase()}`}>
          {veterinarian.availabilityStatus === 'AVAILABLE' ? 'Disponível' : 'Indisponível'}
        </span>
        {veterinarian.emergencyOnDuty && (
          <span className="vet-card__emergency-badge">Plantão Emergência</span>
        )}
      </div>
    </div>
  );
}
