import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout/DashboardLayout';
import { ProfileForm } from '../components/organisms/ProfileForm/ProfileForm';

export const ProfilePage: React.FC = () => {
  return (
    <DashboardLayout>
      <ProfileForm />
    </DashboardLayout>
  );
};
