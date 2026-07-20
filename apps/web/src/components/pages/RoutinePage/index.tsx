import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useRoutineActivities } from '../../../application/routine/useRoutineActivities';
import { useMedications } from '../../../application/medications/useMedications';
import { useConsultations } from '../../../application/consultation/useConsultations';
import { useGrooming } from '../../../application/grooming/useGrooming';
import { useVaccinations } from '../../../application/vaccination/useVaccinations';
import { FormField } from '../../molecules/FormField';
import { Button } from '../../atoms/Button';
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
  const { vaccinations, fetchVaccinations } = useVaccinations(selectedPetId || '');

  const [activities, setActivities] = useState<ConsolidatedActivity[]>([]);

  const [isActivityModalOpen, setIsActivityModalOpen] = useState(false);
  const [isAppointmentModalOpen, setIsAppointmentModalOpen] = useState(false);
  const [isGroomingModalOpen, setIsGroomingModalOpen] = useState(false);
  const [isMedicationModalOpen, setIsMedicationModalOpen] = useState(false);

  const [schedMedId, setSchedMedId] = useState('');
  const [schedMedDate, setSchedMedDate] = useState(new Date().toISOString().split('T')[0]);
  const [schedMedTime, setSchedMedTime] = useState('08:00');
  const [schedMedNotes, setSchedMedNotes] = useState('');

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
      fetchVaccinations();
    }
  }, [selectedPetId, selectedDate, fetchActivities, fetchMedications, fetchConsultations, fetchGroomings, fetchVaccinations]);

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
      const cDate = new Date(c.date);
      const cDateStr = cDate.toISOString().split('T')[0];
      if (cDateStr === dateStr) {
        const timeStr = cDate.toISOString().split('T')[1].substring(0, 5);
        cons.push({ id: `cons-${c.id}`, sourceId: c.id, title: `Consulta com ${c.veterinarian || 'Veterinário'}`, time: timeStr, description: c.reason || 'Clínico Geral', status: cDate >= new Date() ? 'scheduled' : 'completed', type: 'consultation', source: 'consultation' });
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

    vaccinations.forEach(v => {
      const vDateStr = new Date(v.date).toISOString().split('T')[0];
      if (vDateStr === dateStr) {
        cons.push({ id: `vac-${v.id}`, sourceId: v.id, title: `Vacina: ${v.name}`, time: '09:00', description: v.veterinarian || 'Clínica', status: v.status === 'ADMINISTERED' ? 'completed' : 'scheduled', type: 'generic', source: 'medication' });
      }
    });

    cons.sort((a, b) => a.time.localeCompare(b.time));
    setActivities(cons);
  }, [rawActivities, medications, consultations, groomings, vaccinations, selectedDate]);

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
    const isoDate = new Date(`${dateStr}T${appTime}:00`).toISOString();
    await addConsultation({ veterinarian: appVet, reason: appSpec || 'Consulta Geral', clinic: appClinic, date: isoDate, notes: '' });
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

  const submitMedicationSchedule = async (e: React.FormEvent) => {
    e.preventDefault();
    const selectedMed = medications.find(m => m.id === schedMedId);
    if (!selectedMed) return;
    await addActivity({
      title: `Medicamento: ${selectedMed.name}`,
      type: 'GENERIC' as any, // tipo genérico para compatibilidade
      activityDate: schedMedDate,
      activityTime: schedMedTime + ':00',
      description: `Dose: ${selectedMed.dosage}` + (schedMedNotes ? ` — ${schedMedNotes}` : ''),
      status: 'PENDING'
    });
    setIsMedicationModalOpen(false);
    showToast('Medicamento agendado na rotina! 💊', 'success');
    fetchActivities(schedMedDate);
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

              <button className="routine-page__action-card" onClick={() => setIsMedicationModalOpen(true)} data-testid="btn-add-medication-routine">
                <div className="routine-page__action-icon routine-page__action-icon--secondary">
                  <span className="material-symbols-outlined">medication</span>
                </div>
                <div className="routine-page__action-text">
                  <span className="routine-page__action-title">Agendar Medicamento</span>
                  <span className="routine-page__action-desc">Registrar dose ou lembrete de remédio.</span>
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
              <FormField
                id="act-title"
                label="Título"
                type="text"
                value={actTitle}
                onChange={e => setActTitle(e.target.value)}
                required
                data-testid="input-act-title"
              />
              <div className="routine-page__form-row">
                <div className="molecule-form-field">
                  <label htmlFor="act-type" className="atom-label">Tipo</label>
                  <select id="act-type" className="atom-input" value={actType} onChange={e => setActType(e.target.value as any)} data-testid="input-act-type">
                    <option value="WALK">Passeio</option>
                    <option value="FEEDING">Alimentação</option>
                    <option value="GENERIC">Geral</option>
                  </select>
                </div>
                <FormField
                  id="act-time"
                  label="Horário"
                  type="time"
                  value={actTime}
                  onChange={e => setActTime(e.target.value)}
                  required
                  data-testid="input-act-time"
                />
              </div>
              <div className="molecule-form-field">
                <label htmlFor="act-desc" className="atom-label">Descrição</label>
                <textarea id="act-desc" className="atom-input" value={actDesc} onChange={e => setActDesc(e.target.value)} rows={3} data-testid="input-act-desc" />
              </div>
              <div className="routine-page__form-actions">
                <Button type="button" variant="secondary" onClick={() => setIsActivityModalOpen(false)}>Cancelar</Button>
                <Button type="submit">Salvar</Button>
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
              <FormField
                id="app-vet"
                label="Veterinário"
                type="text"
                value={appVet}
                onChange={e => setAppVet(e.target.value)}
                required
                data-testid="input-app-vet"
              />
              <FormField
                id="app-spec"
                label="Especialidade"
                type="text"
                value={appSpec}
                onChange={e => setAppSpec(e.target.value)}
                data-testid="input-app-spec"
              />
              <div className="routine-page__form-row">
                <FormField
                  id="app-clinic"
                  label="Clínica"
                  type="text"
                  value={appClinic}
                  onChange={e => setAppClinic(e.target.value)}
                  data-testid="input-app-clinic"
                />
                <FormField
                  id="app-time"
                  label="Horário"
                  type="time"
                  value={appTime}
                  onChange={e => setAppTime(e.target.value)}
                  required
                  data-testid="input-app-time"
                />
              </div>
              <div className="routine-page__form-actions">
                <Button type="button" variant="secondary" onClick={() => setIsAppointmentModalOpen(false)}>Cancelar</Button>
                <Button type="submit">Salvar</Button>
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
              <FormField
                id="groom-provider"
                label="PetShop / Local"
                type="text"
                value={groomProvider}
                onChange={e => setGroomProvider(e.target.value)}
                required
                data-testid="input-groom-provider"
              />
              <div className="routine-page__form-row">
                <div className="molecule-form-field">
                  <label htmlFor="groom-type" className="atom-label">Tipo de Serviço</label>
                  <select id="groom-type" className="atom-input" value={groomType} onChange={e => setGroomType(e.target.value as any)} data-testid="input-groom-type">
                    <option value="BATH">Banho</option>
                    <option value="GROOMING">Tosa</option>
                    <option value="BATH_AND_GROOMING">Banho & Tosa</option>
                  </select>
                </div>
                <FormField
                  id="groom-time"
                  label="Horário"
                  type="time"
                  value={groomTime}
                  onChange={e => setGroomTime(e.target.value)}
                  required
                  data-testid="input-groom-time"
                />
              </div>
              <div className="routine-page__form-actions">
                <Button type="button" variant="secondary" onClick={() => setIsGroomingModalOpen(false)}>Cancelar</Button>
                <Button type="submit">Salvar</Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isMedicationModalOpen && (
        <div className="routine-page__modal-overlay">
          <div className="routine-page__modal">
            <h2 className="routine-page__modal-title">Agendar Medicamento</h2>
            <form onSubmit={submitMedicationSchedule} className="routine-page__form">
              <div className="molecule-form-field">
                <label htmlFor="sched-med" className="atom-label">Medicamento</label>
                <select id="sched-med" className="atom-input" value={schedMedId} onChange={e => setSchedMedId(e.target.value)} required data-testid="input-sched-med">
                  <option value="">Selecione um medicamento ativo</option>
                  {medications.filter(m => m.status === 'ACTIVE').map(m => (
                    <option key={m.id} value={m.id}>{m.name} ({m.dosage})</option>
                  ))}
                </select>
              </div>
              <div className="routine-page__form-row">
                <FormField
                  id="sched-date"
                  label="Data"
                  type="date"
                  value={schedMedDate}
                  onChange={e => setSchedMedDate(e.target.value)}
                  required
                  data-testid="input-sched-date"
                />
                <FormField
                  id="sched-time"
                  label="Horário"
                  type="time"
                  value={schedMedTime}
                  onChange={e => setSchedMedTime(e.target.value)}
                  required
                  data-testid="input-sched-time"
                />
              </div>
              <div className="molecule-form-field">
                <label htmlFor="sched-notes" className="atom-label">Observações (opcional)</label>
                <textarea id="sched-notes" className="atom-input" value={schedMedNotes} onChange={e => setSchedMedNotes(e.target.value)} rows={2} data-testid="input-sched-notes" placeholder="Ex: administrar com alimento" />
              </div>
              <div className="routine-page__form-actions">
                <Button type="button" variant="secondary" onClick={() => setIsMedicationModalOpen(false)}>Cancelar</Button>
                <Button type="submit" data-testid="btn-confirm-sched-med">Agendar</Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
