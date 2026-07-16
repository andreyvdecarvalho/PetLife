import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useRoutineActivities } from '../../../application/routine/useRoutineActivities';
import { useMedications } from '../../../application/medications/useMedications';
import { useConsultations } from '../../../application/consultation/useConsultations';
import { useGrooming } from '../../../application/grooming/useGrooming';
import './styles.css';

interface ConsolidatedActivity {
  id: string;
  sourceId: string;
  title: string;
  time: string;
  description: string;
  status: 'completed' | 'pending' | 'scheduled';
  type: 'walk' | 'medication' | 'grooming' | 'consultation' | 'feeding' | 'generic';
  source: 'activity' | 'medication' | 'consultation' | 'grooming';
}

export const RoutinePage: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  
  const { pets, fetchPets } = useGetPets();
  const [selectedPetId, setSelectedPetId] = useState<string | null>(null);
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  
  const { activities: rawActivities, fetchActivities, updateStatus: updateActivityStatus, addActivity } = useRoutineActivities(selectedPetId || '');
  const { medications, fetchMedications } = useMedications(selectedPetId || '');
  const { consultations, fetchConsultations, addConsultation } = useConsultations(selectedPetId || '');
  const { groomings, fetchGroomings, addGrooming } = useGrooming(selectedPetId || '');

  const [activities, setActivities] = useState<ConsolidatedActivity[]>([]);

  const [isActivityModalOpen, setIsActivityModalOpen] = useState(false);
  const [isAppointmentModalOpen, setIsAppointmentModalOpen] = useState(false);
  const [isGroomingModalOpen, setIsGroomingModalOpen] = useState(false);

  const [actTitle, setActTitle] = useState('');
  const [actType, setActType] = useState<'WALK' | 'FEEDING' | 'GENERIC'>('WALK');
  const [actTime, setActTime] = useState('08:00');
  const [actDesc, setActDesc] = useState('');

  const [appVet, setAppVet] = useState('');
  const [appSpec, setAppSpec] = useState('');
  const [appClinic, setAppClinic] = useState('');
  const [appTime, setAppTime] = useState('10:00');

  const [groomProvider, setGroomProvider] = useState('');
  const [groomType, setGroomType] = useState<'BATH' | 'GROOMING' | 'BATH_AND_GROOMING'>('BATH');
  const [groomTime, setGroomTime] = useState('09:00');

  useEffect(() => { fetchPets(); }, [fetchPets]);

  useEffect(() => {
    if (pets.length > 0 && !selectedPetId) setSelectedPetId(pets[0].id);
  }, [pets, selectedPetId]);

  useEffect(() => {
    if (selectedPetId) {
      const dateStr = selectedDate.toISOString().split('T')[0];
      fetchActivities(dateStr);
      fetchMedications();
      fetchConsultations();
      fetchGroomings();
    }
  }, [selectedPetId, selectedDate, fetchActivities, fetchMedications, fetchConsultations, fetchGroomings]);

  useEffect(() => {
    const cons: ConsolidatedActivity[] = [];
    rawActivities.forEach(a => {
      cons.push({ id: `act-${a.id}`, sourceId: a.id, title: a.title, time: a.activityTime?.substring(0, 5) || '00:00', description: a.description || '', status: a.status.toLowerCase() as any, type: a.type.toLowerCase() as any, source: 'activity' });
    });

    const dateStr = selectedDate.toISOString().split('T')[0];
    medications.forEach(m => {
      const dosesToday = m.administrations?.filter(admin => admin.scheduledDate === dateStr) || [];
      dosesToday.forEach(dose => {
        cons.push({ id: `med-${dose.id}`, sourceId: dose.id, title: m.name, time: dose.scheduledTime?.substring(0, 5) || '00:00', description: `Dose: ${m.dosage}`, status: dose.status === 'ADMINISTERED' ? 'completed' : 'pending', type: 'medication', source: 'medication' });
      });
    });

    consultations.forEach(c => {
      if (c.date === dateStr) {
        cons.push({ id: `cons-${c.id}`, sourceId: c.id, title: `Consulta com ${c.veterinarianName}`, time: c.time || '00:00', description: c.specialty || 'Clínico Geral', status: c.status === 'COMPLETED' ? 'completed' : (c.status === 'CONFIRMED' ? 'scheduled' : 'pending'), type: 'consultation', source: 'consultation' });
      }
    });

    groomings.forEach(g => {
      const gDate = new Date(g.date);
      const gDateStr = gDate.toISOString().split('T')[0];
      if (gDateStr === dateStr) {
        const typeStr = g.type === 'BATH' ? 'Banho' : g.type === 'GROOMING' ? 'Tosa' : 'Banho & Tosa';
        const timeMatch = g.notes?.match(/Marcado para (\d{2}:\d{2})/);
        const extractedTime = timeMatch ? timeMatch[1] : '09:00';
        cons.push({ id: `groom-${g.id}`, sourceId: g.id, title: typeStr, time: extractedTime, description: g.provider || 'PetShop', status: 'scheduled', type: 'grooming', source: 'grooming' });
      }
    });

    cons.sort((a, b) => a.time.localeCompare(b.time));
    setActivities(cons);
  }, [rawActivities, medications, consultations, groomings, selectedDate]);

  const handleDaySelect = (day: number) => {
    const newDate = new Date(selectedDate);
    newDate.setDate(day);
    setSelectedDate(newDate);
  };

  const handleToggleStatus = async (act: ConsolidatedActivity) => {
    if (act.source === 'activity') {
      const nextStatus = act.status === 'pending' ? 'COMPLETED' : 'PENDING';
      const success = await updateActivityStatus(act.sourceId, nextStatus);
      if (success) {
        showToast('Status atualizado! 🎉', 'success');
        const dateStr = selectedDate.toISOString().split('T')[0];
        fetchActivities(dateStr);
      }
    } else {
      showToast('Apenas atividades da rotina podem ser marcadas por aqui.', 'info');
    }
  };

  const submitActivity = async (e: React.FormEvent) => {
    e.preventDefault();
    const dateStr = selectedDate.toISOString().split('T')[0];
    await addActivity({ title: actTitle, type: actType, activityDate: dateStr, activityTime: actTime + ':00', description: actDesc, status: 'PENDING' });
    setIsActivityModalOpen(false);
    showToast('Atividade criada!', 'success');
    fetchActivities(dateStr);
  };

  const submitAppointment = async (e: React.FormEvent) => {
    e.preventDefault();
    const dateStr = selectedDate.toISOString().split('T')[0];
    await addConsultation({ veterinarianName: appVet, specialty: appSpec, clinicName: appClinic, date: dateStr, time: appTime, notes: '' });
    setIsAppointmentModalOpen(false);
    showToast('Agendamento criado!', 'success');
    fetchConsultations();
  };

  const submitGrooming = async (e: React.FormEvent) => {
    e.preventDefault();
    const dateStr = selectedDate.toISOString().split('T')[0];
    await addGrooming({ provider: groomProvider, type: groomType, date: dateStr, notes: `Marcado para ${groomTime}` });
    setIsGroomingModalOpen(false);
    showToast('Banho/Tosa agendado!', 'success');
    fetchGroomings();
  };

  const daysInMonth = new Date(selectedDate.getFullYear(), selectedDate.getMonth() + 1, 0).getDate();
  const firstDay = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), 1).getDay();
  const spacers = Array.from({ length: firstDay }, (_, i) => i);

  return (
    <div className="routine-page">
      <header className="routine-page__header">
        <h2 className="routine-page__title">Minha Rotina</h2>
        <p className="routine-page__subtitle">Acompanhe e planeje as atividades do seu pet.</p>
      </header>

      <main className="routine-page__main">
        <div className="routine-page__grid">
          <aside className="routine-page__calendar-col">
            <div className="routine-page__calendar-card">
              <div className="routine-page__month-selector">
                <button className="routine-page__month-btn" onClick={() => { const d = new Date(selectedDate); d.setMonth(d.getMonth() - 1); setSelectedDate(d); }}><span className="material-symbols-outlined">chevron_left</span></button>
                <span className="routine-page__month-label">{selectedDate.toLocaleString('pt-BR', { month: 'long', year: 'numeric' })}</span>
                <button className="routine-page__month-btn" onClick={() => { const d = new Date(selectedDate); d.setMonth(d.getMonth() + 1); setSelectedDate(d); }}><span className="material-symbols-outlined">chevron_right</span></button>
              </div>
              <div className="routine-page__weekdays">
                <span>D</span><span>S</span><span>T</span><span>Q</span><span>Q</span><span>S</span><span>S</span>
              </div>
              <div className="routine-page__days-grid">
                {spacers.map(s => <div key={`spacer-${s}`} className="routine-page__day-spacer"></div>)}
                {Array.from({ length: daysInMonth }, (_, i) => i + 1).map(day => (
                  <button key={day} className={`routine-page__day-btn ${day === selectedDate.getDate() ? 'routine-page__day-btn--active' : ''}`} onClick={() => handleDaySelect(day)}>{day}</button>
                ))}
              </div>
            </div>
          </aside>

          <section className="routine-page__activities-col">
            <div className="routine-page__date-header">
              <h3 className="routine-page__date-title">{selectedDate.toLocaleDateString('pt-BR', { day: 'numeric', month: 'long', weekday: 'long' })}</h3>
              <span className="routine-page__badge">{activities.length} Atividades</span>
            </div>

            <div className="routine-page__list">
              {activities.length > 0 ? activities.map(act => (
                <div key={act.id} className={`routine-page__activity-item routine-page__activity-item--${act.type}`} onClick={() => act.status === 'pending' && handleToggleStatus(act)} style={{ cursor: act.status === 'pending' && act.source === 'activity' ? 'pointer' : 'default' }}>
                  <div className={`routine-page__activity-icon routine-page__activity-icon--${act.type}`}>
                    <span className="material-symbols-outlined">{act.type === 'walk' ? 'directions_walk' : act.type === 'medication' ? 'medication' : act.type === 'feeding' ? 'restaurant' : act.type === 'consultation' ? 'stethoscope' : 'event'}</span>
                  </div>
                  <div className="routine-page__activity-content">
                    <div className="routine-page__activity-top">
                      <h4>{act.title}</h4>
                      <span>{act.time}</span>
                    </div>
                    <p>{act.description}</p>
                    <div className={`routine-page__status-chip routine-page__status-chip--${act.status}`}>
                      <span className="material-symbols-outlined">{act.status === 'completed' ? 'done' : 'schedule'}</span>
                      {act.status === 'completed' ? 'Concluído' : act.status === 'pending' ? 'Pendente' : 'Agendado'}
                    </div>
                  </div>
                </div>
              )) : <p className="routine-page__empty">Nenhuma atividade para este dia.</p>}
            </div>

            <div className="routine-page__actions-grid">
              <button className="routine-page__action-card" onClick={() => setIsAppointmentModalOpen(true)} data-testid="btn-add-appointment">
                <div className="routine-page__action-icon routine-page__action-icon--primary"><span className="material-symbols-outlined">medical_services</span></div>
                <div className="routine-page__action-text">
                  <span className="routine-page__action-title">Agendar Veterinário</span>
                  <span className="routine-page__action-desc">Marcar próxima consulta de rotina.</span>
                </div>
              </button>

              <button className="routine-page__action-card" onClick={() => setIsGroomingModalOpen(true)} data-testid="btn-add-grooming">
                <div className="routine-page__action-icon routine-page__action-icon--tertiary"><span className="material-symbols-outlined">content_cut</span></div>
                <div className="routine-page__action-text">
                  <span className="routine-page__action-title">Agendar Banho e Tosa</span>
                  <span className="routine-page__action-desc">Marcar serviços de estética e higiene.</span>
                </div>
              </button>

              <button className="routine-page__action-card" onClick={() => setIsActivityModalOpen(true)} data-testid="btn-add-activity">
                <div className="routine-page__action-icon routine-page__action-icon--secondary"><span className="material-symbols-outlined">event_repeat</span></div>
                <div className="routine-page__action-text">
                  <span className="routine-page__action-title">Planejar Atividade</span>
                  <span className="routine-page__action-desc">Configurar passeios e rotinas.</span>
                </div>
              </button>
            </div>
          </section>
        </div>
      </main>

      {/* Modals are styled identically to Medication Modals, but with routine prefix to avoid cross-contamination */}
      {isActivityModalOpen && (
        <div className="routine-page__modal-overlay">
          <div className="routine-page__modal">
            <h2 className="routine-page__modal-title">Planejar Atividade</h2>
            <form onSubmit={submitActivity} className="routine-page__form">
              <div className="routine-page__form-field">
                <label>Título</label>
                <input type="text" value={actTitle} onChange={e => setActTitle(e.target.value)} required data-testid="input-act-title" />
              </div>
              <div className="routine-page__form-row">
                <div className="routine-page__form-field">
                  <label>Tipo</label>
                  <select value={actType} onChange={e => setActType(e.target.value as any)} data-testid="input-act-type">
                    <option value="WALK">Passeio</option>
                    <option value="FEEDING">Alimentação</option>
                    <option value="GENERIC">Geral</option>
                  </select>
                </div>
                <div className="routine-page__form-field">
                  <label>Horário</label>
                  <input type="time" value={actTime} onChange={e => setActTime(e.target.value)} required data-testid="input-act-time" />
                </div>
              </div>
              <div className="routine-page__form-field">
                <label>Descrição</label>
                <textarea value={actDesc} onChange={e => setActDesc(e.target.value)} rows={3} data-testid="input-act-desc"></textarea>
              </div>
              <div className="routine-page__form-actions">
                <button type="button" className="routine-page__btn-secondary" onClick={() => setIsActivityModalOpen(false)}>Cancelar</button>
                <button type="submit" className="routine-page__btn-primary">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isAppointmentModalOpen && (
        <div className="routine-page__modal-overlay">
          <div className="routine-page__modal">
            <h2 className="routine-page__modal-title">Agendar Retorno</h2>
            <form onSubmit={submitAppointment} className="routine-page__form">
              <div className="routine-page__form-field">
                <label>Veterinário</label>
                <input type="text" value={appVet} onChange={e => setAppVet(e.target.value)} required data-testid="input-app-vet" />
              </div>
              <div className="routine-page__form-field">
                <label>Especialidade</label>
                <input type="text" value={appSpec} onChange={e => setAppSpec(e.target.value)} data-testid="input-app-spec" />
              </div>
              <div className="routine-page__form-row">
                <div className="routine-page__form-field">
                  <label>Clínica</label>
                  <input type="text" value={appClinic} onChange={e => setAppClinic(e.target.value)} data-testid="input-app-clinic" />
                </div>
                <div className="routine-page__form-field">
                  <label>Horário</label>
                  <input type="time" value={appTime} onChange={e => setAppTime(e.target.value)} required data-testid="input-app-time" />
                </div>
              </div>
              <div className="routine-page__form-actions">
                <button type="button" className="routine-page__btn-secondary" onClick={() => setIsAppointmentModalOpen(false)}>Cancelar</button>
                <button type="submit" className="routine-page__btn-primary">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isGroomingModalOpen && (
        <div className="routine-page__modal-overlay">
          <div className="routine-page__modal">
            <h2 className="routine-page__modal-title">Agendar Banho e Tosa</h2>
            <form onSubmit={submitGrooming} className="routine-page__form">
              <div className="routine-page__form-field">
                <label>PetShop / Local</label>
                <input type="text" value={groomProvider} onChange={e => setGroomProvider(e.target.value)} required data-testid="input-groom-provider" />
              </div>
              <div className="routine-page__form-row">
                <div className="routine-page__form-field">
                  <label>Tipo de Serviço</label>
                  <select value={groomType} onChange={e => setGroomType(e.target.value as any)} data-testid="input-groom-type">
                    <option value="BATH">Banho</option>
                    <option value="GROOMING">Tosa</option>
                    <option value="BATH_AND_GROOMING">Banho & Tosa</option>
                  </select>
                </div>
                <div className="routine-page__form-field">
                  <label>Horário</label>
                  <input type="time" value={groomTime} onChange={e => setGroomTime(e.target.value)} required data-testid="input-groom-time" />
                </div>
              </div>
              <div className="routine-page__form-actions">
                <button type="button" className="routine-page__btn-secondary" onClick={() => setIsGroomingModalOpen(false)}>Cancelar</button>
                <button type="submit" className="routine-page__btn-primary">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
