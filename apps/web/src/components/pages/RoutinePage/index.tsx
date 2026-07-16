import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useRoutineActivities } from '../../../application/routine/useRoutineActivities';
import { useMedications } from '../../../application/medications/useMedications';
import { useConsultations } from '../../../application/consultation/useConsultations';
import './styles.css';

interface ConsolidatedActivity {
  id: string;
  sourceId: string;
  title: string;
  time: string;
  description: string;
  status: 'completed' | 'pending' | 'scheduled';
  type: 'walk' | 'medication' | 'grooming' | 'consultation' | 'feeding' | 'generic';
  source: 'activity' | 'medication' | 'consultation';
}

export const RoutinePageContent: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  
  const { pets, fetchPets } = useGetPets();
  const [selectedPetId, setSelectedPetId] = useState<string | null>(null);

  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  
  const { activities: rawActivities, fetchActivities, updateStatus: updateActivityStatus, addActivity } = useRoutineActivities(selectedPetId || '');
  const { medications, fetchMedications } = useMedications(selectedPetId || '');
  const { consultations, fetchConsultations, addConsultation } = useConsultations(selectedPetId || '');

  const [activities, setActivities] = useState<ConsolidatedActivity[]>([]);

  // Modals state
  const [isActivityModalOpen, setIsActivityModalOpen] = useState(false);
  const [isAppointmentModalOpen, setIsAppointmentModalOpen] = useState(false);

  // Form states for Activity
  const [actTitle, setActTitle] = useState('');
  const [actType, setActType] = useState<'WALK' | 'FEEDING' | 'GENERIC'>('WALK');
  const [actTime, setActTime] = useState('08:00');
  const [actDesc, setActDesc] = useState('');

  // Form states for Appointment
  const [appVet, setAppVet] = useState('');
  const [appSpec, setAppSpec] = useState('');
  const [appClinic, setAppClinic] = useState('');
  const [appTime, setAppTime] = useState('10:00');

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
      const dateStr = selectedDate.toISOString().split('T')[0];
      fetchActivities(dateStr);
      fetchMedications();
      fetchConsultations();
    }
  }, [selectedPetId, selectedDate, fetchActivities, fetchMedications, fetchConsultations]);

  useEffect(() => {
    const cons: ConsolidatedActivity[] = [];
    
    // 1. Add Routine Activities
    rawActivities.forEach(a => {
      cons.push({
        id: `act-${a.id}`,
        sourceId: a.id,
        title: a.title,
        time: a.activityTime?.substring(0, 5) || '00:00',
        description: a.description || '',
        status: a.status.toLowerCase() as any,
        type: a.type.toLowerCase() as any,
        source: 'activity'
      });
    });

    // 2. Add Medications for the selected day
    const dateStr = selectedDate.toISOString().split('T')[0];
    medications.forEach(m => {
      // Find doses for this day
      const dosesToday = m.administrations?.filter(admin => admin.scheduledDate === dateStr) || [];
      dosesToday.forEach(dose => {
        cons.push({
          id: `med-${dose.id}`,
          sourceId: dose.id,
          title: m.name,
          time: dose.scheduledTime?.substring(0, 5) || '00:00',
          description: `Dose: ${m.dosage}`,
          status: dose.status === 'ADMINISTERED' ? 'completed' : 'pending',
          type: 'medication',
          source: 'medication'
        });
      });
    });

    // 3. Add Consultations for the selected day
    consultations.forEach(c => {
      if (c.date === dateStr) {
        cons.push({
          id: `cons-${c.id}`,
          sourceId: c.id,
          title: `Consulta com ${c.veterinarianName}`,
          time: c.time || '00:00',
          description: c.specialty || 'Clínico Geral',
          status: c.status === 'COMPLETED' ? 'completed' : (c.status === 'CONFIRMED' ? 'scheduled' : 'pending'),
          type: 'consultation',
          source: 'consultation'
        });
      }
    });

    // Sort by time
    cons.sort((a, b) => a.time.localeCompare(b.time));
    setActivities(cons);
  }, [rawActivities, medications, consultations, selectedDate]);

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
    await addActivity({
      title: actTitle,
      type: actType,
      activityDate: dateStr,
      activityTime: actTime + ':00',
      description: actDesc,
      status: 'PENDING'
    });
    setIsActivityModalOpen(false);
    showToast('Atividade criada!', 'success');
    fetchActivities(dateStr);
  };

  const submitAppointment = async (e: React.FormEvent) => {
    e.preventDefault();
    const dateStr = selectedDate.toISOString().split('T')[0];
    await addConsultation({
      veterinarianName: appVet,
      specialty: appSpec,
      clinicName: appClinic,
      date: dateStr,
      time: appTime,
      notes: ''
    });
    setIsAppointmentModalOpen(false);
    showToast('Agendamento criado!', 'success');
    fetchConsultations();
  };

  const daysInMonth = new Date(selectedDate.getFullYear(), selectedDate.getMonth() + 1, 0).getDate();
  const firstDay = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), 1).getDay();
  const spacers = Array.from({ length: firstDay }, (_, i) => i);

  return (
    <div className="routine-page animate-fade-in">
      {/* Page Header */}
      <section className="routine-page__header">
        <h2 className="routine-page__title">Minha Rotina</h2>
        <p className="routine-page__subtitle">Acompanhe e planeje as atividades do seu pet.</p>
      </section>

      {/* Layout Grid */}
      <div className="routine-page__grid">
        {/* Left Column: Calendar */}
        <aside className="routine-page__calendar-column">
          <div className="routine-page__calendar-card">
            {/* Month Selector */}
            <div className="routine-page__month-selector">
              <button 
                className="routine-page__month-btn" 
                onClick={() => {
                  const d = new Date(selectedDate);
                  d.setMonth(d.getMonth() - 1);
                  setSelectedDate(d);
                }}
                aria-label="Mês anterior"
              >
                <span className="material-symbols-outlined">chevron_left</span>
              </button>
              <span className="routine-page__month-label">
                {selectedDate.toLocaleString('pt-BR', { month: 'long', year: 'numeric' })}
              </span>
              <button 
                className="routine-page__month-btn" 
                onClick={() => {
                  const d = new Date(selectedDate);
                  d.setMonth(d.getMonth() + 1);
                  setSelectedDate(d);
                }}
                aria-label="Próximo mês"
              >
                <span className="material-symbols-outlined">chevron_right</span>
              </button>
            </div>

            {/* Weekdays */}
            <div className="routine-page__weekdays">
              <span>D</span><span>S</span><span>T</span><span>Q</span><span>Q</span><span>S</span><span>S</span>
            </div>

            {/* Days Grid */}
            <div className="routine-page__days-grid">
              {spacers.map(s => <div key={`spacer-${s}`} className="routine-page__day-spacer"></div>)}
              
              {Array.from({ length: daysInMonth }, (_, i) => i + 1).map(day => {
                const isSelected = day === selectedDate.getDate();
                return (
                  <button
                    key={day}
                    className={`routine-page__day-btn ${isSelected ? 'active' : ''}`}
                    onClick={() => handleDaySelect(day)}
                  >
                    {day}
                  </button>
                );
              })}
            </div>
          </div>
        </aside>

        {/* Right Column: Activities List */}
        <section className="routine-page__activities-column">
          <div className="routine-page__date-header">
            <h3 className="routine-page__date-title">{selectedDate.toLocaleDateString('pt-BR', { day: 'numeric', month: 'long', weekday: 'long' })}</h3>
            <span className="routine-page__badge">
              {activities.length} Atividades
            </span>
          </div>

          <div className="routine-page__activities-list">
            {activities.length > 0 ? activities.map(act => (
              <div 
                key={act.id} 
                className={`routine-page__activity-item routine-page__activity-item--${act.status} border-l-${
                  act.type === 'walk' ? 'tertiary' : act.type === 'medication' ? 'error' : 'primary'
                }`}
                onClick={() => act.status === 'pending' && handleToggleStatus(act)}
                style={{ cursor: act.status === 'pending' && act.source === 'activity' ? 'pointer' : 'default' }}
              >
                <div className={`routine-page__activity-icon-wrapper routine-page__activity-icon-wrapper--${act.type}`}>
                  <span className="material-symbols-outlined">
                    {act.type === 'walk' ? 'directions_walk' : 
                     act.type === 'medication' ? 'pill' : 
                     act.type === 'feeding' ? 'restaurant' : 
                     act.type === 'consultation' ? 'stethoscope' : 'event'}
                  </span>
                </div>
                <div className="routine-page__activity-info">
                  <div className="routine-page__activity-top">
                    <h4 className="routine-page__activity-title">{act.title}</h4>
                    <span className="routine-page__activity-time">{act.time}</span>
                  </div>
                  <p className="routine-page__activity-desc">{act.description}</p>
                  
                  <div className={`routine-page__status-chip routine-page__status-chip--${act.status}`}>
                    <span className="material-symbols-outlined">
                      {act.status === 'completed' ? 'done' : 'schedule'}
                    </span>
                    <span className="routine-page__status-label">
                      {act.status === 'completed' ? 'Concluído' : act.status === 'pending' ? 'Pendente' : 'Agendado'}
                    </span>
                  </div>
                </div>
              </div>
            )) : (
              <p className="text-on-surface-variant text-center mt-4">Nenhuma atividade para este dia.</p>
            )}
          </div>

          {/* Action Buttons */}
          <div className="routine-page__actions-grid">
            <button 
              className="routine-page__action-card group"
              onClick={() => setIsAppointmentModalOpen(true)}
            >
              <div className="routine-page__action-icon-wrapper bg-primary-container text-on-primary-container">
                <span className="material-symbols-outlined">medical_services</span>
              </div>
              <div className="routine-page__action-info-group">
                <span className="routine-page__action-title-label">Agendar Retorno Veterinário</span>
                <span className="routine-page__action-desc-label">Marcar próxima consulta de rotina.</span>
              </div>
            </button>

            <button 
              className="routine-page__action-card group"
              onClick={() => setIsActivityModalOpen(true)}
            >
              <div className="routine-page__action-icon-wrapper bg-secondary-container text-on-secondary-container">
                <span className="material-symbols-outlined">event_repeat</span>
              </div>
              <div className="routine-page__action-info-group">
                <span className="routine-page__action-title-label">Planejar Atividade</span>
                <span className="routine-page__action-desc-label">Passeios, alimentação e mais.</span>
              </div>
            </button>
            
            <button 
              className="routine-page__action-card group"
              onClick={() => navigate('/medications')}
            >
              <div className="routine-page__action-icon-wrapper bg-tertiary-container text-on-tertiary-container">
                <span className="material-symbols-outlined">pill</span>
              </div>
              <div className="routine-page__action-info-group">
                <span className="routine-page__action-title-label">Adicionar Medicamento</span>
                <span className="routine-page__action-desc-label">Novo tratamento ou dose única.</span>
              </div>
            </button>
          </div>
        </section>
      </div>

      {/* Activity Modal */}
      {isActivityModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Planejar Atividade</h3>
              <button className="icon-button" onClick={() => setIsActivityModalOpen(false)}>
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <form onSubmit={submitActivity} className="modal-body">
              <div className="input-group">
                <label>Título</label>
                <input type="text" value={actTitle} onChange={e => setActTitle(e.target.value)} required />
              </div>
              <div className="input-group">
                <label>Tipo</label>
                <select value={actType} onChange={e => setActType(e.target.value as any)}>
                  <option value="WALK">Passeio</option>
                  <option value="FEEDING">Alimentação</option>
                  <option value="GENERIC">Geral</option>
                </select>
              </div>
              <div className="input-group">
                <label>Horário</label>
                <input type="time" value={actTime} onChange={e => setActTime(e.target.value)} required />
              </div>
              <div className="input-group">
                <label>Descrição</label>
                <textarea value={actDesc} onChange={e => setActDesc(e.target.value)} rows={3}></textarea>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => setIsActivityModalOpen(false)}>Cancelar</button>
                <button type="submit" className="btn-primary">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Appointment Modal */}
      {isAppointmentModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Agendar Retorno</h3>
              <button className="icon-button" onClick={() => setIsAppointmentModalOpen(false)}>
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <form onSubmit={submitAppointment} className="modal-body">
              <div className="input-group">
                <label>Veterinário</label>
                <input type="text" value={appVet} onChange={e => setAppVet(e.target.value)} required />
              </div>
              <div className="input-group">
                <label>Especialidade</label>
                <input type="text" value={appSpec} onChange={e => setAppSpec(e.target.value)} />
              </div>
              <div className="input-group">
                <label>Clínica</label>
                <input type="text" value={appClinic} onChange={e => setAppClinic(e.target.value)} />
              </div>
              <div className="input-group">
                <label>Horário</label>
                <input type="time" value={appTime} onChange={e => setAppTime(e.target.value)} required />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => setIsAppointmentModalOpen(false)}>Cancelar</button>
                <button type="submit" className="btn-primary">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
export { RoutinePageContent as RoutinePage };
