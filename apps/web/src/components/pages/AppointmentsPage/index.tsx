import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import { useGetPets } from '../../../application/pet/useGetPets';
import { consultationApi } from '../../../infrastructure/http/consultation.api';
import { vaccinationApi } from '../../../infrastructure/http/vaccination.api';
import { groomingApi } from '../../../infrastructure/http/grooming.api';
import './styles.css';

interface ConsolidatedAppointment {
  id: string;
  sourceId: string;
  petId: string;
  date: string;
  time: string;
  title: string;
  subtitle: string;
  location: string;
  icon: string;
  type: 'consultation' | 'vaccination' | 'grooming';
}

export const AppointmentsPageContent: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [activeTab, setActiveTab] = useState<'upcoming' | 'history'>('upcoming');

  const { pets, fetchPets } = useGetPets();
  const [appointments, setAppointments] = useState<ConsolidatedAppointment[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPets();
  }, [fetchPets]);

  useEffect(() => {
    const fetchAllAppointments = async () => {
      if (!pets || pets.length === 0) {
        setLoading(false);
        return;
      }
      setLoading(true);
      try {
        const all: ConsolidatedAppointment[] = [];
        for (const pet of pets) {
          const [cons, vacs, grooms] = await Promise.all([
            consultationApi.list(pet.id).catch(() => []),
            vaccinationApi.listVaccinations(pet.id).catch(() => []),
            groomingApi.listGroomings(pet.id).catch(() => [])
          ]);

          cons.forEach(c => {
            const cDate = new Date(c.date);
            const dateStr = cDate.toISOString().split('T')[0];
            const timeStr = cDate.toISOString().split('T')[1].substring(0, 5);
            all.push({ id: `cons-${c.id}`, sourceId: c.id, petId: pet.id, date: dateStr, time: timeStr, title: `Consulta com ${c.veterinarian || 'Veterinário'}`, subtitle: c.reason || 'Clínico Geral', location: c.clinic || 'Não informado', icon: 'stethoscope', type: 'consultation' });
          });

          vacs.forEach(v => {
            if (v.status === 'SCHEDULED') {
              all.push({ id: `vac-${v.id}`, sourceId: v.id, petId: pet.id, date: v.date, time: '09:00', title: `Vacina: ${v.name}`, subtitle: 'Agendamento de Vacina', location: v.veterinarian || 'Clínica', icon: 'vaccines', type: 'vaccination' });
            }
          });

          grooms.forEach(g => {
            const timeMatch = g.notes?.match(/Marcado para (\d{2}:\d{2})/);
            const extractedTime = timeMatch ? timeMatch[1] : '09:00';
            const typeStr = g.type === 'BATH' ? 'Banho' : g.type === 'GROOMING' ? 'Tosa' : 'Banho & Tosa';
            all.push({ id: `groom-${g.id}`, sourceId: g.id, petId: pet.id, date: new Date(g.date).toISOString().split('T')[0], time: extractedTime, title: typeStr, subtitle: 'Serviço de Estética', location: g.provider || 'PetShop', icon: 'content_cut', type: 'grooming' });
          });
        }
        all.sort((a, b) => new Date(`${a.date}T${a.time}`).getTime() - new Date(`${b.date}T${b.time}`).getTime());
        setAppointments(all);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchAllAppointments();
  }, [pets]);

  const handleAction = (actionType: 'details' | 'reschedule', id: string) => {
    if (actionType === 'details') {
      showToast(`Exibindo detalhes do agendamento`, 'info');
    } else if (actionType === 'reschedule') {
      showToast(`Solicitação de reagendamento enviada! 🗓️`, 'success');
    }
  };

  const todayStr = new Date().toISOString().split('T')[0];
  const filteredAppointments = activeTab === 'upcoming' 
    ? appointments.filter(app => app.date >= todayStr)
    : appointments.filter(app => app.date < todayStr);

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
        <button 
          className="appointments-page__add-btn"
          aria-label="Adicionar agendamento"
          onClick={() => navigate('/routine')}
        >
          <span className="material-symbols-outlined">add</span>
        </button>
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
                    {app.icon}
                  </span>
                  <div>
                    <p className="appointments-page__detail-name">{app.title}</p>
                    <p className="appointments-page__detail-subtitle">{app.subtitle}</p>
                  </div>
                </div>
                <div className="appointments-page__detail-item">
                  <span className={`material-symbols-outlined text-primary`}>
                    location_on
                  </span>
                  <p className="appointments-page__detail-location">{app.location}</p>
                </div>
              </div>

              <div className={`appointments-page__time-box appointments-page__time-box--primary`}>
                <span className="material-symbols-outlined">calendar_month</span>
                <div>
                  <p className="appointments-page__time-date">{new Date(`${app.date}T12:00:00`).toLocaleDateString()}</p>
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
