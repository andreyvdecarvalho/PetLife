import React, { useState } from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { ProfileForm } from '../components/organisms/ProfileForm';
import { NotificationPreferencesForm } from '../components/organisms/NotificationPreferencesForm';
import './ProfilePage.css';

export const ProfilePage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'profile' | 'notifications'>('profile');

  return (
    <DashboardLayout>
      <div className="profile-page-container">
        <div className="profile-page-tabs">
          <button
            onClick={() => setActiveTab('profile')}
            className={`profile-page-tab ${activeTab === 'profile' ? 'profile-page-tab--active' : ''}`}
          >
            Meu Perfil
          </button>
          <button
            onClick={() => setActiveTab('notifications')}
            className={`profile-page-tab ${activeTab === 'notifications' ? 'profile-page-tab--active' : ''}`}
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

