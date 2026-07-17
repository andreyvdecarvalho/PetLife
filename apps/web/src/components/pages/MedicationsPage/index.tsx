import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useMedications } from '../../../application/medications/useMedications';
import type { MedicationFrequency, MedicationAdministration } from '../../../domain/pet/Medication';
import { FormField } from '../../molecules/FormField';
import { Button } from '../../atoms/Button';
import './styles.css';

const AdherenceCircle: React.FC<{ rate: number }> = ({ rate }) => {
  const radius = 30;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (rate / 100) * circumference;
  return (
    <div className="medications-page__adherence-circle-container">
      <svg className="medications-page__adherence-circle" width="80" height="80">
        <circle className="medications-page__adherence-circle-bg" cx="40" cy="40" r={radius} strokeWidth="6" />
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

export const MedicationsPage: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const { pets, fetchPets } = useGetPets();
  const [selectedPetId, setSelectedPetId] = useState<string | null>(null);

  const {
    medications, adherence, fetchMedications, fetchAdherence,
    createMedication, updateAdministration, stopMedication
  } = useMedications(selectedPetId || '');

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [skipDoseId, setSkipDoseId] = useState<string | null>(null);
  const [skipReason, setSkipReason] = useState('');
  const [isSkipModalOpen, setIsSkipModalOpen] = useState(false);

  const [newName, setNewName] = useState('');
  const [newMedicationType, setNewMedicationType] = useState<string>('MEDICINE');
  const [newDosage, setNewDosage] = useState('');
  const [newFrequency, setNewFrequency] = useState<MedicationFrequency>('DAILY');
  const [newCustomHours, setNewCustomHours] = useState<number>(24);
  const [newStartDate, setNewStartDate] = useState(new Date().toISOString().substring(0, 10));
  const [newDurationDays, setNewDurationDays] = useState<number | ''>('');
  const [newTimes, setNewTimes] = useState<string[]>(['08:00']);

  useEffect(() => { fetchPets(); }, [fetchPets]);

  useEffect(() => {
    if (pets.length > 0 && !selectedPetId) {
      setSelectedPetId(pets[0].id);
    }
  }, [pets, selectedPetId]);

  useEffect(() => {
    if (selectedPetId) {
      fetchMedications();
      fetchAdherence();
    }
  }, [selectedPetId, fetchMedications, fetchAdherence]);

  useEffect(() => {
    if (['ONCE', 'WEEKLY', 'DAILY', 'CUSTOM'].includes(newFrequency)) setNewTimes(['08:00']);
    else if (['TWICE_DAILY', 'EVERY_12H'].includes(newFrequency)) setNewTimes(['08:00', '20:00']);
    else if (newFrequency === 'EVERY_8H') setNewTimes(['06:00', '14:00', '22:00']);
  }, [newFrequency]);

  const handleCreateTreatment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newName || !newDosage) {
      showToast('Por favor, preencha todos os campos obrigatórios.', 'error');
      return;
    }
    
    let computedEndDate = undefined;
    if (newDurationDays && Number(newDurationDays) > 0) {
      const sDate = new Date(newStartDate);
      sDate.setUTCDate(sDate.getUTCDate() + Number(newDurationDays));
      computedEndDate = sDate.toISOString().split('T')[0];
    }

    const payload = {
      name: newName, dosage: newDosage, frequency: newFrequency,
      medicationType: newMedicationType,
      customFrequencyHours: newFrequency === 'CUSTOM' ? newCustomHours : undefined,
      startDate: newStartDate, endDate: computedEndDate, timesOfDay: newTimes,
    };
    const success = await createMedication(payload);
    if (success) {
      showToast('Tratamento cadastrado com sucesso! ✨', 'success');
      setIsFormOpen(false);
      setNewName(''); setNewDosage(''); setNewFrequency('DAILY'); setNewMedicationType('MEDICINE'); setNewTimes(['08:00']); setNewDurationDays('');
    }
  };

  const handleSkipDoseSubmit = async () => {
    if (!skipDoseId) return;
    const success = await updateAdministration(skipDoseId, { status: 'SKIPPED', skippedReason: skipReason || 'Pulado pelo tutor' });
    if (success) {
      showToast('Dose registrada como pulada.', 'info');
      setIsSkipModalOpen(false);
    }
  };

  const targetMedications = medications.filter(m => m.medicationType === 'MEDICINE' || m.medicationType === 'VITAMIN' || !m.medicationType);
  const activeMedications = targetMedications.filter(m => m.status === 'ACTIVE');
  const allDoses: MedicationAdministration[] = [];
  targetMedications.forEach(m => m.administrations?.forEach(a => allDoses.push(a)));

  const pendingOrRecentDoses = allDoses
    .sort((a, b) => new Date(a.scheduledTime).getTime() - new Date(b.scheduledTime).getTime())
    .slice(0, 10);

  const historyDoses = allDoses
    .filter(a => a.status === 'TAKEN' || a.status === 'SKIPPED')
    .sort((a, b) => new Date(b.scheduledTime).getTime() - new Date(a.scheduledTime).getTime());

  return (
    <div className="medications-page">
      <header className="medications-page__header">
        <button className="medications-page__back-btn" onClick={() => navigate('/')} data-testid="btn-back">
          <span className="material-symbols-outlined">arrow_back</span>
        </button>
        <h1 className="medications-page__title">Controle de Medicamentos</h1>
        <button className="medications-page__add-btn" onClick={() => setIsFormOpen(true)} data-testid="btn-add" aria-label="Adicionar medicamento">
          <span className="material-symbols-outlined icon-fill">add</span>
        </button>
      </header>

      <main className="medications-page__main">
        <div className="medications-page__pet-select-container">
          <label htmlFor="pet-select" className="medications-page__label">Selecione o Pet:</label>
          <select
            id="pet-select"
            className="medications-page__pet-select"
            value={selectedPetId || ''}
            onChange={(e) => setSelectedPetId(e.target.value)}
            data-testid="input-pet-select"
          >
            <option value="" disabled>Selecione um Pet</option>
            {pets.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>
        </div>

        {adherence && (
          <div className="medications-page__adherence">
            <div className="medications-page__adherence-info">
              <h3>Aderência Geral ao Tratamento</h3>
              <p>{adherence.takenDoses} de {adherence.totalDoses} doses administradas</p>
            </div>
            <AdherenceCircle rate={adherence.adherenceRate} />
          </div>
        )}

        <section className="medications-page__section">
          <div className="medications-page__section-header">
            <h2 className="medications-page__section-title">Tratamentos Ativos</h2>
            <span className="medications-page__badge">
              <span className="medications-page__badge-dot"></span> {activeMedications.length} Ativos
            </span>
          </div>
          <div className="medications-page__list">
            {activeMedications.map(m => (
              <article key={m.id} className="medications-page__card">
                <div className="medications-page__card-accent medications-page__card-accent--secondary"></div>
                <div className="medications-page__card-header">
                  <div className="medications-page__card-icon-wrapper">
                    <span className="material-symbols-outlined">pill</span>
                  </div>
                  <div className="medications-page__card-info">
                    <h3>
                      {m.name}
                      {m.medicationType && (
                        <span className={`medications-page__type-badge medications-page__type-badge--${m.medicationType.toLowerCase()}`}>
                          {m.medicationType === 'VITAMIN' ? '💊 Vitamina' : '💊 Remédio'}
                        </span>
                      )}
                    </h3>
                    <p><span className="material-symbols-outlined">schedule</span> {m.frequency}</p>
                  </div>
                  <button className="medications-page__stop-btn" onClick={async () => { if(window.confirm('Tem certeza que deseja interromper este tratamento manualmente?')) { const ok = await stopMedication(m.id); if(ok) showToast('Tratamento interrompido.', 'info'); } }} data-testid={`btn-stop-${m.id}`}>Parar</button>
                </div>
                <div className="medications-page__card-details">
                  <div><p className="medications-page__label">Dosagem</p><p>{m.dosage}</p></div>
                  <div><p className="medications-page__label">Horários</p><p>{m.timesOfDay.join(', ')}</p></div>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="medications-page__section">
          <h2 className="medications-page__section-title">Próximas Doses</h2>
          <div className="medications-page__list">
            {pendingOrRecentDoses.filter(d => d.status === 'PENDING' || d.status === 'LATE').map(dose => (
              <div key={dose.id} className="medications-page__dose-item">
                <div className="medications-page__dose-info">
                  <p>{dose.medicationName}</p>
                  <p className="medications-page__dose-time">
                    <span className="material-symbols-outlined">schedule</span>
                    {new Date(dose.scheduledTime).toLocaleTimeString('pt-BR', {hour:'2-digit', minute:'2-digit'})}
                    {dose.status === 'LATE' && <span className="medications-page__late-tag">Atrasado</span>}
                  </p>
                </div>
                <div className="medications-page__dose-actions">
                  <button className="medications-page__btn-take" onClick={async () => { const ok = await updateAdministration(dose.id, {status: 'TAKEN'}); if(ok) showToast('Dose registrada com sucesso! ✨', 'success'); }} data-testid={`btn-take-${dose.id}`}>Tomar</button>
                  <button className="medications-page__btn-skip" onClick={() => {setSkipDoseId(dose.id); setIsSkipModalOpen(true);}} data-testid={`btn-skip-${dose.id}`}>Pular</button>
                </div>
              </div>
            ))}
          </div>
        </section>
      </main>

      {isFormOpen && (
        <div className="medications-page__modal-overlay">
          <div className="medications-page__modal">
            <h2 className="medications-page__modal-title">Cadastrar Tratamento</h2>
            <form onSubmit={handleCreateTreatment} className="medications-page__form">
              <div className="molecule-form-field">
                <label htmlFor="med-type" className="atom-label">Tipo</label>
                <select id="med-type" className="atom-input" value={newMedicationType} onChange={e => setNewMedicationType(e.target.value)} data-testid="input-medication-type">
                  <option value="VITAMIN">Vitamina</option>
                  <option value="MEDICINE">Remédio</option>
                </select>
              </div>
              <FormField
                id="med-name"
                label="Nome do Medicamento"
                type="text"
                value={newName}
                onChange={e => setNewName(e.target.value)}
                required
                data-testid="input-name"
              />
              <FormField
                id="med-dosage"
                label="Dosagem"
                type="text"
                value={newDosage}
                onChange={e => setNewDosage(e.target.value)}
                required
                data-testid="input-dosage"
              />
              <div className="medications-page__form-row">
                <FormField
                  id="med-start-date"
                  label="Data de início"
                  type="date"
                  value={newStartDate}
                  onChange={e => setNewStartDate(e.target.value)}
                  required
                  data-testid="input-start-date"
                />
                <FormField
                  id="med-duration"
                  label="Duração do tratamento (dias)"
                  type="number"
                  placeholder="Ex: 7"
                  min="1"
                  value={newDurationDays}
                  onChange={e => setNewDurationDays(e.target.value ? Number(e.target.value) : '')}
                  data-testid="input-duration"
                />
              </div>
              <div className="medications-page__form-row">
                <div className="molecule-form-field">
                  <label htmlFor="med-frequency" className="atom-label">Frequência</label>
                  <select id="med-frequency" className="atom-input" value={newFrequency} onChange={e => setNewFrequency(e.target.value as any)} data-testid="input-frequency">
                    <option value="DAILY">Diário</option>
                    <option value="TWICE_DAILY">2x ao dia</option>
                    <option value="EVERY_8H">A cada 8 horas</option>
                    <option value="CUSTOM">Personalizado</option>
                  </select>
                </div>
                {newFrequency === 'CUSTOM' && (
                  <FormField
                    id="med-custom-hours"
                    label="Horas"
                    type="number"
                    value={newCustomHours}
                    onChange={e => setNewCustomHours(Number(e.target.value))}
                    required
                    data-testid="input-custom-hours"
                  />
                )}
              </div>
              <div className="medications-page__form-actions">
                <Button type="button" variant="secondary" onClick={() => setIsFormOpen(false)} data-testid="btn-cancel">Cancelar</Button>
                <Button type="submit" data-testid="btn-submit">Confirmar</Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isSkipModalOpen && (
        <div className="medications-page__modal-overlay">
          <div className="medications-page__modal">
            <h2 className="medications-page__modal-title">Justificar Dose Pulada</h2>
            <div className="molecule-form-field">
              <label htmlFor="skip-reason" className="atom-label">Motivo</label>
              <textarea id="skip-reason" className="atom-input" placeholder="Ex: pet vomitou" value={skipReason} onChange={e => setSkipReason(e.target.value)} data-testid="input-reason" />
            </div>
            <div className="medications-page__form-actions">
              <Button type="button" variant="secondary" onClick={() => setIsSkipModalOpen(false)}>Cancelar</Button>
              <Button type="button" onClick={handleSkipDoseSubmit} data-testid="btn-confirm-skip">Confirmar</Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
