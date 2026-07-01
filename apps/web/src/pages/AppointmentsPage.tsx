import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { AppointmentsPage as AppointmentsPageContent } from '../components/pages/AppointmentsPage';

export const AppointmentsPage: React.FC = () => {
  return (
    <DashboardLayout>
      <AppointmentsPageContent />
    </DashboardLayout>
  );
};
