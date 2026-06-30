import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../molecules/Toast';
import './styles.css';

interface Activity {
  id: string;
  title: string;
  time: string;
  description: string;
  status: 'completed' | 'pending' | 'scheduled';
  type: 'walk' | 'medication' | 'grooming';
}

export const RoutinePageContent: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [selectedDay, setSelectedDay] = useState<number>(12);
  const [activities, setActivities] = useState<Activity[]>([
    {
      id: 'act-1',
      title: 'Passeio matinal',
      time: '07:30',
      description: 'Parque da cidade. Não esquecer a coleira verde.',
      status: 'completed',
      type: 'walk'
    },
    {
      id: 'act-2',
      title: 'Administração de colírio',
      time: '14:00',
      description: 'Olho direito. 2 gotas.',
      status: 'pending',
      type: 'medication'
    },
    {
      id: 'act-3',
      title: 'Banho',
      time: '18:00',
      description: 'Pet Shop Amigo Fiel.',
      status: 'scheduled',
      type: 'grooming'
    }
  ]);

  const handleDaySelect = (day: number) => {
    setSelectedDay(day);
    // Simulate updating activities when date changes
    if (day === 12) {
      setActivities([
        {
          id: 'act-1',
          title: 'Passeio matinal',
          time: '07:30',
          description: 'Parque da cidade. Não esquecer a coleira verde.',
          status: 'completed',
          type: 'walk'
        },
        {
          id: 'act-2',
          title: 'Administração de colírio',
          time: '14:00',
          description: 'Olho direito. 2 gotas.',
          status: 'pending',
          type: 'medication'
        },
        {
          id: 'act-3',
          title: 'Banho',
          time: '18:00',
          description: 'Pet Shop Amigo Fiel.',
          status: 'scheduled',
          type: 'grooming'
        }
      ]);
    } else {
      // Return empty or different activities for other days
      setActivities([
        {
          id: 'act-auto',
          title: 'Alimentação e Água',
          time: '08:00',
          description: 'Ração seca premium 100g.',
          status: 'completed',
          type: 'walk'
        }
      ]);
    }
  };

  const handleToggleStatus = (id: string) => {
    setActivities(prev => prev.map(act => {
      if (act.id === id) {
        const nextStatus = act.status === 'pending' ? 'completed' : 'pending';
        if (nextStatus === 'completed') {
          showToast('Atividade concluída com sucesso! 🎉', 'success');
        }
        return {
          ...act,
          status: nextStatus
        };
      }
      return act;
    }));
  };

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
                onClick={() => showToast('Mudança de mês em breve!', 'info')}
                aria-label="Mês anterior"
              >
                <span className="material-symbols-outlined">chevron_left</span>
              </button>
              <span className="routine-page__month-label">Outubro 2023</span>
              <button 
                className="routine-page__month-btn" 
                onClick={() => showToast('Mudança de mês em breve!', 'info')}
                aria-label="Próximo mês"
              >
                <span className="material-symbols-outlined">chevron_right</span>
              </button>
            </div>

            {/* Weekdays */}
            <div className="routine-page__weekdays">
              <span>D</span>
              <span>S</span>
              <span>T</span>
              <span>Q</span>
              <span>Q</span>
              <span>S</span>
              <span>S</span>
            </div>

            {/* Days Grid */}
            <div className="routine-page__days-grid">
              {/* Alignment spacers (October 2023 starts on Sunday but let's spacer for alignment) */}
              <div className="routine-page__day-spacer"></div>
              <div className="routine-page__day-spacer"></div>
              
              {Array.from({ length: 31 }, (_, i) => i + 1).map(day => {
                const hasMedication = day === 3;
                const hasWalk = day === 9;
                const hasGrooming = day === 15;
                const isSelected = day === selectedDay;

                return (
                  <button
                    key={day}
                    className={`routine-page__day-btn ${isSelected ? 'active' : ''}`}
                    onClick={() => handleDaySelect(day)}
                  >
                    {day}
                    {hasMedication && <div className="routine-page__day-dot bg-secondary"></div>}
                    {hasWalk && <div className="routine-page__day-dot bg-primary"></div>}
                    {hasGrooming && <div className="routine-page__day-dot bg-tertiary"></div>}
                  </button>
                );
              })}
            </div>
          </div>
        </aside>

        {/* Right Column: Activities List */}
        <section className="routine-page__activities-column">
          <div className="routine-page__date-header">
            <h3 className="routine-page__date-title">{selectedDay} de Outubro, Quinta</h3>
            <span className="routine-page__badge">
              {activities.length} Atividades
            </span>
          </div>

          <div className="routine-page__activities-list">
            {activities.map(act => (
              <div 
                key={act.id} 
                className={`routine-page__activity-item routine-page__activity-item--${act.status} border-l-${
                  act.type === 'walk' ? 'tertiary' : act.type === 'medication' ? 'error' : 'secondary'
                }`}
                onClick={() => act.status === 'pending' && handleToggleStatus(act.id)}
                style={{ cursor: act.status === 'pending' ? 'pointer' : 'default' }}
              >
                <div className={`routine-page__activity-icon-wrapper routine-page__activity-icon-wrapper--${act.type}`}>
                  <span className="material-symbols-outlined">
                    {act.type === 'walk' ? 'directions_walk' : act.type === 'medication' ? 'medication' : 'shower'}
                  </span>
                </div>
                <div className="routine-page__activity-info">
                  <div className="routine-page__activity-top">
                    <h4 className="routine-page__activity-title">{act.title}</h4>
                    <span className="routine-page__activity-time">{act.time}</span>
                  </div>
                  <p className="routine-page__activity-desc">{act.description}</p>
                  
                  {/* Status chip */}
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
            ))}
          </div>

          {/* Action Buttons */}
          <div className="routine-page__actions-grid">
            <button 
              className="routine-page__action-card group"
              onClick={() => navigate('/appointments')}
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
              onClick={() => showToast('Funcionalidade de planejar atividade em breve!', 'info')}
            >
              <div className="routine-page__action-icon-wrapper bg-secondary-container text-on-secondary-container">
                <span className="material-symbols-outlined">event_repeat</span>
              </div>
              <div className="routine-page__action-info-group">
                <span className="routine-page__action-title-label">Planejar Atividade Recorrente</span>
                <span className="routine-page__action-desc-label">Configurar alertas semanais ou diários.</span>
              </div>
            </button>
          </div>
        </section>
      </div>
    </div>
  );
};
export { RoutinePageContent as RoutinePage };
