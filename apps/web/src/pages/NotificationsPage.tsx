import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { NotificationsPageContent } from '../components/pages/NotificationsPage';

export const NotificationsPage: React.FC = () => {
  return (
    <DashboardLayout>
      <NotificationsPageContent />
    </DashboardLayout>
  );
};
