import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { MedicationsPage as MedicationsPageContent } from '../components/pages/MedicationsPage';

export const MedicationsPage: React.FC = () => {
  return (
    <DashboardLayout>
      <MedicationsPageContent />
    </DashboardLayout>
  );
};
