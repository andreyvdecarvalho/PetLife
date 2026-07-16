import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useGrooming } from '../../../application/grooming/useGrooming';
import { useGetPets } from '../../../application/pet/useGetPets';
import type { Pet } from '../../../domain/pet/Pet';
import type { Grooming } from '../../../domain/pet/Grooming';
import { BeforeAfterViewer } from '../../molecules/BeforeAfterViewer';
import { GroomingForm } from '../../organisms/GroomingForm';
import { Modal } from '../../molecules/Modal';
import { useToast } from '../../molecules/Toast';
import './styles.css';

export const GroomingPageContent: React.FC = () => {
  const { petId: urlPetId } = useParams<{ petId: string }>();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const { pets, fetchPets } = useGetPets();
  const [selectedPetId, setSelectedPetId] = useState<string | null>(urlPetId || null);
  const [selectedGrooming, setSelectedGrooming] = useState<Grooming | undefined>(undefined);
  const [isFormOpen, setIsFormOpen] = useState(false);

  useEffect(() => { fetchPets(); }, [fetchPets]);

  useEffect(() => {
    if (pets.length > 0 && !selectedPetId) {
      setSelectedPetId(pets[0].id);
    }
  }, [pets, selectedPetId]);

  const {
    groomings,
    loading: groomingLoading,
    error: groomingError,
    fetchGroomings,
    addGrooming,
    updateGrooming,
    uploadPhoto,
  } = useGrooming(selectedPetId || '');

  useEffect(() => {
    if (selectedPetId) {
      fetchGroomings();
    }
  }, [selectedPetId, fetchGroomings]);

  // Sort groomings from newest to oldest
  const sortedGroomings = [...groomings].sort(
    (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
  );

  const lastGrooming = sortedGroomings[0];

  // Calculate next grooming date
  let nextGroomingDate: string | undefined = undefined;
  let daysRemaining: number | undefined = undefined;

  if (lastGrooming) {
    if (lastGrooming.nextDate) {
      nextGroomingDate = lastGrooming.nextDate;
    } else if (lastGrooming.frequencyDays) {
      const lastDateObj = new Date(lastGrooming.date);
      const nextDateObj = new Date(lastDateObj.getTime() + lastGrooming.frequencyDays * 24 * 60 * 60 * 1000);
      nextGroomingDate = nextDateObj.toISOString().split('T')[0];
    }

    if (nextGroomingDate) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const targetDate = new Date(nextGroomingDate);
      targetDate.setHours(0, 0, 0, 0);
      const diffTime = targetDate.getTime() - today.getTime();
      daysRemaining = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    }
  }

  const handleOpenCreateModal = () => {
    setSelectedGrooming(undefined);
    setIsFormOpen(true);
  };

  const handleOpenEditModal = (grooming: Grooming) => {
    setSelectedGrooming(grooming);
    setIsFormOpen(true);
  };

  const handleFormSuccess = async (
    values: any,
    beforeFile: File | null,
    afterFile: File | null
  ) => {
    try {
      let savedGrooming: Grooming | null = null;
      if (selectedGrooming) {
        const success = await updateGrooming(selectedGrooming.id, values);
        if (success) {
          savedGrooming = { ...selectedGrooming, ...values };
          showToast('Serviço de estética atualizado com sucesso! ✨', 'success');
        }
      } else {
        savedGrooming = await addGrooming(values);
        if (savedGrooming) {
          showToast('Serviço de estética registrado com sucesso! ✨', 'success');
        }
      }

      if (savedGrooming) {
        if (beforeFile) {
          await uploadPhoto(savedGrooming.id, beforeFile, 'BEFORE');
        }
        if (afterFile) {
          await uploadPhoto(savedGrooming.id, afterFile, 'AFTER');
        }
      }
      setIsFormOpen(false);
    } catch (e) {
      showToast('Erro ao salvar serviço de banho e tosa.', 'error');
    }
  };

  return (
    <div className="page-grooming animate-fade-in">
      {/* Header */}
      <div className="page-grooming__header-row">
        <button 
          className="page-grooming__back-btn" 
          onClick={() => navigate('/')}
          aria-label="Voltar para o painel"
          data-testid="btn-back-to-profile"
        >
          <span className="material-symbols-outlined">arrow_back</span>
        </button>
        <h2 className="page-grooming__title">Banho & Tosa</h2>
      </div>
      
      <div className="page-grooming__pet-select-container" style={{ margin: '0 24px' }}>
        <label htmlFor="pet-select" className="page-grooming__label">Selecione o Pet:</label>
        <select
          id="pet-select"
          className="page-grooming__pet-select"
          style={{ width: '100%', padding: '12px', borderRadius: '12px', border: '1px solid var(--color-outline-variant)' }}
          value={selectedPetId || ''}
          onChange={(e) => setSelectedPetId(e.target.value)}
          data-testid="input-pet-select"
        >
          <option value="" disabled>Selecione um Pet</option>
          {pets.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
        </select>
      </div>

      <div className="page-grooming__content-grid">
        {/* Adherence/Next Grooming Info Card */}
        <section className="page-grooming__highlight-card" data-testid="highlight-card">
          <div className="page-grooming__highlight-header">
            <span className="material-symbols-outlined icon-shower">shower</span>
            <div>
              <h3 className="page-grooming__highlight-title">Próximo Banho & Tosa</h3>
              <p className="page-grooming__highlight-subtitle">
                {nextGroomingDate
                  ? `Agendado para: ${new Date(nextGroomingDate).toLocaleDateString('pt-BR')}`
                  : 'Periodicidade não configurada'}
              </p>
            </div>
          </div>

          {daysRemaining !== undefined && (
            <div className="page-grooming__days-badge-container">
              {daysRemaining < 0 ? (
                <div className="page-grooming__days-badge error" data-testid="badge-status">
                  <span className="material-symbols-outlined">warning</span>
                  <span>Atrasado há {Math.abs(daysRemaining)} {Math.abs(daysRemaining) === 1 ? 'dia' : 'dias'}</span>
                </div>
              ) : daysRemaining === 0 ? (
                <div className="page-grooming__days-badge warning" data-testid="badge-status">
                  <span className="material-symbols-outlined">event</span>
                  <span>Hoje! 🧼</span>
                </div>
              ) : (
                <div className="page-grooming__days-badge success" data-testid="badge-status">
                  <span className="material-symbols-outlined">schedule</span>
                  <span>Em {daysRemaining} {daysRemaining === 1 ? 'dia' : 'dias'}</span>
                </div>
              )}
            </div>
          )}

          {lastGrooming?.frequencyDays && (
            <p className="page-grooming__frequency-text">
              Refazer a cada <strong>{lastGrooming.frequencyDays} dias</strong>
            </p>
          )}
        </section>

        {/* Historical List */}
        <section className="page-grooming__history-section">
          <div className="page-grooming__actions-row">
            <h3 className="page-grooming__section-heading">Histórico de Estética</h3>
            <button 
              className="page-grooming__new-btn" 
              onClick={handleOpenCreateModal}
              data-testid="btn-open-grooming-form"
            >
              <span className="material-symbols-outlined">add</span>
              Novo Banho/Tosa
            </button>
          </div>

          {groomingLoading && <p className="page-grooming__status-message">Carregando histórico...</p>}
          {groomingError && <p className="page-grooming__error-message">{groomingError}</p>}

          {!groomingLoading && sortedGroomings.length === 0 && (
            <div className="page-grooming__empty-state">
              <span className="material-symbols-outlined">pets</span>
              <p>Nenhum registro de banho e tosa encontrado. Registre o primeiro! 🐾</p>
            </div>
          )}

          <div className="page-grooming__list">
            {!groomingLoading && sortedGroomings.map((g) => {
              const beforePhoto = g.photos?.[0];
              const afterPhoto = g.photos?.[1];

              return (
                <div key={g.id} className="page-grooming__card" data-testid="grooming-card">
                  <div className="page-grooming__card-header">
                    <div>
                      <span className="page-grooming__card-badge">
                        {g.type === 'BATH' ? 'Banho' : g.type === 'GROOMING' ? 'Tosa' : 'Banho & Tosa'}
                      </span>
                      <h4 className="page-grooming__card-date">
                        {new Date(g.date).toLocaleDateString('pt-BR', { timeZone: 'UTC' })}
                      </h4>
                    </div>

                    <div className="page-grooming__card-actions">
                      <button
                        className="page-grooming__edit-action-btn"
                        onClick={() => handleOpenEditModal(g)}
                        title="Editar"
                        data-testid={`btn-edit-${g.id}`}
                      >
                        <span className="material-symbols-outlined">edit</span>
                      </button>
                    </div>
                  </div>

                  <div className="page-grooming__card-body">
                    {g.provider && (
                      <p className="page-grooming__card-meta">
                        <span className="material-symbols-outlined">storefront</span>
                        <span>{g.provider}</span>
                      </p>
                    )}
                    {g.cost !== undefined && (
                      <p className="page-grooming__card-meta">
                        <span className="material-symbols-outlined">payments</span>
                        <span>R$ {g.cost.toFixed(2)}</span>
                      </p>
                    )}
                    {g.notes && <p className="page-grooming__card-notes">{g.notes}</p>}

                    {/* Compare Photos container */}
                    {beforePhoto && afterPhoto ? (
                      <div className="page-grooming__viewer-wrapper">
                        <BeforeAfterViewer beforeUrl={beforePhoto} afterUrl={afterPhoto} />
                      </div>
                    ) : (
                      (beforePhoto || afterPhoto) && (
                        <div className="page-grooming__single-photo-wrapper">
                          <img 
                            src={beforePhoto || afterPhoto} 
                            alt="Foto do pet" 
                            className="page-grooming__single-photo" 
                          />
                          <span className="page-grooming__single-photo-tag">
                            {beforePhoto ? 'Antes' : 'Depois'}
                          </span>
                        </div>
                      )
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </section>
      </div>

      {/* Modal Form */}
      <Modal 
        isOpen={isFormOpen} 
        onClose={() => setIsFormOpen(false)} 
        title={selectedGrooming ? 'Editar Banho e Tosa' : 'Registrar Banho e Tosa'}
      >
        <GroomingForm
          grooming={selectedGrooming}
          onSuccess={handleFormSuccess}
          onCancel={() => setIsFormOpen(false)}
        />
      </Modal>
    </div>
  );
};
