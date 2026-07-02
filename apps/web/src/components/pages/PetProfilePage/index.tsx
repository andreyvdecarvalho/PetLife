import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { VaccinationsTab } from '../../organisms/VaccinationsTab';
import { useConsultations } from '../../../application/consultation/useConsultations';
import { Modal } from '../../molecules/Modal';
import { ConsultationForm } from '../../organisms/ConsultationForm';
import './styles.css';

interface MedicalRecord {
  id: string;
  title: string;
  category: 'clinical' | 'weight' | 'exam';
  subtitle: string;
  date: string;
  description?: string;
  value?: string;
  trend?: string;
  attachment?: {
    name: string;
    icon: string;
  };
  attachments?: string[];
}

export const PetProfilePageContent: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [activeTab, setActiveTab] = useState<'history' | 'vaccines' | 'medications'>('history');
  const [isModalOpen, setIsModalOpen] = useState(false);

  const { consultations, fetchConsultations } = useConsultations(id || '');

  useEffect(() => {
    if (id) {
      fetchConsultations();
    }
  }, [id, fetchConsultations]);

  const realRecords: MedicalRecord[] = consultations.map(c => ({
    id: c.id,
    title: c.reason,
    category: 'clinical',
    subtitle: `${c.clinic || 'Sem clínica especificada'} - ${c.veterinarian || 'Sem veterinário especificado'}`,
    date: new Date(c.date).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' }),
    description: c.diagnosis ? `Diagnóstico: ${c.diagnosis}${c.prescriptions ? ` | Prescrição: ${c.prescriptions}` : ''}` : c.notes,
    value: c.weightAtVisit ? `${c.weightAtVisit} kg` : undefined,
    attachments: c.attachments,
  }));

  const mockRecords: MedicalRecord[] = [
    {
      id: 'rec-1',
      title: 'Consulta de Rotina',
      category: 'clinical',
      subtitle: 'Clínica Vet Care - Dr. Silva',
      date: '12 Out 2023',
      description: 'Exame geral realizado. Max apresenta ótima saúde cardiovascular e pelagem brilhante. Recomendada continuação da dieta atual.',
      attachment: {
        name: 'Receita.pdf',
        icon: 'description',
      },
    },
    {
      id: 'rec-2',
      title: 'Registro de Peso',
      category: 'weight',
      subtitle: 'Aferição em casa',
      date: '10 Out 2023',
      value: '32.0 kg',
      trend: '- 0.5 kg',
    },
    {
      id: 'rec-3',
      title: 'Exame de Sangue',
      category: 'exam',
      subtitle: 'Laboratório PetLab',
      date: '05 Set 2023',
      description: 'Hemograma completo anual. Todos os índices dentro da normalidade para a raça e idade.',
      attachment: {
        name: 'Resultados.pdf',
        icon: 'lab_profile',
      },
    },
  ];

  const allRecords = [...realRecords, ...mockRecords];

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
                alt="Max the Golden Retriever" 
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
            <button className="pet-profile__new-record-btn" onClick={() => setIsModalOpen(true)}>
              <span className="material-symbols-outlined">add</span>
              Novo Registro Médico
            </button>
          </div>

          <div className="pet-profile__timeline">
            {activeTab === 'history' && allRecords.map(rec => (
              <div key={rec.id} className="pet-profile__timeline-item">
                <div className={`pet-profile__timeline-node pet-profile__timeline-node--${rec.category}`}>
                  <span className="material-symbols-outlined">
                    {rec.category === 'clinical' ? 'local_hospital' : rec.category === 'weight' ? 'scale' : 'science'}
                  </span>
                </div>
                
                <div className="pet-profile__timeline-card">
                  <div className="pet-profile__card-header">
                    <div>
                      <h4 className="pet-profile__card-title">{rec.title}</h4>
                      <p className="pet-profile__card-subtitle">{rec.subtitle}</p>
                    </div>
                    {rec.value ? (
                      <div className="pet-profile__card-value-group">
                        <span className="pet-profile__card-value">{rec.value}</span>
                        {rec.trend && (
                          <span className="pet-profile__card-trend text-tertiary">
                            <span className="material-symbols-outlined">arrow_downward</span>
                            {rec.trend.replace('- ', '')}
                          </span>
                        )}
                      </div>
                    ) : (
                      <span className="pet-profile__card-date">{rec.date}</span>
                    )}
                  </div>
                  
                  {rec.description && (
                    <p className="pet-profile__card-description">{rec.description}</p>
                  )}

                  {rec.attachment && (
                    <div className="pet-profile__card-attachments">
                      <span className="pet-profile__attachment-link">
                        <span className="material-symbols-outlined">{rec.attachment.icon}</span>
                        {rec.attachment.name}
                      </span>
                    </div>
                  )}

                  {rec.attachments && rec.attachments.length > 0 && (
                    <div className="pet-profile__card-attachments">
                      {rec.attachments.map((url, idx) => {
                        const name = url.substring(url.lastIndexOf('/') + 1).replace(/^[0-9a-f-]+_/, '');
                        const isImg = url.toLowerCase().match(/\.(png|jpg|jpeg)$/);
                        return (
                          <a key={idx} href={url} target="_blank" rel="noopener noreferrer" className="pet-profile__attachment-link">
                            <span className="material-symbols-outlined">{isImg ? 'image' : 'description'}</span>
                            {name}
                          </a>
                        );
                      })}
                    </div>
                  )}
                </div>
              </div>
            ))}

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
              fetchConsultations();
            }}
            onCancel={() => setIsModalOpen(false)}
          />
        )}
      </Modal>
    </div>
  );
};
export { PetProfilePageContent as PetProfilePage };
