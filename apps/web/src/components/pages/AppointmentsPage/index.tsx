import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import './styles.css';

interface Appointment {
  id: string;
  petName: string;
  petDescription: string;
  petImageUrl?: string;
  status: 'confirmed' | 'pending' | 'completed';
  statusLabel: string;
  statusIcon: string;
  providerName: string;
  providerSpecialty: string;
  providerIcon: string;
  providerColor: 'primary' | 'secondary';
  location: string;
  date: string;
  time: string;
  accentBg: string;
}

export const AppointmentsPageContent: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [activeTab, setActiveTab] = useState<'upcoming' | 'history'>('upcoming');

  const [appointments, setAppointments] = useState<Appointment[]>([
    {
      id: 'app-1',
      petName: 'Max',
      petDescription: 'Cachorro • Golden Retriever',
      petImageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuB1MDtbTTWthqTMVpm-rKxZ5uStSjMVCsbNfST1-dhsSXwsmyUFUtDJsR3VdNhTnSJoEqbEe_zJnC5vEQcvrc8xqdyrhU_1Dg-l_TZLub7Mwk_57Qt2QJolFTJuNcSPpuC-rbm7Ctbcfk176D6Wd6EiYO0mitNPbL52o6YtC430fp8tBhxr0yaW8KP5URCiEsz9EKu90z51gsynFQVi9dwKEUnX9kXAHNWLBbzUnecxHpBHP50hYG4cMtjIjv2GVuch0HefVC5fBMI',
      status: 'confirmed',
      statusLabel: 'Confirmado',
      statusIcon: 'event_available',
      providerName: 'Dr. Ricardo Silva',
      providerSpecialty: 'Clínico Geral',
      providerIcon: 'stethoscope',
      providerColor: 'primary',
      location: 'Clínica Vida Pet',
      date: 'Terça, 17 de Outubro',
      time: '10:00 - 11:00',
      accentBg: 'primary'
    },
    {
      id: 'app-2',
      petName: 'Luna',
      petDescription: 'Gato • Siamês',
      status: 'pending',
      statusLabel: 'Aguardando',
      statusIcon: 'schedule',
      providerName: 'Pet Shop Estilo',
      providerSpecialty: 'Banho & Tosa',
      providerIcon: 'content_cut',
      providerColor: 'secondary',
      location: 'Unidade Centro',
      date: 'Quinta, 26 de Outubro',
      time: '14:30 - 16:00',
      accentBg: 'secondary'
    }
  ]);

  const handleAction = (actionType: 'details' | 'reschedule', id: string) => {
    const app = appointments.find(a => a.id === id);
    if (!app) return;

    if (actionType === 'details') {
      showToast(`Exibindo detalhes do agendamento para ${app.petName}`, 'info');
    } else if (actionType === 'reschedule') {
      showToast(`Solicitação de reagendamento para ${app.petName} enviada! 🗓️`, 'success');
      
      // Simulate confirmation of rescheduled item
      if (app.status === 'pending') {
        setAppointments(prev => prev.map(item => {
          if (item.id === id) {
            return {
              ...item,
              status: 'confirmed',
              statusLabel: 'Confirmado',
              statusIcon: 'event_available'
            };
          }
          return item;
        }));
      }
    }
  };

  const filteredAppointments = activeTab === 'upcoming' 
    ? appointments.filter(app => app.status !== 'completed')
    : appointments.filter(app => app.status === 'completed');

  return (
    <div className="appointments-page animate-fade-in">
      {/* TopAppBar */}
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

      {/* Segmented Control (Tabs) */}
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

      {/* Appointments List */}
      <div className="appointments-page__grid">
        {filteredAppointments.length > 0 ? (
          filteredAppointments.map(app => (
            <article key={app.id} className="appointments-page__card">
              {/* Status Chip */}
              <div className={`appointments-page__status-badge appointments-page__status-badge--${app.status}`}>
                <span className="material-symbols-outlined">
                  {app.statusIcon}
                </span>
                {app.statusLabel}
              </div>

              {/* Pet Info */}
              <div className="appointments-page__pet-info">
                {app.petImageUrl ? (
                  <div className="appointments-page__pet-avatar">
                    <img src={app.petImageUrl} alt={app.petName} />
                  </div>
                ) : (
                  <div className="appointments-page__pet-avatar-placeholder">
                    <span className="material-symbols-outlined">pets</span>
                  </div>
                )}
                <div>
                  <h2 className="appointments-page__pet-name">{app.petName}</h2>
                  <p className="appointments-page__pet-desc">{app.petDescription}</p>
                </div>
              </div>

              {/* Professional & Location */}
              <div className="appointments-page__details-box">
                <div className="appointments-page__detail-item">
                  <span className={`material-symbols-outlined filled text-${app.providerColor}`}>
                    {app.providerIcon}
                  </span>
                  <div>
                    <p className="appointments-page__detail-name">{app.providerName}</p>
                    <p className="appointments-page__detail-subtitle">{app.providerSpecialty}</p>
                  </div>
                </div>
                <div className="appointments-page__detail-item">
                  <span className={`material-symbols-outlined text-${app.providerColor}`}>
                    location_on
                  </span>
                  <p className="appointments-page__detail-location">{app.location}</p>
                </div>
              </div>

              {/* Date & Time */}
              <div className={`appointments-page__time-box appointments-page__time-box--${app.accentBg}`}>
                <span className="material-symbols-outlined">calendar_month</span>
                <div>
                  <p className="appointments-page__time-date">{app.date}</p>
                  <p className="appointments-page__time-hours">{app.time}</p>
                </div>
              </div>

              {/* Actions */}
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
          ))
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
