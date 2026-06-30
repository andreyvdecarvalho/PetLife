import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { ProfileForm } from '../components/organisms/ProfileForm';

export const ProfilePage: React.FC = () => {
  return (
    <DashboardLayout>
      <ProfileForm />
    </DashboardLayout>
  );
};
