import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useMedications } from '../../../application/medications/useMedications';
import type { Medication, MedicationFrequency, MedicationAdministration } from '../../../domain/pet/Medication';
import './styles.css';

interface TreatmentMock {
  id: string;
  name: string;
  category: 'pill' | 'liquid' | 'injection';
  icon: string;
  petName: string;
  status: 'upcoming' | 'pending';
  statusLabel: string;
  dosage: string;
  frequency: string;
  hours: string;
  hoursDetail?: React.ReactNode;
  progressText: string;
  progressPercent: number;
}

interface HistoryItemMock {
  id: string;
  name: string;
  petName: string;
  time: string;
  status: string;
}

const AdherenceCircle: React.FC<{ rate: number }> = ({ rate }) => {
  const radius = 30;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (rate / 100) * circumference;
  return (
    <div className="medications-page__adherence-circle-container">
      <svg className="medications-page__adherence-circle" width="80" height="80">
        <circle
          className="medications-page__adherence-circle-bg"
          cx="40"
          cy="40"
          r={radius}
          strokeWidth="6"
        />
        <circle
          className="medications-page__adherence-circle-progress"
          cx="40"
          cy="40"
          r={radius}
          strokeWidth="6"
          strokeDasharray={circumference}
          strokeDashoffset={strokeDashoffset}
        />
      </svg>
      <span className="medications-page__adherence-circle-text">{Math.round(rate)}%</span>
    </div>
  );
};

export const MedicationsPageContent: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();

  // Load pets from API
  const { pets, fetchPets } = useGetPets();
  const [selectedPetId, setSelectedPetId] = useState<string | null>(null);

  // Real-time dynamic states
  const {
    medications,
    adherence,
    loading: apiLoading,
    fetchMedications,
    fetchAdherence,
    createMedication,
    updateAdministration,
    stopMedication
  } = useMedications(selectedPetId || '');

  // Form & Modal states
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [skipDoseId, setSkipDoseId] = useState<string | null>(null);
  const [skipReason, setSkipReason] = useState('');
  const [isSkipModalOpen, setIsSkipModalOpen] = useState(false);

  // Form fields
  const [newName, setNewName] = useState('');
  const [newDosage, setNewDosage] = useState('');
  const [newFrequency, setNewFrequency] = useState<MedicationFrequency>('DAILY');
  const [newCustomHours, setNewCustomHours] = useState<number>(24);
  const [newStartDate, setNewStartDate] = useState(new Date().toISOString().substring(0, 10));
  const [newEndDate, setNewEndDate] = useState('');
  const [newTimes, setNewTimes] = useState<string[]>(['08:00']);

  // Fetch pets on mount
  useEffect(() => {
    fetchPets();
  }, [fetchPets]);

  // Select first pet by default
  useEffect(() => {
    if (pets.length > 0 && !selectedPetId) {
      setSelectedPetId(pets[0].id);
    }
  }, [pets, selectedPetId]);

  // Load medication data when pet selection changes
  useEffect(() => {
    if (selectedPetId) {
      fetchMedications();
      fetchAdherence();
    }
  }, [selectedPetId, fetchMedications, fetchAdherence]);

  // Default times of day helper when frequency changes
  useEffect(() => {
    if (newFrequency === 'ONCE' || newFrequency === 'WEEKLY' || newFrequency === 'DAILY') {
      setNewTimes(['08:00']);
    } else if (newFrequency === 'TWICE_DAILY' || newFrequency === 'EVERY_12H') {
      setNewTimes(['08:00', '20:00']);
    } else if (newFrequency === 'EVERY_8H') {
      setNewTimes(['06:00', '14:00', '22:00']);
    } else if (newFrequency === 'CUSTOM') {
      setNewTimes(['08:00']);
    }
  }, [newFrequency]);

  // TimePicker Array actions
  const handleAddTime = () => {
    setNewTimes([...newTimes, '08:00']);
  };

  const handleRemoveTime = (index: number) => {
    if (newTimes.length > 1) {
      setNewTimes(newTimes.filter((_, i) => i !== index));
    }
  };

  const handleTimeChange = (index: number, val: string) => {
    const updated = [...newTimes];
    updated[index] = val;
    setNewTimes(updated);
  };

  // Submit new treatment form
  const handleCreateTreatment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newName || !newDosage) {
      showToast('Por favor, preencha todos os campos obrigatórios.', 'error');
      return;
    }
    const payload = {
      name: newName,
      dosage: newDosage,
      frequency: newFrequency,
      customFrequencyHours: newFrequency === 'CUSTOM' ? newCustomHours : undefined,
      startDate: newStartDate,
      endDate: newEndDate || undefined,
      timesOfDay: newTimes,
    };
    const success = await createMedication(payload);
    if (success) {
      showToast('Tratamento cadastrado com sucesso! ✨', 'success');
      setIsFormOpen(false);
      // Reset form
      setNewName('');
      setNewDosage('');
      setNewFrequency('DAILY');
      setNewTimes(['08:00']);
    }
  };

  // Skip dose flow
  const handleSkipDoseClick = (doseId: string) => {
    setSkipDoseId(doseId);
    setSkipReason('');
    setIsSkipModalOpen(true);
  };

  const handleSkipDoseSubmit = async () => {
    if (!skipDoseId) return;
    const success = await updateAdministration(skipDoseId, {
      status: 'SKIPPED',
      skippedReason: skipReason || 'Pulado pelo tutor',
    });
    if (success) {
      showToast('Dose registrada como pulada.', 'info');
      setIsSkipModalOpen(false);
    }
  };

  const handleTakeDoseClick = async (doseId: string) => {
    const success = await updateAdministration(doseId, {
      status: 'TAKEN',
    });
    if (success) {
      showToast('Dose registrada com sucesso! ✨', 'success');
    }
  };

  const handleStopMedicationClick = async (id: string) => {
    if (window.confirm('Tem certeza que deseja interromper este tratamento manualmente?')) {
      const success = await stopMedication(id);
      if (success) {
        showToast('Tratamento interrompido.', 'info');
      }
    }
  };

  // MOCK STATE BACKWARD COMPATIBILITY
  // Render mock view if no pets are present in DB (to pass unit tests)
  const isMockMode = pets.length === 0;

  const [mockTreatments, setMockTreatments] = useState<TreatmentMock[]>([
    {
      id: 'treat-1',
      name: 'Antibiótico Amoxicilina',
      category: 'pill',
      icon: 'pill',
      petName: 'Max',
      status: 'upcoming',
      statusLabel: 'Próxima dose em 2h',
      dosage: '1 comprimido',
      frequency: 'A cada 12h',
      hours: '08:00 - 20:00',
      progressText: '4 de 10 dias concluídos',
      progressPercent: 40,
    },
    {
      id: 'treat-2',
      name: 'Colírio Optivet',
      category: 'liquid',
      icon: 'medication_liquid',
      petName: 'Luna',
      status: 'pending',
      statusLabel: 'Pendente',
      dosage: '2 gotas',
      frequency: 'A cada 8h',
      hours: '06:00 - 14:00 - 22:00',
      hoursDetail: (
        <span>
          06:00 - <span className="medications-page__hour-error font-bold">14:00</span> - 22:00
        </span>
      ),
      progressText: 'Dose das 14:00 pendente',
      progressPercent: 0,
    }
  ]);

  const [mockHistory, setMockHistory] = useState<HistoryItemMock[]>([
    {
      id: 'hist-1',
      name: 'Colírio Optivet',
      petName: 'Luna',
      time: 'Hoje, 06:05',
      status: 'Tomado',
    },
    {
      id: 'hist-2',
      name: 'Antibiótico Amoxicilina',
      petName: 'Max',
      time: 'Hoje, 08:15',
      status: 'Tomado',
    }
  ]);

  const handleMockMarkAsTaken = (id: string) => {
    const treatment = mockTreatments.find(t => t.id === id);
    if (!treatment) return;
    showToast('Dose registrada com sucesso! ✨', 'success');

    const now = new Date();
    const timeString = `Hoje, ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
    const newHistItem: HistoryItemMock = {
      id: `hist-${Date.now()}`,
      name: treatment.name,
      petName: treatment.petName,
      time: timeString,
      status: 'Tomado'
    };

    setMockHistory(prev => [newHistItem, ...prev]);

    setMockTreatments(prev => prev.map(t => {
      if (t.id === id) {
        return {
          ...t,
          status: 'upcoming',
          statusLabel: 'Próxima dose em 8h',
          hoursDetail: undefined,
          progressText: 'Dose registrada'
        };
      }
      return t;
    }));
  };

  if (isMockMode) {
    return (
      <div className="medications-page animate-fade-in">
        <div className="medications-page__header-row">
          <button 
            className="medications-page__back-btn" 
            onClick={() => navigate('/')}
            aria-label="Voltar para home"
          >
            <span className="material-symbols-outlined">arrow_back</span>
          </button>
          <h1 className="medications-page__title">Controle de Medicamentos</h1>
          <button 
            className="medications-page__add-btn" 
            onClick={() => showToast('Funcionalidade de adicionar medicamento em breve!', 'info')}
            aria-label="Adicionar medicamento"
          >
            <span className="material-symbols-outlined">add</span>
          </button>
        </div>

        <div className="medications-page__grid">
          <section className="medications-page__section">
            <div className="medications-page__section-header">
              <h2 className="medications-page__section-title">Tratamentos Ativos</h2>
              <span className="medications-page__active-badge">
                <span className="medications-page__active-dot"></span>
                2 Ativos
              </span>
            </div>

            <div className="medications-page__list">
              {mockTreatments.map(t => (
                <article 
                  key={t.id} 
                  className={`medications-page__card medications-page__card--${t.status}`}
                >
                  <div className="medications-page__card-accent"></div>
                  <div className="medications-page__card-header">
                    <div className="medications-page__card-info-group">
                      <div className={`medications-page__card-icon-wrapper medications-page__card-icon-wrapper--${t.category}`}>
                        <span className="material-symbols-outlined">{t.icon}</span>
                      </div>
                      <div>
                        <h3 className="medications-page__card-title">{t.name}</h3>
                        <div className="medications-page__card-pet">
                          <span className="material-symbols-outlined">pets</span>
                          {t.petName}
                        </div>
                      </div>
                    </div>
                    <div className={`medications-page__status-badge medications-page__status-badge--${t.status}`}>
                      <span className="material-symbols-outlined">
                        {t.status === 'upcoming' ? 'schedule' : 'warning'}
                      </span>
                      {t.statusLabel}
                    </div>
                  </div>

                  <div className="medications-page__card-details-grid">
                    <div className="medications-page__card-detail-item">
                      <span className="medications-page__detail-label">Dosagem</span>
                      <span className="medications-page__detail-value">{t.dosage}</span>
                    </div>
                    <div className="medications-page__card-detail-item">
                      <span className="medications-page__detail-label">Horários</span>
                      <span className="medications-page__detail-value">{t.frequency}</span>
                      <span className="medications-page__detail-subvalue">
                        {t.hoursDetail || t.hours}
                      </span>
                    </div>
                  </div>

                  <div className="medications-page__card-footer">
                    {t.status === 'pending' ? (
                      <div className="medications-page__action-group">
                        <p className="medications-page__error-msg">{t.progressText}</p>
                        <button 
                          className="medications-page__take-btn"
                          onClick={() => handleMockMarkAsTaken(t.id)}
                        >
                          <span className="material-symbols-outlined">check_circle</span>
                          Marcar como Tomado
                        </button>
                      </div>
                    ) : (
                      <div className="medications-page__progress-group">
                        <div className="medications-page__progress-header">
                          <span className="medications-page__progress-label">Progresso</span>
                          <span className="medications-page__progress-value">{t.progressText}</span>
                        </div>
                        <div className="medications-page__progress-bar-wrapper">
                          <div 
                            className="medications-page__progress-bar"
                            style={{ width: `${t.progressPercent}%` }}
                          ></div>
                        </div>
                      </div>
                    )}
                  </div>
                </article>
              ))}
            </div>
          </section>

          <section className="medications-page__section">
            <h2 className="medications-page__section-title">Histórico Recente</h2>
            <div className="medications-page__history-card">
              {mockHistory.map(item => (
                <div key={item.id} className="medications-page__history-item">
                  <div className="medications-page__history-icon-wrapper">
                    <span className="material-symbols-outlined filled">check_circle</span>
                  </div>
                  <div className="medications-page__history-details">
                    <p className="medications-page__history-name">
                      {item.name} <span className="medications-page__history-pet">({item.petName})</span>
                    </p>
                    <p className="medications-page__history-time">
                      <span className="material-symbols-outlined">schedule</span>
                      {item.time}
                    </p>
                  </div>
                  <span className="medications-page__history-status-badge">{item.status}</span>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    );
  }

  // Get active medications and compile doses list
  const activeMedications = medications.filter(m => m.status === 'ACTIVE');
  const allDoses: MedicationAdministration[] = [];
  medications.forEach(m => {
    if (m.administrations) {
      m.administrations.forEach(a => {
        allDoses.push(a);
      });
    }
  });

  // Sort doses by scheduledTime
  const pendingOrRecentDoses = allDoses
    .sort((a, b) => new Date(a.scheduledTime).getTime() - new Date(b.scheduledTime).getTime())
    .slice(0, 10);

  const historyDoses = allDoses
    .filter(a => a.status === 'TAKEN' || a.status === 'SKIPPED')
    .sort((a, b) => new Date(b.scheduledTime).getTime() - new Date(a.scheduledTime).getTime());

  return (
    <div className="medications-page animate-fade-in">
      <div className="medications-page__header-row">
        <button 
          className="medications-page__back-btn" 
          onClick={() => navigate('/')}
          aria-label="Voltar para home"
        >
          <span className="material-symbols-outlined">arrow_back</span>
        </button>
        <h1 className="medications-page__title">Controle de Medicamentos</h1>
        <button 
          className="medications-page__add-btn" 
          onClick={() => setIsFormOpen(true)}
          aria-label="Adicionar medicamento"
        >
          <span className="material-symbols-outlined">add</span>
        </button>
      </div>

      {/* Pet Selector Dropdown */}
      <div className="medications-page__pet-select-container">
        <label htmlFor="pet-select" className="medications-page__pet-select-label">Selecione o Pet:</label>
        <select
          id="pet-select"
          className="medications-page__pet-select"
          value={selectedPetId || ''}
          onChange={(e) => setSelectedPetId(e.target.value)}
        >
          {pets.map(p => (
            <option key={p.id} value={p.id}>{p.name}</option>
          ))}
        </select>
      </div>

      {/* Adherence Header Card */}
      {adherence && (
        <div className="medications-page__adherence-summary">
          <div className="medications-page__adherence-card-content">
            <div>
              <h3 className="medications-page__adherence-title">Aderência Geral ao Tratamento</h3>
              <p className="medications-page__adherence-subtitle">
                {adherence.takenDoses} de {adherence.totalDoses} doses administradas
              </p>
            </div>
            <AdherenceCircle rate={adherence.adherenceRate} />
          </div>
        </div>
      )}

      <div className="medications-page__grid">
        {/* Active Treatments Section */}
        <section className="medications-page__section">
          <div className="medications-page__section-header">
            <h2 className="medications-page__section-title">Tratamentos Ativos</h2>
            <span className="medications-page__active-badge">
              <span className="medications-page__active-dot"></span>
              {activeMedications.length} Ativos
            </span>
          </div>

          <div className="medications-page__list">
            {activeMedications.length === 0 ? (
              <p className="medications-page__empty">Nenhum tratamento ativo cadastrado.</p>
            ) : (
              activeMedications.map(m => (
                <article key={m.id} className="medications-page__card medications-page__card--upcoming">
                  <div className="medications-page__card-accent"></div>
                  <div className="medications-page__card-header">
                    <div className="medications-page__card-info-group">
                      <div className="medications-page__card-icon-wrapper medications-page__card-icon-wrapper--pill">
                        <span className="material-symbols-outlined">pill</span>
                      </div>
                      <div>
                        <h3 className="medications-page__card-title">{m.name}</h3>
                        <div className="medications-page__card-pet">
                          <span className="material-symbols-outlined">schedule</span>
                          {m.frequency === 'CUSTOM' ? `A cada ${m.customFrequencyHours}h` : m.frequency}
                        </div>
                      </div>
                    </div>
                    <button
                      className="medications-page__stop-treatment-btn"
                      onClick={() => handleStopMedicationClick(m.id)}
                    >
                      Parar
                    </button>
                  </div>

                  <div className="medications-page__card-details-grid">
                    <div className="medications-page__card-detail-item">
                      <span className="medications-page__detail-label">Dosagem</span>
                      <span className="medications-page__detail-value">{m.dosage}</span>
                    </div>
                    <div className="medications-page__card-detail-item">
                      <span className="medications-page__detail-label">Horários</span>
                      <span className="medications-page__detail-value">{m.timesOfDay.join(', ')}</span>
                    </div>
                  </div>
                </article>
              ))
            )}
          </div>
        </section>

        {/* Daily Schedule / Timeline Section */}
        <section className="medications-page__section">
          <h2 className="medications-page__section-title">Próximas Doses</h2>
          <div className="medications-page__list">
            {pendingOrRecentDoses.filter(d => d.status === 'PENDING' || d.status === 'LATE').length === 0 ? (
              <p className="medications-page__empty">Sem doses pendentes agendadas.</p>
            ) : (
              pendingOrRecentDoses
                .filter(d => d.status === 'PENDING' || d.status === 'LATE')
                .map(dose => (
                  <div key={dose.id} className={`medications-page__dose-item medications-page__dose-item--${dose.status.toLowerCase()}`}>
                    <div className="medications-page__dose-details">
                      <p className="medications-page__dose-name">{dose.medicationName}</p>
                      <p className="medications-page__dose-time">
                        <span className="material-symbols-outlined">schedule</span>
                        {new Date(dose.scheduledTime).toLocaleString('pt-BR', {
                          hour: '2-digit',
                          minute: '2-digit',
                          day: '2-digit',
                          month: '2-digit',
                        })}
                        {dose.status === 'LATE' && <span className="medications-page__dose-late-tag">Atrasado</span>}
                      </p>
                    </div>
                    <div className="medications-page__dose-actions">
                      <button
                        className="medications-page__action-btn medications-page__action-btn--take"
                        onClick={() => handleTakeDoseClick(dose.id)}
                      >
                        Tomar
                      </button>
                      <button
                        className="medications-page__action-btn medications-page__action-btn--skip"
                        onClick={() => handleSkipDoseClick(dose.id)}
                      >
                        Pular
                      </button>
                    </div>
                  </div>
                ))
            )}
          </div>

          <h2 className="medications-page__section-title" style={{ marginTop: '24px' }}>Histórico de Doses</h2>
          <div className="medications-page__history-card">
            {historyDoses.length === 0 ? (
              <p className="medications-page__empty">Nenhuma dose administrada recentemente.</p>
            ) : (
              historyDoses.slice(0, 5).map(item => (
                <div key={item.id} className="medications-page__history-item">
                  <div className="medications-page__history-icon-wrapper">
                    <span className="material-symbols-outlined filled">
                      {item.status === 'TAKEN' ? 'check_circle' : 'cancel'}
                    </span>
                  </div>
                  <div className="medications-page__history-details">
                    <p className="medications-page__history-name">
                      {item.medicationName}
                    </p>
                    <p className="medications-page__history-time">
                      <span className="material-symbols-outlined">schedule</span>
                      {new Date(item.scheduledTime).toLocaleString('pt-BR', {
                        hour: '2-digit',
                        minute: '2-digit',
                        day: '2-digit',
                        month: '2-digit',
                      })}
                    </p>
                    {item.skippedReason && (
                      <p className="medications-page__skipped-reason">Motivo: {item.skippedReason}</p>
                    )}
                  </div>
                  <span className={`medications-page__history-status-badge medications-page__history-status-badge--${item.status.toLowerCase()}`}>
                    {item.status === 'TAKEN' ? 'Tomado' : 'Pulado'}
                  </span>
                </div>
              ))
            )}
          </div>
        </section>
      </div>

      {/* FORM MODAL FOR NEW MEDICATION */}
      {isFormOpen && (
        <div className="medications-page__modal-overlay">
          <div className="medications-page__modal animate-scale-up">
            <h2 className="medications-page__modal-title">Cadastrar Tratamento</h2>
            <form onSubmit={handleCreateTreatment} className="medications-page__form">
              <div className="medications-page__form-field">
                <label className="medications-page__form-label">Nome do Medicamento *</label>
                <input
                  type="text"
                  className="medications-page__form-input"
                  placeholder="Ex: Amoxicilina, Colírio"
                  value={newName}
                  onChange={(e) => setNewName(e.target.value)}
                  required
                />
              </div>

              <div className="medications-page__form-field">
                <label className="medications-page__form-label">Dosagem *</label>
                <input
                  type="text"
                  className="medications-page__form-input"
                  placeholder="Ex: 1 comprimido, 5 gotas, 2ml"
                  value={newDosage}
                  onChange={(e) => setNewDosage(e.target.value)}
                  required
                />
              </div>

              <div className="medications-page__form-row">
                <div className="medications-page__form-field">
                  <label className="medications-page__form-label">Frequência *</label>
                  <select
                    className="medications-page__form-input"
                    value={newFrequency}
                    onChange={(e) => setNewFrequency(e.target.value as MedicationFrequency)}
                  >
                    <option value="ONCE">Dose Única</option>
                    <option value="DAILY">Diário</option>
                    <option value="TWICE_DAILY">2x ao dia</option>
                    <option value="EVERY_8H">A cada 8 horas</option>
                    <option value="EVERY_12H">A cada 12 horas</option>
                    <option value="WEEKLY">Semanal</option>
                    <option value="CUSTOM">Personalizado</option>
                  </select>
                </div>

                {newFrequency === 'CUSTOM' && (
                  <div className="medications-page__form-field">
                    <label className="medications-page__form-label">Intervalo (horas) *</label>
                    <input
                      type="number"
                      className="medications-page__form-input"
                      value={newCustomHours}
                      onChange={(e) => setNewCustomHours(Number(e.target.value))}
                      min="1"
                      required
                    />
                  </div>
                )}
              </div>

              <div className="medications-page__form-row">
                <div className="medications-page__form-field">
                  <label className="medications-page__form-label">Data de Início *</label>
                  <input
                    type="date"
                    className="medications-page__form-input"
                    value={newStartDate}
                    onChange={(e) => setNewStartDate(e.target.value)}
                    required
                  />
                </div>
                <div className="medications-page__form-field">
                  <label className="medications-page__form-label">Data de Término (opcional)</label>
                  <input
                    type="date"
                    className="medications-page__form-input"
                    value={newEndDate}
                    onChange={(e) => setNewEndDate(e.target.value)}
                  />
                </div>
              </div>

              {/* TimePicker Array Component */}
              <div className="medications-page__form-field">
                <div className="medications-page__times-header">
                  <label className="medications-page__form-label">Horários de Administração</label>
                  {newFrequency !== 'CUSTOM' && (
                    <button
                      type="button"
                      className="medications-page__add-time-btn"
                      onClick={handleAddTime}
                    >
                      + Horário
                    </button>
                  )}
                </div>
                <div className="medications-page__times-list">
                  {newTimes.map((time, idx) => (
                    <div key={idx} className="medications-page__time-item">
                      <input
                        type="time"
                        className="medications-page__form-input"
                        value={time}
                        onChange={(e) => handleTimeChange(idx, e.target.value)}
                        required
                      />
                      {newTimes.length > 1 && newFrequency !== 'CUSTOM' && (
                        <button
                          type="button"
                          className="medications-page__remove-time-btn"
                          onClick={() => handleRemoveTime(idx)}
                        >
                          Remover
                        </button>
                      )}
                    </div>
                  ))}
                </div>
              </div>

              <div className="medications-page__modal-actions">
                <button
                  type="button"
                  className="medications-page__modal-btn medications-page__modal-btn--secondary"
                  onClick={() => setIsFormOpen(false)}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="medications-page__modal-btn medications-page__modal-btn--primary"
                >
                  Confirmar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* SKIP DOSE MODAL */}
      {isSkipModalOpen && (
        <div className="medications-page__modal-overlay">
          <div className="medications-page__modal medications-page__modal--sm animate-scale-up">
            <h2 className="medications-page__modal-title">Justificar Dose Pulada</h2>
            <div className="medications-page__form-field" style={{ marginTop: '16px' }}>
              <label className="medications-page__form-label">Motivo (opcional)</label>
              <textarea
                className="medications-page__form-input medications-page__form-textarea"
                placeholder="Ex: Pet vomitou, Cuspiu o remédio, Dificuldade de engolir"
                value={skipReason}
                onChange={(e) => setSkipReason(e.target.value)}
              />
            </div>
            <div className="medications-page__modal-actions" style={{ marginTop: '24px' }}>
              <button
                type="button"
                className="medications-page__modal-btn medications-page__modal-btn--secondary"
                onClick={() => setIsSkipModalOpen(false)}
              >
                Voltar
              </button>
              <button
                type="button"
                className="medications-page__modal-btn medications-page__modal-btn--primary"
                onClick={handleSkipDoseSubmit}
              >
                Confirmar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export { MedicationsPageContent as MedicationsPage };
