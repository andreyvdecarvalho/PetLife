import React, { useEffect, useState } from 'react';
import { useVeterinarianProfile } from '../../application/veterinarian/useVeterinarianProfile';
import { Veterinarian } from '../../domain/models/Veterinarian';
import './VetProfilePage.css';

export function VetProfilePage() {
  const { getMyProfile, updateAvailability, updateEmergency, loading, error } = useVeterinarianProfile();
  const [vet, setVet] = useState<Veterinarian | null>(null);

  useEffect(() => {
    getMyProfile().then(setVet).catch(console.error);
  }, [getMyProfile]);

  const handleAvailabilityToggle = async () => {
    if (!vet) return;
    const newStatus = vet.availabilityStatus === 'AVAILABLE' ? 'UNAVAILABLE' : 'AVAILABLE';
    await updateAvailability(newStatus);
    setVet({ ...vet, availabilityStatus: newStatus });
  };

  const handleEmergencyToggle = async () => {
    if (!vet) return;
    const newStatus = !vet.emergencyOnDuty;
    await updateEmergency(newStatus);
    setVet({ ...vet, emergencyOnDuty: newStatus });
  };

  if (loading && !vet) return <div>Carregando...</div>;
  if (error && !vet) return <div className="vet-profile-page__error">{error}</div>;
  if (!vet) return <div>Perfil não encontrado.</div>;

  return (
    <div className="vet-profile-page">
      <h1 className="vet-profile-page__title">Dashboard do Veterinário</h1>
      <div className="vet-profile-page__content">
        <section className="vet-profile-page__section">
          <h2>Dados Básicos</h2>
          <p><strong>Nome:</strong> {vet.fullName}</p>
          <p><strong>CRMV:</strong> {vet.crmvState} - {vet.crmvNumber}</p>
        </section>
        <section className="vet-profile-page__section">
          <h2>Controles</h2>
          <div className="vet-profile-page__controls">
            <button onClick={handleAvailabilityToggle}>
              Mudar Disponibilidade (Atual: {vet.availabilityStatus})
            </button>
            <button onClick={handleEmergencyToggle}>
              Mudar Plantão (Atual: {vet.emergencyOnDuty ? 'Sim' : 'Não'})
            </button>
          </div>
        </section>
      </div>
    </div>
  );
}
