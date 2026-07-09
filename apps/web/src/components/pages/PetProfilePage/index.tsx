import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { VaccinationsTab } from '../../organisms/VaccinationsTab';
import { Modal } from '../../molecules/Modal';
import { ConsultationForm } from '../../organisms/ConsultationForm';
import { useTimeline } from '../../../application/pet/useTimeline';
import { useExportMedicalPass } from '../../../application/pet/useExportMedicalPass';
import { useToast } from '../../molecules/Toast';
import { useAuth } from '../../../contexts/AuthContext';
import type { TimelineEventType } from '../../../domain/pet/Timeline';
import './styles.css';

const FILTER_OPTIONS = [
  { label: 'Todos', value: null },
  { label: 'Vacinas', value: ['VACCINE'] as TimelineEventType[] },
  { label: 'Consultas', value: ['CONSULTATION'] as TimelineEventType[] },
  { label: 'Medicamentos', value: ['MEDICATION_START', 'MEDICATION_END'] as TimelineEventType[] },
  { label: 'Estética', value: ['GROOMING'] as TimelineEventType[] },
  { label: 'Peso', value: ['WEIGHT'] as TimelineEventType[] },
];

export const PetProfilePageContent: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [activeTab, setActiveTab] = useState<'history' | 'vaccines' | 'medications'>('history');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedFilter, setSelectedFilter] = useState<TimelineEventType[] | null>(null);
  const [page, setPage] = useState(0);

  const { user } = useAuth();
  const { showToast } = useToast();
  const { events, isLoading, error: timelineError, hasMore, fetchTimeline } = useTimeline();
  const { isExporting, exportError, exportMedicalPass } = useExportMedicalPass();

  const handleRefresh = React.useCallback(() => {
    if (id) {
      fetchTimeline(id, selectedFilter || undefined, 0, 20, false);
      setPage(0);
    }
  }, [id, selectedFilter, fetchTimeline]);

  useEffect(() => {
    if (activeTab === 'history') {
      handleRefresh();
    }
  }, [activeTab, handleRefresh]);

  useEffect(() => {
    if (timelineError) {
      showToast(timelineError, 'error');
    }
  }, [timelineError, showToast]);

  useEffect(() => {
    if (exportError) {
      showToast(exportError, 'error');
    }
  }, [exportError, showToast]);

  const handleLoadMore = () => {
    if (id) {
      const nextPage = page + 1;
      fetchTimeline(id, selectedFilter || undefined, nextPage, 20, true);
      setPage(nextPage);
    }
  };

  const handleExport = async () => {
    if (!id) return;
    const success = await exportMedicalPass(id);
    if (success) {
      showToast('Prontuário exportado com sucesso!', 'success');
    }
  };

  return (
    <div className="pet-profile animate-fade-in">
      <div className="pet-profile__header-row">
        <button 
          className="pet-profile__back-btn" 
          onClick={() => navigate('/')}
          aria-label="Voltar para home"
        >
          <span className="material-symbols-outlined">arrow_back</span>
        </button>
        <h2 className="pet-profile__title">Perfil do Pet</h2>
      </div>

      <div className="pet-profile__grid">
        <aside className="pet-profile__aside">
          <div className="pet-profile__card">
            <div className="pet-profile__card-blob"></div>
            
            <div className="pet-profile__avatar-container">
              <img 
                alt="Max" 
                className="pet-profile__avatar" 
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuB84xkd7Mi62jZm1xSkIdQ7bSTD9bf2WPHQaNnroEICjqKLPzaBUtaj61a0fWiA_LTm64QiOiCyrQyvZWs7g5819q2etrJfYfmndaJOtCyMXAZZa2G04m3q-laOEp6PGum3rozS-zQmKYFA_U-W7vRzehxWJhfqQj4lFemuWXMULK2fMiVlBNdOBUC8sJflJK2LalIbDXjO7LsLhj2oQL_p-8I97iS5LuRrbOHkFsL_dU07YCUTKprrurY0QOW8aBnRDtP9lBs98as" 
              />
              <div className="pet-profile__favorite-badge">
                <span className="material-symbols-outlined filled">favorite</span>
              </div>
            </div>

            <h3 className="pet-profile__pet-name">Max</h3>
            <p className="pet-profile__pet-breed">Golden Retriever</p>

            <div className="pet-profile__stats-row">
              <div className="pet-profile__stat-box">
                <span className="pet-profile__stat-label">Idade</span>
                <span className="pet-profile__stat-value">3 Anos</span>
              </div>
              <div className="pet-profile__stat-box">
                <span className="pet-profile__stat-label">Peso</span>
                <span className="pet-profile__stat-value">32 kg</span>
              </div>
              <div className="pet-profile__stat-box">
                <span className="pet-profile__stat-label">Sexo</span>
                <span className="pet-profile__stat-value">Macho</span>
              </div>
            </div>
          </div>

          <div className="pet-profile__status-banner">
            <div className="pet-profile__status-icon-wrapper">
              <span className="material-symbols-outlined">check_circle</span>
            </div>
            <div className="pet-profile__status-info">
              <h4 className="pet-profile__status-title">Saúde em Dia</h4>
              <p className="pet-profile__status-desc">Próxima vacina em 4 meses</p>
            </div>
          </div>

          <button
            className="pet-profile__quick-action-btn"
            onClick={() => navigate(`/pets/${id}/grooming`)}
            style={{
              marginTop: '16px',
              width: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px',
              padding: '12px',
              backgroundColor: 'var(--color-primary-fixed, #ffdbcb)',
              color: 'var(--color-on-primary-fixed, #351000)',
              border: 'none',
              borderRadius: 'var(--radius-full, 100px)',
              fontFamily: 'var(--font-label, sans-serif)',
              fontWeight: 600,
              cursor: 'pointer',
              transition: 'background-color 0.2s',
            }}
            data-testid="btn-to-grooming"
          >
            <span className="material-symbols-outlined">shower</span>
            Banho & Tosa
          </button>
        </aside>

        <section className="pet-profile__content">
          <div className="pet-profile__tabs no-scrollbar">
            <button 
              className={`pet-profile__tab ${activeTab === 'history' ? 'active' : ''}`}
              onClick={() => setActiveTab('history')}
            >
              Histórico
            </button>
            <button 
              className={`pet-profile__tab ${activeTab === 'vaccines' ? 'active' : ''}`}
              onClick={() => setActiveTab('vaccines')}
            >
              Vacinas
            </button>
            <button 
              className={`pet-profile__tab ${activeTab === 'medications' ? 'active' : ''}`}
              onClick={() => setActiveTab('medications')}
            >
              Medicamentos
            </button>
          </div>

          <div className="pet-profile__actions-row">
            <h3 className="pet-profile__section-heading">Registros Recentes</h3>
            <div className="pet-profile__buttons-group">
              <button 
                className="pet-profile__export-btn" 
                onClick={handleExport}
                disabled={isExporting}
                data-testid="export-pdf-button"
              >
                <span className="material-symbols-outlined">{isExporting ? 'sync' : 'download'}</span>
                {isExporting ? 'Exportando...' : 'Exportar Prontuário'}
              </button>
              <button className="pet-profile__new-record-btn" onClick={() => setIsModalOpen(true)}>
                <span className="material-symbols-outlined">add</span>
                Novo Registro Médico
              </button>
            </div>
          </div>

          {activeTab === 'history' && (
            <div className="pet-profile__filter-chips no-scrollbar">
              {FILTER_OPTIONS.map(opt => (
                <button
                  key={opt.label}
                  className={`pet-profile__filter-chip ${selectedFilter === opt.value ? 'active' : ''}`}
                  onClick={() => setSelectedFilter(opt.value)}
                  data-testid={`filter-chip-${opt.label}`}
                >
                  {opt.label}
                </button>
              ))}
            </div>
          )}

          <div className="pet-profile__timeline">
            {activeTab === 'history' && isLoading && page === 0 && (
              <div className="pet-profile__empty-tab">
                <span className="material-symbols-outlined className='animate-spin'">sync</span>
                <p>Carregando registros...</p>
              </div>
            )}

            {activeTab === 'history' && !isLoading && events.length === 0 && (
              <div className="pet-profile__empty-tab">
                <span className="material-symbols-outlined">history</span>
                <p>Nenhum registro encontrado.</p>
              </div>
            )}

            {activeTab === 'history' && events.map((event, idx) => {
              const dateStr = event.date 
                ? new Date(event.date).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' })
                : 'N/A';
              return (
                <div key={event.id || `${event.type}-${idx}`} className="pet-profile__timeline-item">
                  <div 
                    className="pet-profile__timeline-node" 
                    style={{ 
                      backgroundColor: event.color, 
                      color: '#ffffff', 
                      borderColor: 'var(--color-surface)' 
                    }}
                    data-testid={`timeline-node-${event.type}`}
                  >
                    <span className="material-symbols-outlined">{event.icon}</span>
                  </div>
                  
                  <div className="pet-profile__timeline-card">
                    <div className="pet-profile__card-header">
                      <div>
                        <h4 className="pet-profile__card-title">{event.title}</h4>
                        <p className="pet-profile__card-subtitle">
                          {event.type === 'BIRTHDAY' ? 'Aniversário' : 
                           event.type === 'VACCINE' ? 'Vacina' :
                           event.type === 'CONSULTATION' ? 'Consulta' :
                           event.type === 'GROOMING' ? 'Estética' :
                           event.type === 'WEIGHT' ? 'Peso' :
                           event.type === 'MEDICATION_START' ? 'Início de Medicamento' :
                           event.type === 'MEDICATION_END' ? 'Fim de Medicamento' : event.type}
                        </p>
                      </div>
                      <span className="pet-profile__card-date">{dateStr}</span>
                    </div>
                    
                    {event.description && (
                      <p className="pet-profile__card-description">{event.description}</p>
                    )}

                    {event.photoUrl && (
                      <div className="pet-profile__card-attachments">
                        <a href={event.photoUrl} target="_blank" rel="noopener noreferrer" className="pet-profile__attachment-link">
                          <span className="material-symbols-outlined">image</span>
                          Visualizar Comprovante
                        </a>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}

            {activeTab === 'history' && hasMore && events.length > 0 && (
              <div className="pet-profile__load-more-container">
                <button 
                  className="pet-profile__load-more-btn"
                  onClick={handleLoadMore}
                  disabled={isLoading}
                  data-testid="load-more-button"
                >
                  {isLoading ? 'Carregando...' : 'Carregar mais'}
                </button>
              </div>
            )}

            {activeTab === 'vaccines' && id && (
              <VaccinationsTab petId={id} species="DOG" />
            )}

            {activeTab === 'medications' && (
              <div className="pet-profile__empty-tab">
                <span className="material-symbols-outlined">pill</span>
                <p>Nenhum medicamento ativo no momento.</p>
              </div>
            )}
          </div>
        </section>
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Registrar Consulta Médica">
        {id && (
          <ConsultationForm
            petId={id}
            onSuccess={() => {
              setIsModalOpen(false);
              handleRefresh();
            }}
            onCancel={() => setIsModalOpen(false)}
          />
        )}
      </Modal>
    </div>
  );
};
export { PetProfilePageContent as PetProfilePage };
