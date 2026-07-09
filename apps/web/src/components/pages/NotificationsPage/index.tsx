import React, { useEffect, useState } from 'react';
import { useNotifications } from '../../../application/notification/useNotifications';
import { Button } from '../../atoms/Button';
import './styles.css';

export const NotificationsPageContent: React.FC = () => {
  const { notifications, isLoading, error, fetchNotifications, markAsRead, markAllAsRead } =
    useNotifications();
  const [filter, setFilter] = useState<'all' | 'unread'>('unread');
  const [page, setPage] = useState(0);

  useEffect(() => {
    fetchNotifications(0);
    setPage(0);
  }, [fetchNotifications]);

  const handleLoadMore = () => {
    const nextPage = page + 1;
    fetchNotifications(nextPage);
    setPage(nextPage);
  };

  const handleFilterChange = (newFilter: 'all' | 'unread') => {
    setFilter(newFilter);
  };

  const filteredNotifications = notifications.filter(n => {
    if (filter === 'unread') return !n.read;
    return true;
  });

  const getEventIcon = (type: string) => {
    switch (type) {
      case 'VACCINATION_DUE':
        return 'vaccines';
      case 'MEDICATION_DOSE':
      case 'MEDICATION_LATE':
        return 'medication';
      case 'CONSULTATION_FOLLOWUP':
        return 'medical_services';
      case 'GROOMING_DUE':
        return 'content_cut';
      case 'PET_BIRTHDAY':
        return 'cake';
      default:
        return 'notifications';
    }
  };

  return (
    <div className="notifications-page">
      <div className="notifications-page__header">
        <h2 className="notifications-page__title">Central de Notificações</h2>
        {notifications.some(n => !n.read) && (
          <button className="notifications-page__read-all-btn" onClick={markAllAsRead}>
            Marcar todas como lidas
          </button>
        )}
      </div>

      <div className="notifications-page__filters">
        <button
          className={`notifications-page__filter-btn ${filter === 'unread' ? 'active' : ''}`}
          onClick={() => handleFilterChange('unread')}
        >
          Não lidas
        </button>
        <button
          className={`notifications-page__filter-btn ${filter === 'all' ? 'active' : ''}`}
          onClick={() => handleFilterChange('all')}
        >
          Todas
        </button>
      </div>

      {error && <div className="notifications-page__error">{error}</div>}

      <div className="notifications-page__list">
        {filteredNotifications.length === 0 ? (
          <div className="notifications-page__empty">
            <span className="material-symbols-outlined">notifications_off</span>
            <p>Nenhuma notificação encontrada.</p>
          </div>
        ) : (
          filteredNotifications.map(n => (
            <div
              key={n.id}
              className={`notifications-page__item ${n.read ? 'read' : 'unread'}`}
              onClick={() => !n.read && markAsRead(n.id)}
            >
              <div className="notifications-page__item-icon">
                <span className="material-symbols-outlined">{getEventIcon(n.type)}</span>
              </div>
              <div className="notifications-page__item-content">
                <h4 className="notifications-page__item-title">{n.title}</h4>
                <p className="notifications-page__item-body">{n.body}</p>
                <span className="notifications-page__item-date">
                  {new Date(n.createdAt).toLocaleDateString('pt-BR', {
                    day: '2-digit',
                    month: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                  })}
                </span>
              </div>
              {!n.read && (
                <button
                  className="notifications-page__item-action"
                  onClick={(e) => {
                    e.stopPropagation();
                    markAsRead(n.id);
                  }}
                  title="Marcar como lida"
                >
                  <span className="material-symbols-outlined">check_circle</span>
                </button>
              )}
            </div>
          ))
        )}
      </div>

      {notifications.length > 0 && notifications.length % 20 === 0 && (
        <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
          <Button
            variant="secondary"
            onClick={handleLoadMore}
            isLoading={isLoading}
            style={{ width: 'auto' }}
          >
            Carregar Mais
          </Button>
        </div>
      )}
    </div>
  );
};
