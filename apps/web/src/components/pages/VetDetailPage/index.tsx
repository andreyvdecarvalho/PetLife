import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useGetVetProfile } from '../../../application/veterinarian/useGetVetProfile';
import { Button } from '../../atoms/Button';
import './styles.css';

export function VetDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { getProfile, vet, loading, error } = useGetVetProfile();

  useEffect(() => {
    if (id) {
      getProfile(id);
    }
  }, [id, getProfile]);

  if (loading) {
    return <div className="vet-detail-page__loading">Carregando perfil...</div>;
  }

  if (error || !vet) {
    return (
      <div className="vet-detail-page__error">
        <p>{error || 'Veterinário não encontrado.'}</p>
        <Button onClick={() => navigate(-1)}>Voltar</Button>
      </div>
    );
  }

  return (
    <div className="vet-detail-page">
      <header className="vet-detail-page__header">
        <Button onClick={() => navigate(-1)} className="vet-detail-page__back-btn" variant="text">
          &larr; Voltar
        </Button>
      </header>
      
      <div className="vet-detail-page__content">
        <div className="vet-detail-page__profile-card">
          {vet.profilePhotoUrl ? (
            <img src={vet.profilePhotoUrl} alt={vet.fullName} className="vet-detail-page__photo" />
          ) : (
            <div className="vet-detail-page__photo-placeholder" />
          )}
          <h1 className="vet-detail-page__name">{vet.fullName}</h1>
          <p className="vet-detail-page__crmv">CRMV-{vet.crmvState} {vet.crmvNumber}</p>
          <div className="vet-detail-page__status">
            <span className={`vet-detail-page__availability vet-detail-page__availability--${vet.availabilityStatus.toLowerCase()}`}>
              {vet.availabilityStatus === 'AVAILABLE' ? 'Disponível' : 'Indisponível'}
            </span>
            {vet.emergencyOnDuty && <span className="vet-detail-page__emergency">Plantão Emergência</span>}
          </div>
        </div>

        <div className="vet-detail-page__info">
          <section className="vet-detail-page__section">
            <h3>Sobre</h3>
            <p>{vet.bio || 'Nenhuma biografia informada.'}</p>
          </section>

          <section className="vet-detail-page__section">
            <h3>Especialidades</h3>
            <div className="vet-detail-page__chips">
              {vet.specialties?.map(s => <span key={s} className="vet-detail-page__chip">{s}</span>)}
            </div>
          </section>

          <section className="vet-detail-page__section">
            <h3>Modalidades de Atendimento</h3>
            <div className="vet-detail-page__chips">
              {vet.modalities?.map(m => (
                <span key={m} className="vet-detail-page__chip">
                  {m === 'CLINIC' ? 'Clínica' : m === 'HOME' ? 'Domicílio' : 'Online'}
                </span>
              ))}
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}
