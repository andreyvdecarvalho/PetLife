import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import './styles.css';

interface Treatment {
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

interface HistoryItem {
  id: string;
  name: string;
  petName: string;
  time: string;
  status: string;
}

export const MedicationsPageContent: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  
  const [treatments, setTreatments] = useState<Treatment[]>([
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

  const [history, setHistory] = useState<HistoryItem[]>([
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

  const handleMarkAsTaken = (id: string) => {
    const treatment = treatments.find(t => t.id === id);
    if (!treatment) return;

    // Show success feedback
    showToast('Dose registrada com sucesso! ✨', 'success');

    // Add to history
    const now = new Date();
    const timeString = `Hoje, ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
    const newHistItem: HistoryItem = {
      id: `hist-${Date.now()}`,
      name: treatment.name,
      petName: treatment.petName,
      time: timeString,
      status: 'Tomado'
    };

    setHistory(prev => [newHistItem, ...prev]);

    // Update treatment status or remove if finished
    setTreatments(prev => prev.map(t => {
      if (t.id === id) {
        return {
          ...t,
          status: 'upcoming',
          statusLabel: 'Próxima dose em 8h',
          hoursDetail: undefined, // restore normal hours view
          progressText: 'Dose registrada'
        };
      }
      return t;
    }));
  };

  const activeCount = treatments.length;

  return (
    <div className="medications-page animate-fade-in">
      {/* TopAppBar inside template content for mobile back btn action */}
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
        {/* Active Treatments Section */}
        <section className="medications-page__section">
          <div className="medications-page__section-header">
            <h2 className="medications-page__section-title">Tratamentos Ativos</h2>
            <span className="medications-page__active-badge">
              <span className="medications-page__active-dot"></span>
              {activeCount} Ativos
            </span>
          </div>

          <div className="medications-page__list">
            {treatments.map(t => (
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
                        onClick={() => handleMarkAsTaken(t.id)}
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

        {/* History Section */}
        <section className="medications-page__section">
          <h2 className="medications-page__section-title">Histórico Recente</h2>
          <div className="medications-page__history-card">
            {history.map(item => (
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
};
export { MedicationsPageContent as MedicationsPage };
