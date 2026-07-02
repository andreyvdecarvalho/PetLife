import React from 'react';
import { Vaccination } from '../../../domain/pet/Vaccination';
import './styles.css';

interface VaccineListProps {
  vaccinations: Vaccination[];
  onVaccineClick?: (vaccine: Vaccination) => void;
  emptyMessage?: string;
}

export const VaccineList: React.FC<VaccineListProps> = ({ 
  vaccinations, 
  onVaccineClick,
  emptyMessage = "Nenhuma vacina registrada."
}) => {
  if (!vaccinations.length) {
    return (
      <div className="organism-vaccine-list__empty">
        <span className="material-symbols-outlined">vaccines</span>
        <p>{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="organism-vaccine-list">
      {vaccinations.map((vaccine) => (
        <div 
          key={vaccine.id} 
          className="organism-vaccine-list__card"
          onClick={() => onVaccineClick?.(vaccine)}
          role="button"
          tabIndex={0}
        >
          <div className="organism-vaccine-list__icon">
            <span className="material-symbols-outlined">vaccines</span>
          </div>
          <div className="organism-vaccine-list__content">
            <h4 className="organism-vaccine-list__title">{vaccine.vaccineName}</h4>
            <div className="organism-vaccine-list__details">
              <span>{new Date(vaccine.dateAdministered).toLocaleDateString()}</span>
              {vaccine.nextDoseDate && (
                <>
                  <span className="organism-vaccine-list__dot">•</span>
                  <span className="organism-vaccine-list__next">Próx: {new Date(vaccine.nextDoseDate).toLocaleDateString()}</span>
                </>
              )}
            </div>
          </div>
          {vaccine.proofUrl && (
            <div className="organism-vaccine-list__attachment" title="Comprovante anexado">
              <span className="material-symbols-outlined">attachment</span>
            </div>
          )}
          <div className="organism-vaccine-list__arrow">
            <span className="material-symbols-outlined">chevron_right</span>
          </div>
        </div>
      ))}
    </div>
  );
};
