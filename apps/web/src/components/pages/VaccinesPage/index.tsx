import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useMedications } from '../../../application/medications/useMedications';
import './styles.css';

export const VaccinesPage: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const { pets, fetchPets } = useGetPets();
  const [selectedPetId, setSelectedPetId] = useState<string | null>(null);

  const {
    medications, fetchMedications,
    createMedication, stopMedication
  } = useMedications(selectedPetId || '');

  const [isFormOpen, setIsFormOpen] = useState(false);
  
  const [newMedicationType, setNewMedicationType] = useState<string>('VACCINE');
  const [newName, setNewName] = useState('');
  const [newDosage, setNewDosage] = useState('1 dose');
  const [newStartDate, setNewStartDate] = useState(new Date().toISOString().substring(0, 10));
  const [newEndDate, setNewEndDate] = useState(''); // Próxima dose

  useEffect(() => { fetchPets(); }, [fetchPets]);

  useEffect(() => {
    if (pets.length > 0 && !selectedPetId) {
      setSelectedPetId(pets[0].id);
    }
  }, [pets, selectedPetId]);

  useEffect(() => {
    if (selectedPetId) {
      fetchMedications();
    }
  }, [selectedPetId, fetchMedications]);

  const handleCreateRecord = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newName) {
      showToast('Por favor, preencha o nome.', 'error');
      return;
    }

    const payload = {
      name: newName,
      dosage: newDosage || '1 dose',
      frequency: 'ONCE' as const,
      medicationType: newMedicationType,
      startDate: newStartDate,
      endDate: newEndDate || undefined,
      timesOfDay: ['08:00'], // default just to satisfy API
    };

    const success = await createMedication(payload);
    if (success) {
      showToast('Registro adicionado com sucesso! ✨', 'success');
      setIsFormOpen(false);
      setNewName(''); setNewMedicationType('VACCINE'); setNewStartDate(new Date().toISOString().substring(0, 10)); setNewEndDate('');
    }
  };

  const targetRecords = medications.filter(m => (m.medicationType === 'VACCINE' || m.medicationType === 'DEWORMER') && m.status !== 'CANCELLED');

  return (
    <div className="vaccines-page animate-fade-in">
      <header className="vaccines-page__header">
        <button className="vaccines-page__back-btn" onClick={() => navigate('/')}>
          <span className="material-symbols-outlined">arrow_back</span>
        </button>
        <h1 className="vaccines-page__title">Vacinas e Vermífugos</h1>
        <button className="vaccines-page__add-btn" onClick={() => setIsFormOpen(true)} aria-label="Adicionar registro">
          <span className="material-symbols-outlined icon-fill">add</span>
        </button>
      </header>

      <main className="vaccines-page__main">
        <div className="vaccines-page__pet-select-container">
          <label htmlFor="pet-select" className="vaccines-page__label">Selecione o Pet:</label>
          <select
            id="pet-select"
            className="vaccines-page__pet-select"
            value={selectedPetId || ''}
            onChange={(e) => setSelectedPetId(e.target.value)}
          >
            <option value="" disabled>Selecione um Pet</option>
            {pets.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>
        </div>

        <section className="vaccines-page__section">
          <div className="vaccines-page__section-header">
            <h2 className="vaccines-page__section-title">Carteirinha de Imunização</h2>
            <span className="vaccines-page__badge">
              {targetRecords.length} Registros
            </span>
          </div>

          <div className="vaccines-page__list">
            {targetRecords.length === 0 ? (
              <p className="vaccines-page__empty">Nenhum registro encontrado para este pet.</p>
            ) : (
              targetRecords
                .sort((a, b) => new Date(b.startDate).getTime() - new Date(a.startDate).getTime())
                .map(m => (
                <article key={m.id} className="vaccines-page__card">
                  <div className={`vaccines-page__card-accent vaccines-page__card-accent--${m.medicationType?.toLowerCase()}`}></div>
                  <div className="vaccines-page__card-header">
                    <div className="vaccines-page__card-info">
                      <h3>
                        {m.medicationType === 'VACCINE' ? '💉 ' : '🐛 '}
                        {m.name}
                      </h3>
                      <p className="vaccines-page__date-info">
                        <strong>Aplicado em:</strong> {new Date(m.startDate).toLocaleDateString('pt-BR')}
                      </p>
                      {m.endDate && (
                        <p className="vaccines-page__date-info vaccines-page__date-info--next">
                          <strong>Próxima dose:</strong> {new Date(m.endDate).toLocaleDateString('pt-BR')}
                        </p>
                      )}
                    </div>
                    <button 
                      className="vaccines-page__delete-btn" 
                      onClick={async () => { 
                        if(window.confirm('Excluir este registro?')) { 
                          const ok = await stopMedication(m.id); 
                          if(ok) showToast('Registro excluído.', 'info'); 
                        } 
                      }}
                    >
                      <span className="material-symbols-outlined">delete</span>
                    </button>
                  </div>
                </article>
              ))
            )}
          </div>
        </section>
      </main>

      {isFormOpen && (
        <div className="vaccines-page__modal-overlay">
          <div className="vaccines-page__modal">
            <h2 className="vaccines-page__modal-title">Novo Registro</h2>
            <form onSubmit={handleCreateRecord} className="vaccines-page__form">
              <div className="vaccines-page__form-field">
                <label htmlFor="vac-type">Tipo</label>
                <select id="vac-type" value={newMedicationType} onChange={e => setNewMedicationType(e.target.value)}>
                  <option value="VACCINE">Vacina</option>
                  <option value="DEWORMER">Vermífugo</option>
                </select>
              </div>
              <div className="vaccines-page__form-field">
                <label htmlFor="vac-name">Nome (ex: V10, Antirrábica)</label>
                <input id="vac-name" type="text" value={newName} onChange={e => setNewName(e.target.value)} required />
              </div>
              <div className="vaccines-page__form-field">
                <label htmlFor="vac-dosage">Dosagem</label>
                <input id="vac-dosage" type="text" value={newDosage} onChange={e => setNewDosage(e.target.value)} required placeholder="Ex: 1 dose, 1 comprimido" />
              </div>
              <div className="vaccines-page__form-row">
                <div className="vaccines-page__form-field">
                  <label htmlFor="vac-start-date">Data da Aplicação</label>
                  <input id="vac-start-date" type="date" value={newStartDate} onChange={e => setNewStartDate(e.target.value)} required />
                </div>
                <div className="vaccines-page__form-field">
                  <label htmlFor="vac-end-date">Próxima Dose (opcional)</label>
                  <input id="vac-end-date" type="date" value={newEndDate} onChange={e => setNewEndDate(e.target.value)} />
                </div>
              </div>
              
              <div className="vaccines-page__form-actions">
                <button type="button" className="vaccines-page__btn-secondary" onClick={() => setIsFormOpen(false)}>Cancelar</button>
                <button type="submit" className="vaccines-page__btn-primary">Confirmar</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
