import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { ProfilePage } from './ProfilePage';

// Mock the components used inside ProfilePage
vi.mock('../components/templates/DashboardLayout', () => ({
  DashboardLayout: ({ children }: { children: React.ReactNode }) => <div data-testid="dashboard-layout">{children}</div>,
}));

vi.mock('../components/organisms/ProfileForm', () => ({
  ProfileForm: () => <div data-testid="profile-form">Profile Form Content</div>,
}));

vi.mock('../components/organisms/NotificationPreferencesForm', () => ({
  NotificationPreferencesForm: () => <div data-testid="notification-preferences-form">Notification Preferences Content</div>,
}));

describe('ProfilePage', () => {
  it('should render DashboardLayout and initial ProfileForm', () => {
    render(
      <MemoryRouter>
        <ProfilePage />
      </MemoryRouter>
    );

    expect(screen.getByTestId('dashboard-layout')).toBeInTheDocument();
    expect(screen.getByTestId('profile-form')).toBeInTheDocument();
    
    // Tab text should be visible
    expect(screen.getByText('Meu Perfil')).toBeInTheDocument();
    expect(screen.getByText('Notificações')).toBeInTheDocument();
  });

  it('should switch to Notifications tab on click', () => {
    render(
      <MemoryRouter>
        <ProfilePage />
      </MemoryRouter>
    );

    const notificationsTab = screen.getByText('Notificações');
    fireEvent.click(notificationsTab);

    expect(screen.getByTestId('notification-preferences-form')).toBeInTheDocument();
    expect(screen.getByText('Notification Preferences Content')).toBeInTheDocument();
    
    // Profile form should not be present
    expect(screen.queryByTestId('profile-form')).not.toBeInTheDocument();
  });
});
