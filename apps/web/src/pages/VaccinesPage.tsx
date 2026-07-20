import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { VaccinesPage as VaccinesPageContent } from '../components/pages/VaccinesPage';

export const VaccinesPage: React.FC = () => {
  return (
    <DashboardLayout>
      <VaccinesPageContent />
    </DashboardLayout>
  );
};
