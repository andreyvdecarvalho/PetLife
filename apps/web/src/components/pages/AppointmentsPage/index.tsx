import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useConsultations } from '../../../application/consultation/useConsultations';
import './styles.css';

export const AppointmentsPageContent: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [activeTab, setActiveTab] = useState<'upcoming' | 'history'>('upcoming');

  const { pets, fetchPets } = useGetPets();
  const [selectedPetId, setSelectedPetId] = useState<string | null>(null);

  const { consultations, loading, fetchConsultations } = useConsultations(selectedPetId || '');

  useEffect(() => {
    fetchPets();
  }, [fetchPets]);

  useEffect(() => {
    if (pets.length > 0 && !selectedPetId) {
      setSelectedPetId(pets[0].id);
    }
  }, [pets, selectedPetId]);

  useEffect(() => {
    if (selectedPetId) {
      fetchConsultations();
    }
  }, [selectedPetId, fetchConsultations]);

  const handleAction = (actionType: 'details' | 'reschedule', id: string) => {
    if (actionType === 'details') {
      showToast(`Exibindo detalhes do agendamento`, 'info');
    } else if (actionType === 'reschedule') {
      showToast(`Solicitação de reagendamento enviada! 🗓️`, 'success');
    }
  };

  const filteredAppointments = activeTab === 'upcoming' 
    ? consultations.filter(app => new Date(app.date) >= new Date())
    : consultations.filter(app => new Date(app.date) < new Date());

  const getPet = (id: string) => pets.find(p => p.id === id);

  return (
    <div className="appointments-page animate-fade-in">
      <div className="appointments-page__header-row">
        <button 
          className="appointments-page__back-btn" 
          onClick={() => navigate('/')}
          aria-label="Voltar para home"
        >
          <span className="material-symbols-outlined">arrow_back</span>
        </button>
        <h1 className="appointments-page__title">Meus Agendamentos</h1>
        <div className="appointments-page__spacer"></div>
      </div>

      <div className="appointments-page__tabs shadow-sm">
        <button 
          className={`appointments-page__tab ${activeTab === 'upcoming' ? 'active' : ''}`}
          onClick={() => setActiveTab('upcoming')}
        >
          Próximos
        </button>
        <button 
          className={`appointments-page__tab ${activeTab === 'history' ? 'active' : ''}`}
          onClick={() => setActiveTab('history')}
        >
          Histórico
        </button>
      </div>

      <div className="appointments-page__grid">
        {loading ? (
          <div className="appointments-page__empty">
            <span className="material-symbols-outlined spin">sync</span>
            <p>Carregando agendamentos...</p>
          </div>
        ) : filteredAppointments.length > 0 ? (
          filteredAppointments.map(app => {
            const pet = getPet(app.petId);
            return (
            <article key={app.id} className="appointments-page__card">
              <div className={`appointments-page__status-badge appointments-page__status-badge--pending`}>
                <span className="material-symbols-outlined">schedule</span>
                Agendado
              </div>

              <div className="appointments-page__pet-info">
                {pet?.photoUrl ? (
                  <div className="appointments-page__pet-avatar">
                    <img src={pet.photoUrl} alt={pet.name} />
                  </div>
                ) : (
                  <div className="appointments-page__pet-avatar-placeholder">
                    <span className="material-symbols-outlined">pets</span>
                  </div>
                )}
                <div>
                  <h2 className="appointments-page__pet-name">{pet?.name || 'Pet'}</h2>
                  <p className="appointments-page__pet-desc">{pet?.species || 'Desconhecido'}</p>
                </div>
              </div>

              <div className="appointments-page__details-box">
                <div className="appointments-page__detail-item">
                  <span className={`material-symbols-outlined filled text-primary`}>
                    stethoscope
                  </span>
                  <div>
                    <p className="appointments-page__detail-name">{app.veterinarianName}</p>
                    <p className="appointments-page__detail-subtitle">{app.specialty || 'Clínico Geral'}</p>
                  </div>
                </div>
                <div className="appointments-page__detail-item">
                  <span className={`material-symbols-outlined text-primary`}>
                    location_on
                  </span>
                  <p className="appointments-page__detail-location">{app.clinicName || 'Não informado'}</p>
                </div>
              </div>

              <div className={`appointments-page__time-box appointments-page__time-box--primary`}>
                <span className="material-symbols-outlined">calendar_month</span>
                <div>
                  <p className="appointments-page__time-date">{new Date(app.date).toLocaleDateString()}</p>
                  <p className="appointments-page__time-hours">{app.time}</p>
                </div>
              </div>

              <div className="appointments-page__actions">
                <button 
                  className="appointments-page__btn-primary"
                  onClick={() => handleAction('details', app.id)}
                >
                  Ver Detalhes
                </button>
                <button 
                  className="appointments-page__btn-secondary"
                  onClick={() => handleAction('reschedule', app.id)}
                >
                  Reagendar
                </button>
              </div>
            </article>
          )})
        ) : (
          <div className="appointments-page__empty">
            <span className="material-symbols-outlined">event_busy</span>
            <p>Nenhum agendamento encontrado.</p>
          </div>
        )}
      </div>
    </div>
  );
};
export { AppointmentsPageContent as AppointmentsPage };
