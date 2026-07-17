import React, { useState } from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { ProfileForm } from '../components/organisms/ProfileForm';
import { NotificationPreferencesForm } from '../components/organisms/NotificationPreferencesForm';

export const ProfilePage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'profile' | 'notifications'>('profile');

  return (
    <DashboardLayout>
      <div style={{ maxWidth: '600px', margin: '0 auto', marginBottom: '24px' }}>
        <div
          style={{
            display: 'flex',
            gap: '12px',
            borderBottom: '1px solid var(--color-outline-variant)',
            paddingBottom: '8px',
          }}
        >
          <button
            onClick={() => setActiveTab('profile')}
            style={{
              background: 'none',
              border: 'none',
              color:
                activeTab === 'profile'
                  ? 'var(--color-primary)'
                  : 'var(--color-on-surface-variant)',
              fontWeight: activeTab === 'profile' ? 600 : 500,
              cursor: 'pointer',
              fontSize: 'var(--text-headline-md-size)',
              fontFamily: 'var(--font-headline)',
              padding: '8px 16px',
              borderBottom: activeTab === 'profile' ? '2px solid var(--color-primary)' : 'none',
            }}
          >
            Meu Perfil
          </button>
          <button
            onClick={() => setActiveTab('notifications')}
            style={{
              background: 'none',
              border: 'none',
              color:
                activeTab === 'notifications'
                  ? 'var(--color-primary)'
                  : 'var(--color-on-surface-variant)',
              fontWeight: activeTab === 'notifications' ? 600 : 500,
              cursor: 'pointer',
              fontSize: 'var(--text-headline-md-size)',
              fontFamily: 'var(--font-headline)',
              padding: '8px 16px',
              borderBottom:
                activeTab === 'notifications' ? '2px solid var(--color-primary)' : 'none',
            }}
          >
            Notificações
          </button>
        </div>
      </div>
      {activeTab === 'profile' ? (
        <ProfileForm />
      ) : (
        <div className="profile-container">
          <h2 className="profile-title">Preferências de Notificação</h2>
          <NotificationPreferencesForm />
        </div>
      )}
    </DashboardLayout>
  );
};

